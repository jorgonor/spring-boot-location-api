package com.jorgonor.locationapi.adapter.rest;

import com.jorgonor.locationapi.adapter.rest.api.ErrorResponseDTO;
import com.jorgonor.locationapi.domain.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class LocationControllerAdvice {

    private static final ResponseEntity<Void> VOID_NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();

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

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        return VOID_NOT_FOUND_RESPONSE;
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
