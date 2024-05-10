package com.jorgonor.locationapi.domain.repository;

import com.jorgonor.locationapi.domain.Location;
import com.jorgonor.locationapi.domain.LocationId;

/**
 * Location repository interface
 *
 * @author jorgonor
 */
public interface LocationRepository {
    Location get(LocationId id);
    Location save(Location location);
    Location update(LocationId id, Location location);
    boolean delete(LocationId id);
}
