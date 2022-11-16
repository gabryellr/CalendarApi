package com.gabryellr.calendarapi.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDto {


    @JsonProperty("errors")
    List<String> errorList;

    String errorMessage;
    int statusCode;

}