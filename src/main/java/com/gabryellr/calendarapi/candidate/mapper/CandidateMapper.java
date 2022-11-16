package com.gabryellr.calendarapi.candidate.mapper;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotTimeDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateDocument;
import com.gabryellr.calendarapi.candidate.entities.CandidateSlotDocument;
import com.gabryellr.calendarapi.candidate.model.Slot;
import com.gabryellr.calendarapi.candidate.model.SlotTime;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    CandidateDocument toCandidateDocument(CandidateDto candidateDto);

    CandidateDto toCandidateDto(CandidateDocument candidateSaved);

    List<CandidateDto> toCandidateDtoList(List<CandidateDocument> candidateDocumentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "candidateId", target = "candidateId")
    @Mapping(target = "slotList", expression = "java(toSlotList(candidateSlotDto.getDate(), slotTimeDtoList))")
    CandidateSlotDocument toCandidateSlotDocument(String candidateId, CandidateSlotDto candidateSlotDto, List<SlotTimeDto> slotTimeDtoList);

    default CandidateSlotDto toCandidateSlotDto(CandidateSlotDocument candidateSlotDocument) {
        if (candidateSlotDocument == null) return null;

        return CandidateSlotDto.builder()
                .id(candidateSlotDocument.getId())
                .candidateId(candidateSlotDocument.getCandidateId())
                .availableSlotCandidate(toSlotDtoList(candidateSlotDocument.getSlotList()))
                .build();
    }


    default List<SlotDto> toSlotDtoList(List<Slot> slotList) {
        return CollectionUtils.isEmpty(slotList) ? List.of() : slotList.stream()
                .map(slot -> SlotDto.builder().slotTimeDtoList(toSlotTimeDtoList(slot.getSlotTimeList()))
                        .date(slot.getDate())
                        .build())
                .collect(Collectors.toList());
    }

    List<SlotTimeDto> toSlotTimeDtoList(List<SlotTime> slotTimeList);

    default List<Slot> toSlotList(LocalDate date, List<SlotTimeDto> slotTimeDtoList) {
        return Collections.singletonList(Slot.builder()
                .date(date)
                .slotTimeList(toSlotTimeList(slotTimeDtoList))
                .build());
    }

    List<SlotTime> toSlotTimeList(List<SlotTimeDto> slotTimeDtoList);

    InterviewerAvailabilityDto toInterviewerCandidateAvailabilityDto(InterviewerAvailabilityDto interviewerAvailabilityDto);
}