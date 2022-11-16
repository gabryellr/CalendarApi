package com.gabryellr.calendarapi.interviewer.service;

import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.exception.exceptions.SlotValidationException;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.dto.SlotTimeDto;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerSlotDocument;
import com.gabryellr.calendarapi.interviewer.mapper.InterviewerMapper;
import com.gabryellr.calendarapi.interviewer.model.Slot;
import com.gabryellr.calendarapi.interviewer.model.SlotTime;
import com.gabryellr.calendarapi.interviewer.repository.InterviewerSlotRepository;
import com.gabryellr.calendarapi.interviewer.status.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InterviewerSlotService {

    public static final int ONE_HOUR = 1;
    private InterviewerSlotRepository repository;
    private InterviewerMapper mapper;

    public InterviewerSlotDto findSlotsByInterviewerId(String id) {
        InterviewerSlotDocument slotByInterviewIdList = this.repository.findAllByInterviewerId(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found Slots with interviewer id: %s", id)));

        return this.mapper.toInterviewerSlotDto(slotByInterviewIdList);
    }

    public InterviewerSlotDto saveSlot(InterviewerDto interviewerDto, InterviewerSlotDto interviewerSlotDto) {
        SlotTimeDto slotTimeDto = interviewerSlotDto.getSlotTimeDto();
        LocalTime from = slotTimeDto.getFrom();
        LocalTime to = slotTimeDto.getTo();
        Optional<InterviewerSlotDocument> interviewerSlotDocumentOptional = repository.findAllByInterviewerId(interviewerDto.getId());

        slotTimeValidation(interviewerSlotDto, interviewerSlotDocumentOptional);
        List<SlotTimeDto> slotTimeDtoList = processSlots(from, to);

        InterviewerSlotDocument interviewerSlotDocument = this.mapper.toInterviewerSlotDocument(interviewerDto.getId(), interviewerSlotDto, slotTimeDtoList);
        InterviewerSlotDocument slotSaved = this.repository.save(interviewerSlotDocument);

        return this.mapper.toInterviewerSlotDto(slotSaved);
    }

    public List<InterviewerAvailabilityDto> findAllInterviewersAvailable(LocalDate date, LocalTime from, LocalTime to) {
        List<InterviewerSlotDocument> allInterviewersWithSlotsAvailable = this.repository.findAllBySlotListDateAndSlotListSlotTimeListStatus(date, StatusEnum.AVAILABLE);

        List<InterviewerAvailabilityDto> interviewerAvailabilityDtoList = new ArrayList<>();

        allInterviewersWithSlotsAvailable.forEach(interviewerSlotDocument -> {
            List<Slot> slotListPerInterviewer = interviewerSlotDocument.getSlotList();

            slotListPerInterviewer.forEach(slot -> {
                InterviewerAvailabilityDto.InterviewerAvailabilityDtoBuilder availabilityDtoBuilder = InterviewerAvailabilityDto.builder();

                if (date.isEqual(date)) {
                    List<SlotTime> allAvailableSlotsList = slot.getSlotTimeList()
                            .stream()
                            .filter(slotTime -> StatusEnum.AVAILABLE.equals(slotTime.getStatus()))
                            .collect(Collectors.toList());

                    if (from != null && to != null) {
                        List<SlotTimeDto> slotTimeBetweenIntervalDtoList = allAvailableSlotsList.stream()
                                .filter(slotTime -> isBetweenIntervalTime(from, to, slotTime))
                                .collect(Collectors.toList())
                                .stream()
                                .map(slotTime -> mapper.toSlotTimeDto(slotTime))
                                .collect(Collectors.toList());

                        if (slotTimeBetweenIntervalDtoList.isEmpty()) return;

                        availabilityDtoBuilder.slotTimeDtoList(slotTimeBetweenIntervalDtoList);
                    } else {
                        availabilityDtoBuilder.slotTimeDtoList(this.mapper.toSlotTimeDtoList(allAvailableSlotsList));
                    }

                    availabilityDtoBuilder.interviewerId(interviewerSlotDocument.getInterviewerId());
                    availabilityDtoBuilder.date(date);
                }
                interviewerAvailabilityDtoList.add(availabilityDtoBuilder.build());
            });


        });
        return interviewerAvailabilityDtoList;
    }

    public List<InterviewerAvailabilityDto> findAllInterviewersAvailableByDate(LocalDate date, com.gabryellr.calendarapi.candidate.dto.SlotTimeDto from, com.gabryellr.calendarapi.candidate.dto.SlotTimeDto to) {
        return this.findAllInterviewersAvailable(date, from.getFrom(), to.getTo());
    }

    private boolean isBetweenIntervalTime(LocalTime from, LocalTime to, SlotTime slotTime) {
        return (slotTime.getFrom().isAfter(from) || slotTime.getFrom().equals(from)) && (slotTime.getTo().isBefore(to) || slotTime.getTo().equals(to));
    }

    private List<SlotTimeDto> processSlots(LocalTime from, LocalTime to) {
        List<SlotTimeDto> slotTimeDtoList = new ArrayList<>();

        while (from.isBefore(to)) {
            LocalTime fromPlusOneHour = from.plus(ONE_HOUR, ChronoUnit.HOURS);

            slotTimeDtoList.add(SlotTimeDto.builder().from(from).to(fromPlusOneHour).build());
            from = fromPlusOneHour;
        }

        return slotTimeDtoList;
    }

    private void slotTimeValidation(InterviewerSlotDto interviewerSlotDto, Optional<InterviewerSlotDocument> allSlotsByInterviewerIdOpt) {
        SlotTimeDto slotTimeDto = interviewerSlotDto.getSlotTimeDto();
        String interviewerId = interviewerSlotDto.getInterviewerId();
        LocalTime from = slotTimeDto.getFrom();
        LocalTime to = slotTimeDto.getTo();

        boolean isInvalidDate = to.isBefore(from);
        boolean hasMinutesOrSecondsDifferentFromZero = hasMinutesOrSecondsDifferentFromZero(slotTimeDto);

        if (isInvalidDate) {
            throw new SlotValidationException("date To cannot be before date From");
        }

        if (hasMinutesOrSecondsDifferentFromZero) {
            throw new SlotValidationException("Minutes or/and seconds must be zero");
        }

        if (allSlotsByInterviewerIdOpt.isEmpty()) return;

        List<Slot> slotList = allSlotsByInterviewerIdOpt.get().getSlotList();

        slotList.stream()
                .filter(slot -> slot.getDate().isEqual(interviewerSlotDto.getDate()))
                .map(Slot::getSlotTimeList)
                .forEach(slotDtoList -> {
                    boolean hasFromAfterResourceSaved = slotDtoList.stream()
                            .anyMatch(slotSaved -> from.isAfter(slotSaved.getFrom()) || from == slotSaved.getFrom());

                    if (hasFromAfterResourceSaved) {
                        throw new SlotValidationException(String.format("From %s is after or equal than from's saved to the interviewer with Id %s", from, interviewerId));
                    }

                    SlotTime lastSlotSaved = slotDtoList.get(slotDtoList.size() - 1);
                    if (to.isBefore(lastSlotSaved.getTo()) || to == lastSlotSaved.getTo()) {
                        throw new SlotValidationException(String.format("To %s is before or equal than last slot saved to the interviewer with id %s", to, interviewerId));
                    }
                });
    }

    private boolean hasMinutesOrSecondsDifferentFromZero(SlotTimeDto slotTimeDto) {
        return slotTimeDto.getFrom().getMinute() != 0 || slotTimeDto.getTo().getSecond() != 0;
    }
}