package com.jorgonor.locationapi.adapter.rest;

import com.jorgonor.locationapi.adapter.rest.api.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class LocationControllerAdvice {

    @ExceptionHandler({DataAccessException.class, TransactionException.class })
    public ResponseEntity<ErrorResponseDTO> handleDataAccessException(RuntimeException e) {
        if (e instanceof DataAccessException) {
            log.error("Data access exception", e);
        } else if (e instanceof TransactionException) {
            log.error("Transaction exception", e);
        }

        return ResponseEntity.internalServerError()
                .body(ErrorResponseDTO.builder()
                        .err(ErrorResponseDTO.DATA_ERROR)
                        .message(e.getMessage())
                        .build()
                );
    }


    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<ErrorResponseDTO> handleUnexpectedException(RuntimeException e) {
        log.error("Unexpected runtime exception", e);

        return ResponseEntity.internalServerError()
                .body(ErrorResponseDTO.builder()
                        .err(ErrorResponseDTO.UNEXPECTED_ERROR)
                        .message(e.getMessage())
                        .build()
                );
    }
}
