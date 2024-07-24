package com.jorgonor.locationapi.db.integration;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.exception.EntityNotFoundException;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class PersistenceIntegrationTest {

    @Autowired
    LocationRepository locationRepository;

    @Test
    void testLocationDoesNotExist() {
        LocationId locationId = new LocationId(1);

        Assertions.assertThatThrownBy(() -> locationRepository.get(locationId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testLocationSave() {
        createSampleLocationAndCheckReadEntityIsEqual();

    }

    @Test
    void testLocationUpdate() {
        Location readLocation = createSampleLocationAndCheckReadEntityIsEqual();

        List<String> updatedTags = List.of("soccer", "stadium", "la-liga-ea");

        Location updatedLocation = locationRepository.update(readLocation.getId(), readLocation.toBuilder()
                .name("Estadio de la Cerámica")
                .description("Villarreal CF Soccer Stadium Rebranded")
                .tags(updatedTags)
                .createdAt(null)
                .modifiedAt(null)
                .build());

        Location rereadLocation = locationRepository.get(readLocation.getId());

        Assertions.assertThat(updatedLocation.getName()).isEqualTo("Estadio de la Cerámica");
        Assertions.assertThat(updatedLocation.getDescription()).isEqualTo("Villarreal CF Soccer Stadium Rebranded");
        Assertions.assertThat(updatedLocation.getTags()).hasSameElementsAs(updatedTags);

        Assertions.assertThat(rereadLocation).isEqualTo(updatedLocation);

    }

    protected Location createSampleLocationAndCheckReadEntityIsEqual() {
        List<String> tags = List.of("soccer", "stadium");

        Location savedLocation = locationRepository.save(sampleLocationBuilder().build());

        Location readLocation = locationRepository.get(savedLocation.getId());

        Assertions.assertThat(readLocation.getName()).isEqualTo("Estadio El Madrigal");
        Assertions.assertThat(readLocation.getDescription()).isEqualTo("Villarreal CF Soccer Stadium");
        Assertions.assertThat(readLocation.getTags()).hasSameElementsAs(tags);
        Assertions.assertThat(readLocation.getLatitude()).isEqualTo(39.9438899);
        Assertions.assertThat(readLocation.getLongitude()).isEqualTo(0.105713);
        Assertions.assertThat(readLocation.getCreatedAt()).isNotNull();
        Assertions.assertThat(readLocation.getModifiedAt()).isNotNull();

        Assertions.assertThat(readLocation).isEqualTo(savedLocation);

        return readLocation;
    }

    protected static Location.LocationBuilder sampleLocationBuilder() {
        return Location.builder()
                .name("Estadio El Madrigal")
                .description("Villarreal CF Soccer Stadium")
                .tags(List.of("soccer", "stadium"))
                .latitude(39.9438899)
                .longitude(0.105713);
    }
}
