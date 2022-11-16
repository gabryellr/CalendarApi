package com.gabryellr.calendarapi.interviewer.service;

import com.gabryellr.calendarapi.exception.exceptions.ResourceAlreadyExistsException;
import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerDocument;
import com.gabryellr.calendarapi.interviewer.mapper.InterviewerMapper;
import com.gabryellr.calendarapi.interviewer.repository.InterviewerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class InterviewerService {

    private InterviewerRepository repository;
    private InterviewerMapper mapper;
    private InterviewerSlotService interviewerSlotService;

    public InterviewerDto create(InterviewerDto interviewerDto) {
        String interviewerName = interviewerDto.getName();

        if (isExistsByName(interviewerName)) {
            throw new ResourceAlreadyExistsException(String.format("Interviewer with name %s already exists.", interviewerName));
        }

        InterviewerDocument interviewerDocument = this.mapper.toInterviewerDocument(interviewerDto);

        InterviewerDocument interviewerSaved = this.repository.save(interviewerDocument);
        log.info("Interviewer [{}] has been saved", interviewerSaved);

        return this.mapper.toInterviewerDto(interviewerSaved);
    }

    public List<InterviewerDto> findAll() {
        List<InterviewerDocument> interviewerDocumentList = this.repository.findAll();

        return this.mapper.toInterviewerDtoList(interviewerDocumentList);
    }

    public InterviewerDto findById(String interviewerId) {
        return findInterviewerById(interviewerId)
                .map(mapper::toInterviewerDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("interviewer with ID %s not found", interviewerId)));
    }

    public InterviewerSlotDto findAllSlotsByInterviewerId(String interviewerId) {
        InterviewerDto interviewerDto = findById(interviewerId);

        return this.interviewerSlotService.findSlotsByInterviewerId(interviewerDto.getId());
    }

    public InterviewerSlotDto createSlot(String interviewerId, InterviewerSlotDto interviewerSlotDto) {
        InterviewerDto interviewerDto = this.findById(interviewerId);

        return this.interviewerSlotService.saveSlot(interviewerDto, interviewerSlotDto);
    }

    public List<InterviewerAvailabilityDto> findAllInterviewersAvailable(LocalDate date, Integer from, Integer to) {
        LocalTime fromLocalTime;
        LocalTime toLocalTime;

        try {
            fromLocalTime = from == null ? null : LocalTime.of(from, 0, 0, 0);
            toLocalTime = to == null ? null : LocalTime.of(to, 0, 0, 0);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Error to convert filter time");
        }

        return this.interviewerSlotService.findAllInterviewersAvailable(date, fromLocalTime, toLocalTime);
    }

    private Optional<InterviewerDocument> findInterviewerById(String interviewerId) {
        log.info("Searching interviewer with id {}", interviewerId);
        return this.repository.findById(interviewerId);
    }

    private boolean isExistsByName(String name) {
        log.info("Searching interviewer with name {}", name);
        return this.repository.existsByName(name);
    }
}