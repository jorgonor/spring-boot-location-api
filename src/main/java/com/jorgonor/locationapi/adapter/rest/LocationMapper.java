package com.jorgonor.locationapi.adapter.rest;

import com.jorgonor.locationapi.adapter.rest.api.LocationDTO;
import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    LocationDTO map(Location location) {
        return LocationDTO.builder()
            .id(location.getId().getId())
            .name(location.getName())
            .description(location.getDescription())
            .createdAt(location.getCreatedAt())
            .modifiedAt(location.getModifiedAt())
            .longitude(location.getLongitude())
            .latitude(location.getLatitude())
            .tags(location.getTags())
            .build();
    }

    Location from(LocationDTO locationDTO) {
        Location.LocationBuilder locationBuilder = Location.builder();

        Long locationId = locationDTO.getId();

        if (locationId != null) {
            locationBuilder.id(new LocationId(locationId));
        }

        locationBuilder.name(locationDTO.getName())
            .description(locationDTO.getDescription())
            .createdAt(locationDTO.getCreatedAt())
            .modifiedAt(locationDTO.getModifiedAt())
            .longitude(locationDTO.getLongitude())
            .latitude(locationDTO.getLatitude())
            .tags(locationDTO.getTags());

        return locationBuilder.build();
    }


}
