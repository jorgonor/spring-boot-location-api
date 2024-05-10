package com.jorgonor.locationapi.adapter.jdbc;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcLocationRowMapper implements RowMapper<Location.LocationBuilder> {
    @Override
    public Location.LocationBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Location.builder()
            .id(new LocationId(rs.getLong("id")))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .modifiedAt(rs.getTimestamp("modified_at").toInstant())
            .latitude(rs.getDouble("latitude"))
            .longitude(rs.getDouble("longitude"));
    }
}
