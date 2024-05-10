package com.jorgonor.locationapi.adapter.rest.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@Builder
public class LocationDTO {
    Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Instant modifiedAt;
    double latitude;
    double longitude;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> tags;
}
