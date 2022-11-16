package com.gabryellr.calendarapi.interviewer.entities;

import com.gabryellr.calendarapi.interviewer.model.Slot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "interviewers-slots")
public class InterviewerSlotDocument {

    @Id
    private String id;

    private String interviewerId;
    private List<Slot> slotList;

}