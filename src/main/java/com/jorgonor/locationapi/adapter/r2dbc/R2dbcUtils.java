package com.jorgonor.locationapi.adapter.r2dbc;

import io.r2dbc.spi.Readable;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * R2dbc utility methods
 * @author jorgonor
 */
public final class R2dbcUtils {

    private R2dbcUtils() {
    }

    static int getNotNullInt(Readable readable, int index) {
        return readable.get(index, Integer.class);
    }

    static int getNotNullInt(Readable readable, String name) {
        return readable.get(name, Integer.class);
    }

    static double getNotNullDouble(Readable readable, String name) {
        return readable.get(name, Double.class);
    }

    static DatabaseClient.GenericExecuteSpec nullableBind(DatabaseClient.GenericExecuteSpec genericExecuteSpec,
        String name, String value) {
        final DatabaseClient.GenericExecuteSpec result;

        if (value != null) {
            result = genericExecuteSpec.bind(name, value);
        } else {
            result = genericExecuteSpec.bindNull(name, String.class);
        }

        return result;
    }
}
