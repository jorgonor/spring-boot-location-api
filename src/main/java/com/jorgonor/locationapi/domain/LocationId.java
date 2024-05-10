package com.jorgonor.locationapi.domain;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Location model identifier
 *
 * @author jorgonor
 */
@Value
@RequiredArgsConstructor
public class LocationId {
    long id;
}
