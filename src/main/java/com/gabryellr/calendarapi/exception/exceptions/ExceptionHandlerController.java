package com.gabryellr.calendarapi.exception.exceptions;

import com.gabryellr.calendarapi.exception.dto.ApiErrorDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorDto handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ApiErrorDto.builder()
                .errorList(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Request body validation error")
                .build();
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ApiErrorDto handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ApiErrorDto.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .errorMessage(HttpStatus.CONFLICT.getReasonPhrase())
                .errorList(Collections.singletonList(ex.getLocalizedMessage()))
                .build();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiErrorDto handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiErrorDto.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(HttpStatus.NOT_FOUND.getReasonPhrase())
                .errorList(Collections.singletonList(ex.getLocalizedMessage()))
                .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SlotValidationException.class)
    public ApiErrorDto handleSlotValidationException(SlotValidationException ex) {
        return ApiErrorDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorList(Collections.singletonList(ex.getLocalizedMessage()))
                .build();
    }
}