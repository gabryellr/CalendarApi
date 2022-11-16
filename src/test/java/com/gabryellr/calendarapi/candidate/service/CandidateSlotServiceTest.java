package com.gabryellr.calendarapi.candidate.service;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.dto.SlotTimeDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateSlotDocument;
import com.gabryellr.calendarapi.candidate.mapper.CandidateMapper;
import com.gabryellr.calendarapi.candidate.model.Slot;
import com.gabryellr.calendarapi.candidate.model.SlotTime;
import com.gabryellr.calendarapi.candidate.repository.CandidateSlotRepository;
import com.gabryellr.calendarapi.exception.exceptions.ResourceAlreadyExistsException;
import com.gabryellr.calendarapi.exception.exceptions.SlotValidationException;
import com.gabryellr.calendarapi.interviewer.service.InterviewerSlotService;
import com.gabryellr.calendarapi.utils.MockBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

class CandidateSlotServiceTest {

    @Mock
    private CandidateSlotRepository repository;

    @Mock
    private InterviewerSlotService interviewerSlotService;

    @Captor
    ArgumentCaptor<CandidateSlotDocument> candidateSlotDocumentCaptor;

    private final CandidateMapper mapper = Mappers.getMapper(CandidateMapper.class);

    @InjectMocks
    private CandidateSlotService candidateSlotService;

    @BeforeEach
    void setUp() {
        this.candidateSlotService = new CandidateSlotService(repository, mapper, interviewerSlotService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findAllSlotsByCandidateId_ValidId_ReturnsCandidateSlotDto() {
        Optional<CandidateSlotDocument> candidateSlotDocumentOptional = Optional.of(MockBuilder.mockCandidateSlotDocument());

        Mockito.when(repository.findAllByCandidateId(ArgumentMatchers.anyString()))
                .thenReturn(candidateSlotDocumentOptional);

        this.candidateSlotService.findAllSlotsByCandidateId(ArgumentMatchers.anyString());
    }

    @Test
    void saveSlots_FromIsAfterTo_ThrowsSlotValidationException() {
        CandidateDto candidateDto = MockBuilder.mockCandidateDto();
        CandidateSlotDto candidateSlotDto = MockBuilder.mockCandidateSlotDto();
        candidateSlotDto.getSlotTimeDto().setFrom(LocalTime.of(10, 0, 0, 0));
        candidateSlotDto.getSlotTimeDto().setTo(LocalTime.of(9, 0, 0, 0));

        Mockito.when(repository.existsByCandidateId(ArgumentMatchers.anyString())).thenReturn(false);

        SlotValidationException slotValidationException = Assertions.assertThrows(SlotValidationException.class,
                () -> this.candidateSlotService.saveSlots(candidateDto, candidateSlotDto));

        Assertions.assertEquals("date To cannot be before date From", slotValidationException.getMessage());
    }

    @Test
    void saveSlots_FromOrToHasMinutesDifferentFromZero_ThrowsSlotValidationException() {
        CandidateDto candidateDto = MockBuilder.mockCandidateDto();
        CandidateSlotDto candidateSlotDto = MockBuilder.mockCandidateSlotDto();
        candidateSlotDto.getSlotTimeDto().setFrom(LocalTime.of(9, 25, 0, 0));
        candidateSlotDto.getSlotTimeDto().setTo(LocalTime.of(10, 8, 0, 0));

        Mockito.when(repository.existsByCandidateId(ArgumentMatchers.anyString())).thenReturn(false);

        SlotValidationException slotValidationException = Assertions.assertThrows(SlotValidationException.class,
                () -> this.candidateSlotService.saveSlots(candidateDto, candidateSlotDto));

        Assertions.assertEquals("Minutes or/and seconds must be zero", slotValidationException.getMessage());
    }

    @Test
    void saveSlots_FromHasMoreThanOneHourDifferentToTo_SavesListOfSlotsWithOneHourOfInterval() {
        CandidateDto candidateDto = MockBuilder.mockCandidateDto();
        CandidateSlotDto candidateSlotDto = MockBuilder.mockCandidateSlotDto();
        candidateSlotDto.getSlotTimeDto().setFrom(LocalTime.of(9, 0, 0, 0));
        candidateSlotDto.getSlotTimeDto().setTo(LocalTime.of(12, 9, 0, 0));

        Mockito.when(repository.existsByCandidateId(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(CandidateSlotDocument.builder().build());

        this.candidateSlotService.saveSlots(candidateDto, candidateSlotDto);

        Mockito.verify(this.repository, Mockito.atLeastOnce()).save(candidateSlotDocumentCaptor.capture());

        CandidateSlotDocument documentCaptorValue = candidateSlotDocumentCaptor.getValue();
        Assertions.assertEquals(4, documentCaptorValue.getSlotList().get(0).getSlotTimeList().size());
    }

    @Test
    void updateSlots_HasFromAfterResourceSaved_ThrowsSlotValidationException() {
        CandidateSlotDocument candidateSlotDocument = MockBuilder.mockCandidateSlotDocument();
        LocalTime from = LocalTime.of(9, 0, 0, 0);
        LocalTime to = LocalTime.of(10, 0, 0, 0);
        LocalDate date = LocalDate.of(2022, 11, 15);

        SlotTime slotTime = SlotTime.builder()
                .from(from)
                .to(to)
                .build();

        Slot slot = Slot.builder().date(date).slotTimeList(List.of(slotTime)).build();

        candidateSlotDocument.setSlotList(List.of(slot));

        Mockito.when(repository.findAllByCandidateId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(candidateSlotDocument));

        CandidateSlotDto candidateSlotDto = MockBuilder.mockCandidateSlotDto();
        candidateSlotDto.setSlotTimeDto(SlotTimeDto.builder()
                .from(from)
                .to(to)
                .build());

        candidateSlotDto.setDate(date);

        SlotValidationException exception = Assertions.assertThrows(SlotValidationException.class,
                () -> candidateSlotService.saveSlots(CandidateDto.builder().id("id123").build(), candidateSlotDto));

        Assertions.assertEquals("From 09:00 is after or equal than from's saved to the candidate with Id candidateId123", exception.getMessage());
    }

    @Test
    void updateSlots_HasToBeforeResourceSaved_ThrowsSlotValidationException() {
        CandidateSlotDocument candidateSlotDocument = MockBuilder.mockCandidateSlotDocument();
        LocalTime fromSaved = LocalTime.of(9, 0, 0, 0);
        LocalTime toSaved = LocalTime.of(10, 0, 0, 0);
        LocalDate date = LocalDate.of(2022, 11, 15);

        SlotTime slotTime = SlotTime.builder()
                .from(fromSaved)
                .to(toSaved)
                .build();

        Slot slot = Slot.builder().date(date).slotTimeList(List.of(slotTime)).build();

        candidateSlotDocument.setSlotList(List.of(slot));

        Mockito.when(repository.findAllByCandidateId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(candidateSlotDocument));

        CandidateSlotDto candidateSlotDto = MockBuilder.mockCandidateSlotDto();
        candidateSlotDto.setSlotTimeDto(SlotTimeDto.builder()
                .from(fromSaved.minusHours(1L))
                .to(toSaved.minusHours(1L))
                .build());

        candidateSlotDto.setDate(date);

        SlotValidationException exception = Assertions.assertThrows(SlotValidationException.class,
                () -> candidateSlotService.saveSlots(CandidateDto.builder().id("id123").build(), candidateSlotDto));

        Assertions.assertEquals("To 09:00 is before or equal than last slot saved to the candidate with id candidateId123", exception.getMessage());
    }
}