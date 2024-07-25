package com.jorgonor.locationapi.adapter.rest.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@Builder
public class CreateOrUpdateLocationDTO {
    String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;
    double latitude;
    double longitude;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> tags;
}
