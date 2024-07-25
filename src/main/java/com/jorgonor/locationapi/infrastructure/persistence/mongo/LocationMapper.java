package com.jorgonor.locationapi.infrastructure.persistence.mongo;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import org.springframework.stereotype.Component;

@Component("mongoLocationMapper")
public class LocationMapper {

    Location mapToLocation(LocationDocument locationDocument) {
        return Location.builder()
            .id(new LocationId(locationDocument.getId()))
            .name(locationDocument.getName())
            .description(locationDocument.getDescription())
            .createdAt(locationDocument.getCreatedAt())
            .modifiedAt(locationDocument.getModifiedAt())
            .latitude(locationDocument.getLatitude())
            .longitude(locationDocument.getLongitude())
            .tags(locationDocument.getTags())
            .build();
    }

    LocationDocument mapToLocationDocument(Location location) {
        return LocationDocument.builder()
            .id(location.getId() != null ? location.getId().getId() : null)
            .name(location.getName())
            .description(location.getDescription())
            .createdAt(location.getCreatedAt())
            .modifiedAt(location.getModifiedAt())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .tags(location.getTags())
            .build();
    }

    public void remapLocationDocument(LocationDocument locationDocument, Location location) {
        locationDocument.setName(location.getName());
        locationDocument.setDescription(location.getDescription());
        locationDocument.setLatitude(location.getLatitude());
        locationDocument.setLongitude(location.getLongitude());
        locationDocument.setTags(location.getTags());
    }
}
