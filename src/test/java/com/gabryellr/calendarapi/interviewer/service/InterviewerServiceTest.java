package com.gabryellr.calendarapi.interviewer.service;

import com.gabryellr.calendarapi.exception.exceptions.ResourceAlreadyExistsException;
import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerDto;
import com.gabryellr.calendarapi.interviewer.dto.InterviewerSlotDto;
import com.gabryellr.calendarapi.interviewer.entities.InterviewerDocument;
import com.gabryellr.calendarapi.interviewer.mapper.InterviewerMapper;
import com.gabryellr.calendarapi.interviewer.repository.InterviewerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

class InterviewerServiceTest {

    @Mock
    private InterviewerRepository repository;

    @Mock
    private InterviewerSlotService interviewerSlotService;

    private final InterviewerMapper mapper = Mappers.getMapper(InterviewerMapper.class);

    @InjectMocks
    private InterviewerService interviewerService;

    @BeforeEach
    void setUp() {
        this.interviewerService = new InterviewerService(repository, mapper, interviewerSlotService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create_AlreadyExistsByName_ResourceAlreadyExistsException() {
        Mockito.when(repository.existsByName(ArgumentMatchers.anyString())).thenReturn(true);

        ResourceAlreadyExistsException exception = Assertions.assertThrows(ResourceAlreadyExistsException.class,
                () -> interviewerService.create(InterviewerDto.builder().name("test").build()));

        Assertions.assertEquals("Interviewer with name test already exists.", exception.getMessage());
    }

    @Test
    void create_ValidInput_ShouldCallRepositorySave() {
        Mockito.when(repository.existsByName(ArgumentMatchers.anyString())).thenReturn(false);

        interviewerService.create(InterviewerDto.builder().name("test").build());

        Mockito.verify(repository, Mockito.atLeastOnce()).save(ArgumentMatchers.any());
    }

    @Test
    void findAll_ShouldCallRepositoryFindALl() {
        Mockito.when(repository.findAll()).thenReturn(List.of(InterviewerDocument.builder().build()));

        interviewerService.findAll();

        Mockito.verify(repository, Mockito.atLeastOnce()).findAll();
    }

    @Test
    void findById_IdNotExists_ThrowsResourceNotFoundException() {
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> interviewerService.findById("123"));

        Mockito.verify(repository, Mockito.atLeastOnce()).findById(ArgumentMatchers.anyString());
    }

    @Test
    void findById_IdExists_ReturnsInterviewerDto() {
        InterviewerDocument interviewerDocument = InterviewerDocument.builder().id("123").name("name123").build();
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(interviewerDocument));

        interviewerService.findById("123");
        Mockito.verify(repository, Mockito.atLeastOnce()).findById(ArgumentMatchers.anyString());
    }

    @Test
    void findAllSlotsByInterviewerId_IdExists_CallsInterviewerSlotServiceFindSlotsByInterviewerId() {
        InterviewerDocument interviewerDocument = InterviewerDocument.builder().id("123").name("name123").build();
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(interviewerDocument));

        interviewerService.findAllSlotsByInterviewerId("123");
        Mockito.verify(repository, Mockito.atLeastOnce()).findById(ArgumentMatchers.anyString());
        Mockito.verify(interviewerSlotService, Mockito.atLeastOnce()).findSlotsByInterviewerId(ArgumentMatchers.anyString());
    }

    @Test
    void createSlot_ValidId_CallsInterviewerSlotServiceSaveSlot() {
        InterviewerDocument interviewerDocument = InterviewerDocument.builder().id("123").name("name123").build();
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(interviewerDocument));
        InterviewerSlotDto interviewerSlotDto = InterviewerSlotDto.builder().build();

        interviewerService.createSlot("123", interviewerSlotDto);

        Mockito.verify(interviewerSlotService, Mockito.atLeastOnce()).saveSlot(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}