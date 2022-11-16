package com.gabryellr.calendarapi.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotTimeDto {

    @NotNull(message = "From cannot be null")
    @Schema(implementation = String.class, example = "09:00")
    private LocalTime from;

    @NotNull(message = "To cannot be null")
    @Schema(implementation = String.class, example = "10:00")
    private LocalTime to;

}
