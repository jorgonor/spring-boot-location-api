package com.jorgonor.locationapi.domain.repository;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;
import reactor.core.publisher.Mono;

/**
 * Location repository interface
 *
 * @author jorgonor
 */
public interface LocationRepository {
    Mono<Location> get(LocationId id);
    Mono<Location> save(Location location);
    Mono<Location> update(LocationId id, Location location);
    Mono<Boolean> delete(LocationId id);
}
