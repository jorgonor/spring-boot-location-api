package com.jorgonor.locationapi.adapter.r2dbc;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class R2dbcLocationRepository implements LocationRepository {

    private static final String BASE_QUERY_LOCATION = "SELECT id, name, description, created_at, modified_at, latitude, longitude FROM location ";
    private static final String QUERY_LOCATION_BY_ID = BASE_QUERY_LOCATION + " WHERE id = :id";
    private static final String QUERY_LOCATION_TAGS_BY_ID = "SELECT tag from location_tag where location_id = :id ORDER BY tag";

    private static final String QUERY_INSERT_LOCATION = "INSERT INTO location(name, description, created_at, modified_at, latitude, longitude)"
            + " VALUES (:name, :description, NOW(), NOW(), :latitude, :longitude) RETURNING ID";

    private static final String QUERY_BATCH_INSERT_LOCATION_TAG = "INSERT INTO location_tag(location_id, tag) VALUES($1, $2)";

    private static final String QUERY_UPDATE_LOCATION = "UPDATE location "
            + " SET name = :name, description = :description, modified_at = NOW(), latitude = :latitude, longitude = :longitude "
            + " WHERE id = :id";

    private static final String QUERY_EXISTS_LOCATION = "select 1 from location where id = :id";

    private static final String QUERY_DELETE_LOCATION = "DELETE FROM location WHERE id = :id";
    private static final String QUERY_DELETE_LOCATION_TAG_BY_LOCATION_ID = "DELETE FROM location_tag WHERE location_id = :location_id";
    private static final String QUERY_BATCH_DELETE_LOCATION_TAG_BY_LOCATION_ID_AND_TAG = "DELETE FROM location_tag WHERE location_id = $1 AND tag = $2";

    private final DatabaseClient databaseClient;
    private final R2dbcLocationMapper rd2dbcLocationMapper;

    @Transactional(readOnly = true)
    @Override
    public Mono<Location> get(LocationId id) {
        return getLocationBuilder(id)
            .flatMap(locationBuilder -> addLocationTagsAndBuildLocationBuilder(id, locationBuilder));
    }

    @Transactional
    @Override
    public Mono<Location> save(Location location) {
        return bindLocation(databaseClient.sql(QUERY_INSERT_LOCATION), location)
            .fetch()
            .one()
            .flatMap(result -> {
                LocationId locationId = rd2dbcLocationMapper.mapLocationId(result);
                return batchInsertLocationTags(locationId, location.getTags());
            })
            .map(locationId -> location.toBuilder().id(locationId).build());
    }

    @Transactional
    @Override
    public Mono<Location> update(LocationId id, Location location) {
        return existsLocation(id)
            .flatMap(v -> bindLocation(databaseClient.sql(QUERY_UPDATE_LOCATION), location)
                .bind("id", id.getId())
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Updated {} rows for location {}.", rowsUpdated, id.getId());
                    }

                    return updateTags(id, location.getTags());
                }))
            .map(locationId -> location.toBuilder().id(id).build());
    }

    @Transactional
    @Override
    public Mono<Boolean> delete(LocationId id) {
        return databaseClient.sql(QUERY_DELETE_LOCATION_TAG_BY_LOCATION_ID)
            .bind("location_id", id.getId())
            .fetch()
            .rowsUpdated()
            .flatMap(rowsDeleted ->
                databaseClient.sql(QUERY_DELETE_LOCATION)
                    .bind("id", id.getId())
                    .fetch()
                    .rowsUpdated()
            )
            .map(v -> v == 1);
    }

    private Mono<Location.LocationBuilder> getLocationBuilder(LocationId id) {
        return databaseClient.sql(QUERY_LOCATION_BY_ID)
            .bind("id", id.getId())
            .map(rd2dbcLocationMapper::map)
            .one();
    }

    private Mono<Location> addLocationTagsAndBuildLocationBuilder(LocationId id, Location.LocationBuilder locationBuilder) {
        return getLocationTags(id)
            .map(locationTags -> {
                locationBuilder.tags(locationTags);
                return locationBuilder.build();
            });
    }

    private Mono<List<String>> getLocationTags(LocationId id) {
        return databaseClient.sql(QUERY_LOCATION_TAGS_BY_ID)
            .bind("id", id.getId())
            .map(rd2dbcLocationMapper::mapLocationTag)
            .all()
            .collect(Collectors.toList());
    }

    private Mono<LocationId> batchInsertLocationTags(LocationId locationId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Mono.just(locationId);
        }

        return databaseClient.inConnectionMany(connection -> {
            Statement statement = connection.createStatement(QUERY_BATCH_INSERT_LOCATION_TAG);
            long locationIdValue = locationId.getId();
            String tag = tags.getFirst();

            statement.bind("$1", locationIdValue)
                    .bind("$2", tag);

            for (int i = 1; i < tags.size(); i++) {
                tag = tags.get(i);
                statement.add().bind("$1", locationIdValue)
                        .bind("$2", tag);
            }

            return Flux.from(statement.execute())
                .flatMap(result -> result.map((row, rowMetadata) -> locationId));
        })
        .reduce(locationId, (a, v) -> locationId);
    }

    private Mono<LocationId> batchDeleteLocationTags(LocationId locationId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Mono.just(locationId);
        }

        return databaseClient.inConnectionMany(connection -> {
            Statement statement = connection.createStatement(QUERY_BATCH_DELETE_LOCATION_TAG_BY_LOCATION_ID_AND_TAG);
            long locationIdValue = locationId.getId();
            String tag = tags.getFirst();

            statement.bind("$1", locationIdValue)
                    .bind("$2", tag);

            for (int i = 1; i < tags.size(); i++) {
                tag = tags.get(i);
                statement.add().bind("$1", locationIdValue)
                    .bind("$2", tag);
            }

            return Flux.from(statement.execute())
                    .flatMap(result -> result.map((row, rowMetadata) -> locationId));
        })
        .reduce(locationId, (a, v) -> locationId);
    }

    private Mono<Boolean> existsLocation(LocationId locationId) {
        return databaseClient.sql(QUERY_EXISTS_LOCATION)
            .bind("id", locationId.getId())
            .map((row, rowMetadata) -> R2dbcUtils.getNotNullInt(row, 0) == 1)
            .one()
            .filter(v -> v);
    }

    private Mono<LocationId> updateTags(LocationId locationId, List<String> tags) {
        final List<String> internalTagsList = tags != null ? tags : Collections.emptyList();

        return getCurrentTags(locationId)
            .flatMap(currentTags -> {
                List<String> tagsToDelete = currentTags.stream().filter(tag -> !internalTagsList.contains(tag)).toList();
                List<String> tagsToAdd = internalTagsList.stream().filter(tag -> !currentTags.contains(tag)).toList();

                return batchInsertLocationTags(locationId, tagsToAdd)
                    .flatMap(v -> batchDeleteLocationTags(locationId, tagsToDelete));
            });
    }

    private Mono<List<String>> getCurrentTags(LocationId locationId) {
        return databaseClient.sql(QUERY_LOCATION_TAGS_BY_ID)
            .bind("id", locationId.getId())
            .map((row, rowMetadata) -> row.get("tag", String.class))
            .all()
            .collect(Collectors.toList());
    }

    private DatabaseClient.GenericExecuteSpec bindLocation(DatabaseClient.GenericExecuteSpec genericExecuteSpec, Location location) {
        DatabaseClient.GenericExecuteSpec result;

        result = genericExecuteSpec.bind("name", location.getName())
            .bind("latitude", location.getLatitude())
            .bind("longitude", location.getLongitude());

        result = R2dbcUtils.nullableBind(result, "description", location.getDescription());

        return result;
    }

}
