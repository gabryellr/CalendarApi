package com.gabryellr.calendarapi.interviewer.mapper;

import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.dto.SlotDto;
import com.gabryellr.calendarapi.interviewer.dto.SlotTimeDto;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerDocument;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerSlotDocument;
import com.gabryellr.calendarapi.interviewer.model.Slot;
import com.gabryellr.calendarapi.interviewer.model.SlotTime;
import com.gabryellr.calendarapi.interviewer.status.StatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InterviewerMapper {

    InterviewerDocument toInterviewerDocument(InterviewerDto interviewerDto);

    InterviewerDto toInterviewerDto(InterviewerDocument interviewerSaved);

    List<InterviewerDto> toInterviewerDtoList(List<InterviewerDocument> interviewerDocumentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "interviewerId", target = "interviewerId")
    @Mapping(target = "slotList", expression = "java(toSlotList(interviewerSlotDto.getDate(), slotTimeDtoList))")
    InterviewerSlotDocument toInterviewerSlotDocument(String interviewerId, InterviewerSlotDto interviewerSlotDto, List<SlotTimeDto> slotTimeDtoList);

    default InterviewerSlotDto toInterviewerSlotDto(InterviewerSlotDocument interviewerSlotDocument) {
        if (interviewerSlotDocument == null) return null;

        return InterviewerSlotDto.builder()
                .id(interviewerSlotDocument.getId())
                .interviewerId(interviewerSlotDocument.getInterviewerId())
                .availableSlotInterviewer(toSlotDtoList(interviewerSlotDocument.getSlotList()))
                .build();
    }


    default List<SlotDto> toSlotDtoList(List<Slot> slotList) {
        if (CollectionUtils.isEmpty(slotList)) return List.of();

        return slotList.stream()
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

    default List<SlotTime> toSlotTimeList(List<SlotTimeDto> slotTimeDtoList) {
        return slotTimeDtoList.stream()
                .map(slotTimeDto -> SlotTime.builder()
                        .from(slotTimeDto.getFrom())
                        .to(slotTimeDto.getTo())
                        .status(slotTimeDto.getStatus() == null ? StatusEnum.AVAILABLE : slotTimeDto.getStatus())
                        .slotTimeId(UUID.randomUUID().toString())
                        .build())
                .collect(Collectors.toList());
    }

    List<InterviewerSlotDto> toInterviewerSlotDtoList(List<InterviewerSlotDocument> allInterviewersWithSlotsAvailable);

    SlotTimeDto toSlotTimeDto(SlotTime slotTime);
}