package com.jorgonor.locationapi.adapter.rest;


import com.jorgonor.locationapi.adapter.rest.api.LocationDTO;
import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import com.jorgonor.locationapi.domain.repository.LocationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private static final ResponseEntity<Void> DELETED_RESPONSE = ResponseEntity.noContent().build();
    private static final ResponseEntity<Void> VOID_NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private static final Mono<ResponseEntity<LocationDTO>> MONO_NOT_FOUND_RESPONSE = Mono.just(ResponseEntity.notFound().build());

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<LocationDTO>> get(@PathVariable long id) {
        return locationRepository.get(new LocationId(id))
            .map(this::renderResponse)
            .switchIfEmpty(MONO_NOT_FOUND_RESPONSE);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<LocationDTO>> post(@RequestBody LocationDTO newLocationDTO) {
        Location newLocation = locationMapper.from(newLocationDTO);

        return locationRepository.save(newLocation)
            .map(this::renderResponse);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<LocationDTO>> put(@PathVariable long id, @RequestBody LocationDTO updatedLocationDTO) {
        Location updatedLocation = locationMapper.from(updatedLocationDTO);
        return locationRepository.update(new LocationId(id), updatedLocation)
            .map(this::renderResponse)
            .switchIfEmpty(MONO_NOT_FOUND_RESPONSE);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable long id) {
        return locationRepository.delete(new LocationId(id))
            .map(this::renderDeleteResponse);
    }

    private ResponseEntity<LocationDTO> renderResponse(Location location) {
        return ResponseEntity.ok(locationMapper.map(location));
    }

    private ResponseEntity<Void> renderDeleteResponse(@NonNull Boolean deleted) {
        if (deleted) {
            return DELETED_RESPONSE;
        } else {
            return VOID_NOT_FOUND_RESPONSE;
        }
    }
}
