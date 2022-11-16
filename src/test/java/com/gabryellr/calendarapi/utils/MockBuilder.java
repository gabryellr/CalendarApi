package com.gabryellr.calendarapi.utils;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotTimeDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateDocument;
import com.gabryellr.calendarapi.candidate.entities.CandidateSlotDocument;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerSlotDocument;

import java.time.LocalTime;

public class MockBuilder {


    public static CandidateDto mockCandidateDto() {
        return CandidateDto.builder().id("id123").name("nameTest").build();
    }

    public static CandidateDocument mockCandidateDocument() {
        return CandidateDocument.builder().id("id123").name("nameTest").build();
    }

    public static CandidateSlotDto mockCandidateSlotDto() {
        SlotTimeDto slotTimeDto = SlotTimeDto.builder()
                .from(LocalTime.of(9, 0, 0, 0))
                .to(LocalTime.of(18, 0, 0, 0))
                .build();

        return CandidateSlotDto.builder()
                .candidateId("candidateId123")
                .slotTimeDto(slotTimeDto)
                .build();
    }

    public static CandidateSlotDocument mockCandidateSlotDocument() {
        return CandidateSlotDocument.builder().build();
    }

    public static InterviewerDto mockInterviewerDto() {
        return InterviewerDto.builder().id("id123").name("nameTest").build();
    }

    public static InterviewerSlotDto mockInterviewerSlotDto() {
        com.gabryellr.calendarapi.interviewer.dto.SlotTimeDto slotTimeDto = com.gabryellr.calendarapi.interviewer.dto.SlotTimeDto.builder()
                .from(LocalTime.of(9, 0, 0, 0))
                .to(LocalTime.of(18, 0, 0, 0))
                .build();

        return InterviewerSlotDto.builder()
                .interviewerId("interviewer123")
                .slotTimeDto(slotTimeDto)
                .build();
    }

    public static InterviewerSlotDocument mockInterviewerSlotDocument() {
        return InterviewerSlotDocument.builder().build();
    }
}
