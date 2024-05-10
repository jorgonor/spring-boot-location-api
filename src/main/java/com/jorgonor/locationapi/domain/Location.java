package com.jorgonor.locationapi.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Location model
 *
 * @author jorgonor
 */
@Value
@Builder(toBuilder = true)
public class Location {
    LocationId id;
    String name;
    String description;
    Instant createdAt;
    Instant modifiedAt;
    double latitude;
    double longitude;
    @Singular
    List<String> tags;
}
