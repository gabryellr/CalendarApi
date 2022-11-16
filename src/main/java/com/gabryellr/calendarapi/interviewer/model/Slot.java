package com.gabryellr.calendarapi.interviewer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

    private LocalDate date;
    private List<SlotTime> slotTimeList;

    public void updateSlot(List<SlotTime> newSlotTimeDtoList) {
        newSlotTimeDtoList.removeIf(slotTime -> slotTimeList.contains(slotTime));

        this.slotTimeList.addAll(newSlotTimeDtoList);
        this.slotTimeList.sort(Comparator.comparing(SlotTime::getFrom));
    }

}
