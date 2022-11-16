package com.gabryellr.calendarapi.interviewer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent
    private LocalDate date;

    @JsonProperty("times")
    private List<SlotTimeDto> slotTimeDtoList;

}
