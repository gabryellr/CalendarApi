package com.gabryellr.calendarapi.interviewer.controller;

import com.gabryellr.calendarapi.interviewer.dto.InterviewerAvailabilityDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.service.InterviewerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/interviewers")
public class InterviewerController {

    private InterviewerService service;

    @Operation(summary = "Create Interviewer")
    @PostMapping
    public ResponseEntity<InterviewerDto> create(@RequestBody @Validated InterviewerDto interviewerDto) {
        InterviewerDto interviewerCreated = this.service.create(interviewerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(interviewerCreated);
    }

    @Operation(summary = "Find all interviewers")
    @GetMapping
    public ResponseEntity<List<InterviewerDto>> findAll() {
        List<InterviewerDto> interviewerDtoList = this.service.findAll();

        return ResponseEntity.ok(interviewerDtoList);
    }

    @Operation(summary = "Find interviewer by id")
    @GetMapping("/{interviewerId}")
    public ResponseEntity<InterviewerDto> findById(@PathVariable("interviewerId") String interviewerId) {
        InterviewerDto interviewerDto = this.service.findById(interviewerId);

        return ResponseEntity.ok(interviewerDto);
    }

    @Operation(summary = "Find slots by interviewer id")
    @GetMapping("/{interviewerId}/slots")
    public ResponseEntity<InterviewerSlotDto> findSlotsByInterviewerId(@PathVariable("interviewerId") String interviewerId) {
        InterviewerSlotDto allSlotsByInterviewerId = this.service.findAllSlotsByInterviewerId(interviewerId);

        return ResponseEntity.ok(allSlotsByInterviewerId);
    }

    @Operation(summary = "Find all interviewers by filter. Date, from and To")
    @GetMapping("/availabilities")
    public ResponseEntity<List<InterviewerAvailabilityDto>> findAllInterviewersAvailable(@RequestParam("date")
                                                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                                         LocalDate date,
                                                                                         @RequestParam(value = "from", required = false)
                                                                                         Integer from,
                                                                                         @RequestParam(value = "to", required = false)
                                                                                         Integer to) {

        List<InterviewerAvailabilityDto> allInterviewersAvailable = this.service.findAllInterviewersAvailable(date, from, to);

        return ResponseEntity.ok(allInterviewersAvailable);
    }

    @Operation(summary = "Create slot to the interviewer by interviewer id")
    @PostMapping("/{interviewerId}/slots")
    public ResponseEntity<InterviewerSlotDto> createInterviewerSlot(@PathVariable("interviewerId") String interviewerId,
                                                                    @RequestBody @Validated InterviewerSlotDto interviewerSlotDto) {

        InterviewerSlotDto slotCreated = this.service.createSlot(interviewerId, interviewerSlotDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(slotCreated);
    }
}
