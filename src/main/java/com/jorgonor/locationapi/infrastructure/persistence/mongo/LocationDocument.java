package com.jorgonor.locationapi.infrastructure.persistence.mongo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Mongo location document
 *
 * @author jorgonor
 */
@Data
@Document(collection = "locations")
@Builder(toBuilder = true)
class LocationDocument {
    @Id
    @Indexed(unique = true)
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant modifiedAt;
    private double latitude;
    private double longitude;
    private List<String> tags;
}
