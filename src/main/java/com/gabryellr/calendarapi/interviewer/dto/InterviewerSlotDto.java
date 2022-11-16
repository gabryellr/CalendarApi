package com.gabryellr.calendarapi.interviewer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewerSlotDto {

    @Hidden
    private String id;

    @JsonProperty("slots")
    @Hidden
    private List<SlotDto> availableSlotInterviewer;

    @Hidden
    private String interviewerId;

    @JsonProperty("slot")
    @NotNull(message = "Slot time cannot be null")
    @Valid
    private SlotTimeDto slotTimeDto;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent
    private LocalDate date;

}