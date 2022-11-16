package com.gabryellr.calendarapi.candidate.controller;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.service.CandidateService;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/candidates")
public class CandidateController {

    private CandidateService service;

    @Operation(summary = "Create Candidate")
    @PostMapping
    public ResponseEntity<CandidateDto> create(@RequestBody @Validated CandidateDto candidateDto) {
        CandidateDto candidateCreated = this.service.create(candidateDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(candidateCreated);
    }

    @Operation(summary = "Find call candidates")
    @GetMapping
    public ResponseEntity<List<CandidateDto>> findAll() {
        List<CandidateDto> candidateDtoList = this.service.findAll();

        return ResponseEntity.ok(candidateDtoList);
    }

    @Operation(summary = "Find candidate by id")
    @GetMapping("/{candidateId}")
    public ResponseEntity<CandidateDto> findById(@PathVariable("candidateId") String candidateId) {
        CandidateDto candidateDto = this.service.findById(candidateId);

        return ResponseEntity.ok(candidateDto);
    }

    @Operation(summary = "Find slots of the candidate by candidate id")
    @GetMapping("/{candidateId}/slots")
    public ResponseEntity<CandidateSlotDto> findSlotsByCandidateId(@PathVariable("candidateId") String candidateId) {
        CandidateSlotDto allSlotsByCandidateId = this.service.findAllSlotsByCandidateId(candidateId);

        return ResponseEntity.ok(allSlotsByCandidateId);
    }

    @Operation(summary = "Create slot of the candidate by candidate id")
    @PostMapping("/{candidateId}/slots")
    public ResponseEntity<CandidateSlotDto> createCandidateSlot(@PathVariable("candidateId") String candidateId,
                                                                @RequestBody @Validated CandidateSlotDto candidateSlotDto) {

        CandidateSlotDto slotCreated = this.service.createSlot(candidateId, candidateSlotDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(slotCreated);
    }

    @Operation(summary = "Find all interviewers available by candidate id")
    @GetMapping("/{candidateId}/availabilities/interviewers")
    public ResponseEntity<List<InterviewerAvailabilityDto>> findAllInterviewerAvailabilityByCandidateId(@PathVariable("candidateId") String candidateId) {
        List<InterviewerAvailabilityDto> allInterviewerAvailabilityByCandidateId = this.service.findAllInterviewerAvailabilityByCandidateId(candidateId);

        return ResponseEntity.ok(allInterviewerAvailabilityByCandidateId);
    }
}
