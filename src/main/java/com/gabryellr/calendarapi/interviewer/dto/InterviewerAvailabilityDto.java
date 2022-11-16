package com.gabryellr.calendarapi.interviewer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerAvailabilityDto {

    private String interviewerId;
    private LocalDate date;

    @JsonProperty("times")
    private List<SlotTimeDto> slotTimeDtoList;

}
