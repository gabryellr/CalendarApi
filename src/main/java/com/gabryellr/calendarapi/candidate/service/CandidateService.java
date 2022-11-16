package com.gabryellr.calendarapi.candidate.service;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateDocument;
import com.gabryellr.calendarapi.candidate.mapper.CandidateMapper;
import com.gabryellr.calendarapi.candidate.repository.CandidateRepository;
import com.gabryellr.calendarapi.exception.exceptions.ResourceAlreadyExistsException;
import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class CandidateService {

    private CandidateRepository repository;
    private CandidateMapper mapper;
    private CandidateSlotService candidateSlotService;

    public CandidateDto create(CandidateDto candidateDto) {
        String candidateName = candidateDto.getName();

        if (isExistsByName(candidateName)) {
            throw new ResourceAlreadyExistsException(String.format("Candidate with name %s already exists.", candidateName));
        }

        CandidateDocument candidateDocument = this.mapper.toCandidateDocument(candidateDto);

        CandidateDocument candidateSaved = this.repository.save(candidateDocument);
        log.info("Candidate [{}] has been saved", candidateSaved);

        return this.mapper.toCandidateDto(candidateSaved);
    }

    public List<CandidateDto> findAll() {
        List<CandidateDocument> candidateDocumentList = this.repository.findAll();

        return this.mapper.toCandidateDtoList(candidateDocumentList);
    }

    public CandidateDto findById(String candidateId) {
        return findCandidateById(candidateId)
                .map(mapper::toCandidateDto)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Candidate with ID %s not found", candidateId)));
    }

    public CandidateSlotDto findAllSlotsByCandidateId(String candidateId) {
        CandidateDto candidateDto = findById(candidateId);

        return this.candidateSlotService.findAllSlotsByCandidateId(candidateDto.getId());
    }

    public CandidateSlotDto createSlot(String candidateId, CandidateSlotDto candidateSlotDto) {
        CandidateDto candidateDto = this.findById(candidateId);

        return this.candidateSlotService.saveSlots(candidateDto, candidateSlotDto);
    }

    public List<InterviewerAvailabilityDto> findAllInterviewerAvailabilityByCandidateId(String candidateId) {
        CandidateDto candidateDto = this.findById(candidateId);

       return this.candidateSlotService.findAllInterviewerAvailabilityByCandidate(candidateDto);
    }

    private Optional<CandidateDocument> findCandidateById(String candidateId) {
        log.info("Searching candidate with id {}", candidateId);
        return this.repository.findById(candidateId);
    }

    private boolean isExistsByName(String name) {
        log.info("Searching candidate with name {}", name);
        return this.repository.existsByName(name);
    }
}