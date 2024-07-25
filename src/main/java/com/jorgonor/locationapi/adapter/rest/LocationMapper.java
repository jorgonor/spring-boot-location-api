package com.jorgonor.locationapi.adapter.rest;

import com.jorgonor.locationapi.adapter.rest.api.CreateOrUpdateLocationDTO;
import com.jorgonor.locationapi.adapter.rest.api.LocationDTO;
import com.jorgonor.locationapi.domain.Location;
import org.springframework.stereotype.Component;

@Component("restLocationMapper")
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

    Location from(CreateOrUpdateLocationDTO locationDTO) {
        Location.LocationBuilder locationBuilder = Location.builder();

        locationBuilder.name(locationDTO.getName())
            .description(locationDTO.getDescription())
            .longitude(locationDTO.getLongitude())
            .latitude(locationDTO.getLatitude());

        if (locationDTO.getTags() != null) {
            locationBuilder.tags(locationDTO.getTags());
        }

        return locationBuilder.build();
    }


}
