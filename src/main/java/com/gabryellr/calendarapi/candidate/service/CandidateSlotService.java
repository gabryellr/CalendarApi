package com.gabryellr.calendarapi.candidate.service;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotTimeDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateSlotDocument;
import com.gabryellr.calendarapi.candidate.mapper.CandidateMapper;
import com.gabryellr.calendarapi.candidate.model.Slot;
import com.gabryellr.calendarapi.candidate.model.SlotTime;
import com.gabryellr.calendarapi.candidate.repository.CandidateSlotRepository;
import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.exception.exceptions.SlotValidationException;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import com.gabryellr.calendarapi.interviewer.service.InterviewerSlotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CandidateSlotService {

    public static final int ONE_HOUR = 1;
    public static final int FIRST_TIME_DTO = 0;
    private CandidateSlotRepository repository;
    private CandidateMapper mapper;
    private InterviewerSlotService interviewerSlotService;

    public CandidateSlotDto findAllSlotsByCandidateId(String id) {
        CandidateSlotDocument slotByCandidateIdList = this.repository.findAllByCandidateId(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found Slots with candidate id: %s", id)));

        return this.mapper.toCandidateSlotDto(slotByCandidateIdList);
    }

    public CandidateSlotDto saveSlots(CandidateDto candidateDto, CandidateSlotDto candidateSlotDto) {
        SlotTimeDto slotTimeDto = candidateSlotDto.getSlotTimeDto();
        LocalTime from = slotTimeDto.getFrom();
        LocalTime to = slotTimeDto.getTo();
        Optional<CandidateSlotDocument> optionalCandidateSlotDocument = repository.findAllByCandidateId(candidateDto.getId());

        slotTimeValidation(candidateSlotDto, optionalCandidateSlotDocument);
        List<SlotTimeDto> slotTimeDtoList = processSlots(from, to);

        CandidateSlotDocument candidateSlotDocument = this.mapper.toCandidateSlotDocument(candidateDto.getId(), candidateSlotDto, slotTimeDtoList);
        CandidateSlotDocument slotSaved = this.repository.save(candidateSlotDocument);

        return this.mapper.toCandidateSlotDto(slotSaved);
    }

    public List<InterviewerAvailabilityDto> findAllInterviewerAvailabilityByCandidate(CandidateDto candidateDto) {
        CandidateSlotDto slotsByCandidateId = this.findAllSlotsByCandidateId(candidateDto.getId());

        List<List<InterviewerAvailabilityDto>> interviewerAvailableList = slotsByCandidateId.getAvailableSlotCandidate().stream()
                .map(slotDto -> interviewerSlotService.findAllInterviewersAvailableByDate(slotDto.getDate(),
                        slotDto.getSlotTimeDtoList().get(FIRST_TIME_DTO), slotDto.getSlotTimeDtoList().get(lastTimeDto(slotDto))))
                .collect(Collectors.toList());

        return interviewerAvailableList.stream()
                .flatMap(Collection::stream)
                .filter(interviewerAvailabilityDto -> !interviewerAvailabilityDto.getSlotTimeDtoList().isEmpty())
                .map(interviewerAvailabilityDto -> this.mapper.toInterviewerCandidateAvailabilityDto(interviewerAvailabilityDto))
                .collect(Collectors.toList());
    }

    private int lastTimeDto(SlotDto slotDto) {
        return slotDto.getSlotTimeDtoList().size() - 1;
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

    private void slotTimeValidation(CandidateSlotDto candidateSlotDto, Optional<CandidateSlotDocument> allSlotsByCandidateIdOpt) {
        SlotTimeDto slotTimeDto = candidateSlotDto.getSlotTimeDto();
        String candidateId = candidateSlotDto.getCandidateId();
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

        if (allSlotsByCandidateIdOpt.isEmpty()) return;

        List<Slot> slotList = allSlotsByCandidateIdOpt.get().getSlotList();

        slotList.stream()
                .filter(slot -> slot.getDate().isEqual(candidateSlotDto.getDate()))
                .map(Slot::getSlotTimeList)
                .forEach(slotDtoList -> {
                    boolean hasFromAfterResourceSaved = slotDtoList.stream()
                            .anyMatch(slotSaved -> from.isAfter(slotSaved.getFrom()) || from == slotSaved.getFrom());

                    if (hasFromAfterResourceSaved) {
                        throw new SlotValidationException(String.format("From %s is after or equal than from's saved to the candidate with Id %s", from, candidateId));
                    }

                    SlotTime lastSlotSaved = slotDtoList.get(slotDtoList.size() - 1);
                    if (to.isBefore(lastSlotSaved.getTo()) || to == lastSlotSaved.getTo()) {
                        throw new SlotValidationException(String.format("To %s is before or equal than last slot saved to the candidate with id %s", to, candidateId));
                    }
                });
    }

    private boolean hasMinutesOrSecondsDifferentFromZero(SlotTimeDto slotTimeDto) {
        return slotTimeDto.getFrom().getMinute() != 0 || slotTimeDto.getTo().getSecond() != 0;
    }
}