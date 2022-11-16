package com.gabryellr.calendarapi.candidate.entities;

import com.gabryellr.calendarapi.candidate.model.Slot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "candidate-slots")
public class CandidateSlotDocument {

    @Id
    private String id;

    private String candidateId;
    private List<Slot> slotList = new ArrayList<>();

}