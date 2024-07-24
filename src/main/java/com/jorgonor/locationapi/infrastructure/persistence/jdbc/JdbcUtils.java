package com.jorgonor.locationapi.infrastructure.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

class JdbcUtils {

    private JdbcUtils() { }

    static Instant getInstantFromColumnLabel(ResultSet resultSet, String columnLabel) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnLabel);
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }
}
