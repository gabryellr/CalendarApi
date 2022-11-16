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

class InterviewerSlotServiceTest {

    @Mock
    private InterviewerSlotRepository repository;

    private final InterviewerMapper mapper = Mappers.getMapper(InterviewerMapper.class);

    @InjectMocks
    private InterviewerSlotService service;

    @Captor
    ArgumentCaptor<InterviewerSlotDocument> interviewerSlotDocumentCaptor;

    @BeforeEach
    void setUp() {
        this.service = new InterviewerSlotService(repository, mapper);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findSlotsByInterviewerId_SlotNotExistsWithInterviewerId_ThrowsResourceNotFoundException() {
        Mockito.when(repository.findAllByInterviewerId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findSlotsByInterviewerId("123"));
    }

    @Test
    void findSlotsByInterviewerId_SlotExistsWithInterviewerId_ReturnsInterviewerSlotDto() {
        InterviewerSlotDocument slotDocument = InterviewerSlotDocument.builder().build();

        Mockito.when(repository.findAllByInterviewerId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(slotDocument));

        InterviewerSlotDto slotsByInterviewerId = service.findSlotsByInterviewerId("123");
        Assertions.assertNotNull(slotsByInterviewerId);
    }

    @Test
    void saveSlots_FromIsAfterTo_ThrowsSlotValidationException() {
        InterviewerDto interviewerDto = MockBuilder.mockInterviewerDto();
        InterviewerSlotDto interviewerSlotDto = MockBuilder.mockInterviewerSlotDto();
        interviewerSlotDto.getSlotTimeDto().setFrom(LocalTime.of(10, 0, 0, 0));
        interviewerSlotDto.getSlotTimeDto().setTo(LocalTime.of(9, 0, 0, 0));

        Mockito.when(repository.existsByInterviewerId(ArgumentMatchers.anyString())).thenReturn(false);

        SlotValidationException slotValidationException = Assertions.assertThrows(SlotValidationException.class,
                () -> this.service.saveSlot(interviewerDto, interviewerSlotDto));

        Assertions.assertEquals("date To cannot be before date From", slotValidationException.getMessage());
    }

    @Test
    void saveSlots_FromOrToHasMinutesDifferentFromZero_ThrowsSlotValidationException() {
        InterviewerDto interviewerDto = MockBuilder.mockInterviewerDto();
        InterviewerSlotDto interviewerSlotDto = MockBuilder.mockInterviewerSlotDto();
        interviewerSlotDto.getSlotTimeDto().setFrom(LocalTime.of(9, 25, 0, 0));
        interviewerSlotDto.getSlotTimeDto().setTo(LocalTime.of(10, 8, 0, 0));

        Mockito.when(repository.existsByInterviewerId(ArgumentMatchers.anyString())).thenReturn(false);

        SlotValidationException slotValidationException = Assertions.assertThrows(SlotValidationException.class,
                () -> this.service.saveSlot(interviewerDto, interviewerSlotDto));

        Assertions.assertEquals("Minutes or/and seconds must be zero", slotValidationException.getMessage());
    }

    @Test
    void saveSlots_FromHasMoreThanOneHourDifferentToTo_SavesListOfSlotsWithOneHourOfInterval() {
        InterviewerDto interviewerDto = MockBuilder.mockInterviewerDto();
        InterviewerSlotDto interviewerSlotDto = MockBuilder.mockInterviewerSlotDto();
        interviewerSlotDto.getSlotTimeDto().setFrom(LocalTime.of(9, 0, 0, 0));
        interviewerSlotDto.getSlotTimeDto().setTo(LocalTime.of(12, 9, 0, 0));

        Mockito.when(repository.existsByInterviewerId(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(InterviewerSlotDocument.builder().build());

        this.service.saveSlot(interviewerDto, interviewerSlotDto);

        Mockito.verify(this.repository, Mockito.atLeastOnce()).save(interviewerSlotDocumentCaptor.capture());

        InterviewerSlotDocument documentCaptorValue = interviewerSlotDocumentCaptor.getValue();
        Assertions.assertEquals(4, documentCaptorValue.getSlotList().get(0).getSlotTimeList().size());
    }

    @Test
    void updateSlots_HasFromAfterResourceSaved_ThrowsSlotValidationException() {
        InterviewerSlotDocument interviewerSlotDocument = MockBuilder.mockInterviewerSlotDocument();
        LocalTime from = LocalTime.of(9, 0, 0, 0);
        LocalTime to = LocalTime.of(10, 0, 0, 0);
        LocalDate date = LocalDate.of(2022, 11, 15);

        com.gabryellr.calendarapi.interviewer.model.SlotTime slotTime = com.gabryellr.calendarapi.interviewer.model.SlotTime.builder()
                .from(from)
                .to(to)
                .build();

        com.gabryellr.calendarapi.interviewer.model.Slot slot = com.gabryellr.calendarapi.interviewer.model.Slot.builder().date(date).slotTimeList(List.of(slotTime)).build();

        interviewerSlotDocument.setSlotList(List.of(slot));

        Mockito.when(repository.findAllByInterviewerId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(interviewerSlotDocument));

        InterviewerSlotDto interviewerSlotDto = MockBuilder.mockInterviewerSlotDto();
        interviewerSlotDto.setSlotTimeDto(com.gabryellr.calendarapi.interviewer.dto.SlotTimeDto.builder()
                .from(from)
                .to(to)
                .build());

        interviewerSlotDto.setDate(date);

        SlotValidationException exception = Assertions.assertThrows(SlotValidationException.class,
                () -> service.saveSlot(InterviewerDto.builder().id("id123").build(), interviewerSlotDto));

        Assertions.assertEquals("From 09:00 is after or equal than from's saved to the interviewer with Id interviewer123", exception.getMessage());
    }

    @Test
    void updateSlots_HasToBeforeResourceSaved_ThrowsSlotValidationException() {
        InterviewerSlotDocument interviewerSlotDocument = MockBuilder.mockInterviewerSlotDocument();
        LocalTime fromSaved = LocalTime.of(9, 0, 0, 0);
        LocalTime toSaved = LocalTime.of(10, 0, 0, 0);
        LocalDate date = LocalDate.of(2022, 11, 15);

        com.gabryellr.calendarapi.interviewer.model.SlotTime slotTime = SlotTime.builder()
                .from(fromSaved)
                .to(toSaved)
                .build();

        com.gabryellr.calendarapi.interviewer.model.Slot slot = Slot.builder().date(date).slotTimeList(List.of(slotTime)).build();

        interviewerSlotDocument.setSlotList(List.of(slot));

        Mockito.when(repository.findAllByInterviewerId(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(interviewerSlotDocument));

        InterviewerSlotDto interviewerSlotDto = MockBuilder.mockInterviewerSlotDto();
        interviewerSlotDto.setSlotTimeDto(SlotTimeDto.builder()
                .from(fromSaved.minusHours(1L))
                .to(toSaved.minusHours(1L))
                .build());

        interviewerSlotDto.setDate(date);

        SlotValidationException exception = Assertions.assertThrows(SlotValidationException.class,
                () -> service.saveSlot(InterviewerDto.builder().id("id123").build(), interviewerSlotDto));

        Assertions.assertEquals("To 09:00 is before or equal than last slot saved to the interviewer with id interviewer123", exception.getMessage());
    }

    @Test
    void findAllInterviewersAvailable_InterviewersWithMatchTimes_ReturnsListOfInterviewers() {
        InterviewerSlotDocument interviewerSlotDocument = MockBuilder.mockInterviewerSlotDocument();
        LocalDate date = LocalDate.of(2022, 11, 15);
        LocalTime localTime9Am = LocalTime.of(9, 0, 0, 0);
        LocalTime localTime10Am = LocalTime.of(10, 0, 0, 0);
        LocalTime localTime11Am = LocalTime.of(11, 0, 0, 0);
        LocalTime localTime12Pm = LocalTime.of(12, 0, 0, 0);

        SlotTime slotTimeMatchTime = SlotTime.builder().from(localTime9Am)
                .to(localTime10Am)
                .status(StatusEnum.AVAILABLE)
                .slotTimeId("SlotTimeMatch123")
                .build();

        SlotTime slotTimeNotMatchTime = SlotTime.builder().from(localTime11Am)
                .to(localTime12Pm)
                .status(StatusEnum.AVAILABLE)
                .slotTimeId("SlotTimeNotMatch123")
                .build();

        Slot slot = Slot.builder().slotTimeList(List.of(slotTimeMatchTime, slotTimeNotMatchTime)).date(date)
                .build();

        interviewerSlotDocument.setSlotList(List.of(slot));

        Mockito.when(repository.findAllBySlotListDateAndSlotListSlotTimeListStatus(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(interviewerSlotDocument));

        List<InterviewerAvailabilityDto> allInterviewersAvailable = service.findAllInterviewersAvailable(date, localTime9Am, localTime10Am);

        Assertions.assertEquals(1, allInterviewersAvailable.size());
        List<SlotTimeDto> slotTimeDtoList = allInterviewersAvailable.get(0).getSlotTimeDtoList();

        Assertions.assertEquals("SlotTimeMatch123", slotTimeDtoList.get(0).getSlotTimeId());
        Assertions.assertNotEquals("SlotTimeNotMatch123", slotTimeDtoList.get(0).getSlotTimeId());
        Assertions.assertEquals(localTime9Am, slotTimeDtoList.get(0).getFrom());
        Assertions.assertEquals(localTime10Am, slotTimeDtoList.get(0).getTo());
    }
}