package com.jorgonor.locationapi.adapter.jdbc;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.exception.EntityNotFoundException;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@Profile("postgres")
@RequiredArgsConstructor
public class JdbcLocationRepository implements LocationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String QUERY_EXISTS_LOCATION = "select 1 from location where id = :id";
    private static final String BASE_QUERY_LOCATION = "SELECT id, name, description, created_at, modified_at, latitude, longitude FROM location ";

    private static final String QUERY_LOCATION_BY_ID = BASE_QUERY_LOCATION + " WHERE id = :id";

    private static final String QUERY_LOCATION_TAGS_BY_ID = "SELECT tag from location_tag where location_id = :location_id ORDER BY tag";

    private static final String QUERY_INSERT_LOCATION = "INSERT INTO location(name, description, created_at, modified_at, latitude, longitude)"
        + " VALUES (:name, :description, NOW(), NOW(), :latitude, :longitude) RETURNING id, created_at, modified_at";

    private static final String QUERY_BATCH_INSERT_LOCATION_TAG = "INSERT INTO location_tag(location_id, tag) VALUES(?, ?)";

    private static final String QUERY_UPDATE_LOCATION = "UPDATE location "
        + " SET name = :name, description = :description, modified_at = NOW(), latitude = :latitude, longitude = :longitude "
        + " WHERE id = :location_id";

    private static final String QUERY_DELETE_LOCATION = "DELETE FROM location WHERE id = :location_id";
    private static final String QUERY_DELETE_LOCATION_TAG_BY_LOCATION_ID = "DELETE FROM location_tag WHERE location_id = :location_id";
    private static final String QUERY_BATCH_DELETE_LOCATION_TAG_BY_LOCATION_ID_AND_TAG = "DELETE FROM location_tag WHERE location_id = ? AND tag = ?";

    private static final String LOCATION_ID_EXPRESSION = "location_id";

    @Override
    @Transactional(readOnly = true)
    public Location get(LocationId id) {

        Location.LocationBuilder locationBuilder;

        try {
            locationBuilder = namedParameterJdbcTemplate.queryForObject(QUERY_LOCATION_BY_ID, Map.of("id", id.getId()),
                new JdbcLocationRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Entity with id " + id + " not found", e);
        }

        List<String> tags = getLocationTags(id);
        locationBuilder.tags(tags);

        return locationBuilder.build();
    }

    @Override
    @Transactional
    public Location save(Location location) {
        MapSqlParameterSource mapSqlParameterSource = createLocationMapSqlParameterSource(location);

        Location.LocationBuilder locationBuilder = location.toBuilder();

        DataHolder<Long> locationIdDataHolder = new DataHolder<>();

        namedParameterJdbcTemplate.query(QUERY_INSERT_LOCATION, mapSqlParameterSource, rs -> {
            long locationIdValue = rs.getLong("id");
            Instant createdAt = JdbcUtils.getInstantFromColumnLabel(rs, "created_at");
            Instant modifiedAt = JdbcUtils.getInstantFromColumnLabel(rs, "modified_at");

            locationIdDataHolder.setValue(locationIdValue);
            LocationId locationId = new LocationId(locationIdValue);

            locationBuilder.id(locationId)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt);
        });

        if (location.getTags() != null && !location.getTags().isEmpty()) {
            batchInsertTags(locationIdDataHolder.getValue(), location.getTags());
        }

        return locationBuilder.build();
    }


    @Override
    @Transactional
    public Location update(LocationId id, Location location) {
        DataHolder<Boolean> dataHolder = new DataHolder<>(Boolean.FALSE);

        namedParameterJdbcTemplate.query(QUERY_EXISTS_LOCATION, Map.of("id", id.getId()), (RowCallbackHandler) rs ->
            dataHolder.setValue(Boolean.TRUE)
        );

        if (Boolean.FALSE.equals(dataHolder.getValue())) {
            throw new EntityNotFoundException("Entity with id " + id.toString() + " not found");
        }

        List<String> newTags = location.getTags() != null ? location.getTags() : Collections.emptyList();
        List<String> currentTags = getLocationTags(id);

        List<String> tagsToDelete = currentTags.stream()
            .filter(existingTag -> !newTags.contains(existingTag))
            .toList();

        List<String> tagsToInsert = newTags.stream()
            .filter(newTag -> !currentTags.contains(newTag))
            .toList();

        MapSqlParameterSource mapSqlParameterSource = createLocationMapSqlParameterSource(location);
        mapSqlParameterSource.addValue(LOCATION_ID_EXPRESSION, id.getId());
        namedParameterJdbcTemplate.update(QUERY_UPDATE_LOCATION, mapSqlParameterSource);

        if (!tagsToInsert.isEmpty()) {
            batchInsertTags(id.getId(), tagsToInsert);
        }

        if (!tagsToDelete.isEmpty()) {
            batchDeleteTags(id.getId(), tagsToDelete);
        }

        return get(id);
    }

    @Override
    @Transactional
    public boolean delete(LocationId id) {
        Map<String, Long> params = Map.of(LOCATION_ID_EXPRESSION, id.getId());

        namedParameterJdbcTemplate.update(QUERY_DELETE_LOCATION_TAG_BY_LOCATION_ID, params);
        int affectedRows = namedParameterJdbcTemplate.update(QUERY_DELETE_LOCATION, params);

        return affectedRows > 0;
    }

    private List<String> getLocationTags(LocationId locationId) {

        List<String> tags = new ArrayList<>();

        namedParameterJdbcTemplate.query(QUERY_LOCATION_TAGS_BY_ID, Map.of(LOCATION_ID_EXPRESSION, locationId.getId()),
            rs -> {
                String tag = rs.getString("tag");
                tags.add(tag);
            });

        return tags;
    }

    private void batchInsertTags(long locationId, List<String> tags) {
        jdbcTemplate.batchUpdate(QUERY_BATCH_INSERT_LOCATION_TAG, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String tag = tags.get(i);
                ps.setLong(1, locationId);
                ps.setString(2, tag);
            }

            @Override
            public int getBatchSize() {
                return tags.size();
            }
        });
    }

    private void batchDeleteTags(long locationId, List<String> tags) {
        jdbcTemplate.batchUpdate(QUERY_BATCH_DELETE_LOCATION_TAG_BY_LOCATION_ID_AND_TAG, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String tag = tags.get(i);
                ps.setLong(1, locationId);
                ps.setString(2, tag);
            }

            @Override
            public int getBatchSize() {
                return tags.size();
            }
        });
    }

    private static MapSqlParameterSource    createLocationMapSqlParameterSource(Location location) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        mapSqlParameterSource.addValue("name", location.getName());
        mapSqlParameterSource.addValue("description", location.getDescription());
        mapSqlParameterSource.addValue("latitude", location.getLatitude());
        mapSqlParameterSource.addValue("longitude", location.getLongitude());
        return mapSqlParameterSource;
    }

}
