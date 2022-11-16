package com.gabryellr.calendarapi.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gabryellr.calendarapi.candidate.model.Slot;
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
public class CandidateSlotDto {

    @Hidden
    private String id;

    @JsonProperty("slots")
    @Hidden
    private List<SlotDto> availableSlotCandidate;

    @Hidden
    private String candidateId;

    @JsonProperty("slot")
    @NotNull(message = "Slot time cannot be null")
    @Valid
    private SlotTimeDto slotTimeDto;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent
    private LocalDate date;

}