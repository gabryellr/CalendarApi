package com.gabryellr.calendarapi.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlotDto {

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @Hidden
    private String interviewerId;

    @JsonProperty("times")
    private List<SlotTimeDto> slotTimeDtoList;

}
