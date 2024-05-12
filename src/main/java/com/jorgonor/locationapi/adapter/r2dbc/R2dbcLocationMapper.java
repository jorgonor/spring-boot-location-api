package com.jorgonor.locationapi.adapter.r2dbc;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import io.r2dbc.spi.Readable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * R2dbc location entities mapper
 *
 * @author jorgonor
 */
@Component
public class R2dbcLocationMapper {

    public Location.LocationBuilder map(Readable readable) {
        long id = R2dbcUtils.getNotNullInt(readable, "id");
        String name = readable.get("name", String.class);
        String description = readable.get("description", String.class);
        Instant createdAt = readable.get("created_at", Instant.class);
        Instant modifiedAt = readable.get("modified_at", Instant.class);
        double latitude = R2dbcUtils.getNotNullDouble(readable,"latitude");
        double longitude = R2dbcUtils.getNotNullDouble(readable, "longitude");

        return Location.builder()
                .id(new LocationId(id))
                .name(name)
                .description(description)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .latitude(latitude)
                .longitude(longitude);
    }

    public String mapLocationTag(Readable readable) {
        return readable.get("tag", String.class);
    }

    public LocationId mapLocationId(Map<String, Object> resultMap) {
        long newLocationId = (long) resultMap.get("id");
        return new LocationId(newLocationId);
    }
}
