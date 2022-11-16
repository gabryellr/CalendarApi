package com.gabryellr.calendarapi.candidate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotTime {

    private LocalTime from;
    private LocalTime to;

}
