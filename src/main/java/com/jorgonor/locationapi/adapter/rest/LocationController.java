package com.jorgonor.locationapi.adapter.rest;


import com.jorgonor.locationapi.adapter.rest.api.CreateOrUpdateLocationDTO;
import com.jorgonor.locationapi.adapter.rest.api.LocationDTO;
import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private static final ResponseEntity<Void> VOID_NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> get(@PathVariable long id) {
        Location location = locationRepository.get(new LocationId(id));
        return ResponseEntity.ok(locationMapper.map(location));
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocationDTO> post(@RequestBody CreateOrUpdateLocationDTO newLocationDTO) {
        Location newLocation = locationMapper.from(newLocationDTO);
        Location savedLocation = locationRepository.save(newLocation);

        return ResponseEntity.ok(locationMapper.map(savedLocation));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocationDTO> put(@PathVariable long id, @RequestBody CreateOrUpdateLocationDTO updatedLocationDTO) {
        Location updatedLocation = locationMapper.from(updatedLocationDTO);
        Location savedLocation = locationRepository.update(new LocationId(id), updatedLocation);

        return ResponseEntity.ok(locationMapper.map(savedLocation));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean deleted = locationRepository.delete(new LocationId(id));

        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return VOID_NOT_FOUND_RESPONSE;
        }
    }
}
