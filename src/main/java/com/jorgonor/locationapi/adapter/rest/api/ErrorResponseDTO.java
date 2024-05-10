package com.jorgonor.locationapi.adapter.rest.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponseDTO {

    public static final int DATA_ERROR = 100;
    public static final int UNEXPECTED_ERROR = 500;

    int err;
    String message;
}
