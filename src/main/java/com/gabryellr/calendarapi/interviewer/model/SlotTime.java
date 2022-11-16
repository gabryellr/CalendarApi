package com.gabryellr.calendarapi.interviewer.model;

import com.gabryellr.calendarapi.interviewer.status.StatusEnum;
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

    private String slotTimeId;
    private StatusEnum status;
    private LocalTime from;
    private LocalTime to;

}
