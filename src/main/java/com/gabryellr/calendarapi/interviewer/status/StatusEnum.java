package com.gabryellr.calendarapi.interviewer.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StatusEnum {

    AVAILABLE("AVAILABLE"),
    BUSY("BUSY");

    private final String value;

    StatusEnum(String value) {
       this.value = value;
    }

    @JsonCreator
    public StatusEnum toEnum(String value) {
        return Arrays.stream(StatusEnum.values())
                .filter(statusEnum -> statusEnum.getValue().equalsIgnoreCase(value) || !StringUtils.hasText(value))
                .findFirst()
                .orElse(StatusEnum.AVAILABLE);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}