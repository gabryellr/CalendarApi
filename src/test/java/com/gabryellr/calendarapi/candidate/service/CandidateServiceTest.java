package com.gabryellr.calendarapi.candidate.service;

import com.gabryellr.calendarapi.candidate.dto.CandidateDto;
import com.gabryellr.calendarapi.candidate.dto.CandidateSlotDto;
import com.gabryellr.calendarapi.candidate.entities.CandidateDocument;
import com.gabryellr.calendarapi.candidate.mapper.CandidateMapper;
import com.gabryellr.calendarapi.candidate.repository.CandidateRepository;
import com.gabryellr.calendarapi.exception.exceptions.ResourceAlreadyExistsException;
import com.gabryellr.calendarapi.exception.exceptions.ResourceNotFoundException;
import com.gabryellr.calendarapi.utils.MockBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

class CandidateServiceTest {

    @Mock
    private CandidateRepository repository;

    @Mock
    private CandidateSlotService candidateSlotService;

    private final CandidateMapper mapper = Mappers.getMapper(CandidateMapper.class);

    @InjectMocks
    private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        this.candidateService = new CandidateService(repository, mapper, candidateSlotService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create_ValidCandidateDto_ReturnsCandidateDtoCreated() {
        CandidateDto candidateDto = MockBuilder.mockCandidateDto();
        CandidateDocument candidateSaved = MockBuilder.mockCandidateDocument();

        Mockito.when(repository.existsByName(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(candidateSaved);

        CandidateDto candidateSavedDto = this.candidateService.create(candidateDto);

        Mockito.verify(repository, Mockito.atLeastOnce()).save(ArgumentMatchers.any());

        Assertions.assertEquals(candidateDto.getName(), candidateSavedDto.getName());
        Assertions.assertEquals(candidateSaved.getId(), candidateSavedDto.getId());
    }

    @Test
    void create_ExistsCandidateName_ThrowsResourceAlreadyExistsException() {
        CandidateDto candidateDto = MockBuilder.mockCandidateDto();
        Mockito.when(repository.existsByName(ArgumentMatchers.anyString())).thenReturn(true);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> this.candidateService.create(candidateDto));

        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());

    }

    @Test
    void findAll_ReturnsList() {
        List<CandidateDocument> candidateDocumentList = List.of(MockBuilder.mockCandidateDocument());
        Mockito.when(repository.findAll()).thenReturn(candidateDocumentList);

        List<CandidateDto> candidateDtoList = this.candidateService.findAll();

        Assertions.assertEquals(candidateDocumentList.size(), candidateDtoList.size());
        Mockito.verify(repository, Mockito.atLeastOnce()).findAll();
    }

    @Test
    void findById_ExistsResource_ReturnsCandidateDto() {
        Optional<CandidateDocument> candidateDocumentOptional = Optional.of(MockBuilder.mockCandidateDocument());
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(candidateDocumentOptional);

        CandidateDto candidateDto = this.candidateService.findById(candidateDocumentOptional.get().getId());

        Assertions.assertEquals(candidateDocumentOptional.get().getId(), candidateDto.getId());
        Assertions.assertEquals(candidateDocumentOptional.get().getName(), candidateDto.getName());
    }

    @Test
    void findAllSlotsByCandidateId_ValidCandidateId_ReturnsCandidateSlotDto() {
        Optional<CandidateDocument> candidateDocumentOptional = Optional.of(MockBuilder.mockCandidateDocument());
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(candidateDocumentOptional);
        Mockito.when(candidateSlotService.findAllSlotsByCandidateId(ArgumentMatchers.anyString())).thenReturn(MockBuilder.mockCandidateSlotDto());

        CandidateSlotDto allSlotsByCandidateId = this.candidateService.findAllSlotsByCandidateId(candidateDocumentOptional.get().getId());

        Mockito.verify(candidateSlotService, Mockito.atLeastOnce()).findAllSlotsByCandidateId(ArgumentMatchers.anyString());
        Assertions.assertNotNull(allSlotsByCandidateId);
    }

    @Test
    void create_ExistsCandidate_ShouldSaveSlots() {
        Optional<CandidateDocument> candidateDocumentOptional = Optional.of(MockBuilder.mockCandidateDocument());
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(candidateDocumentOptional);

        this.candidateService.createSlot(candidateDocumentOptional.get().getId(), MockBuilder.mockCandidateSlotDto());
        Mockito.verify(candidateSlotService, Mockito.atLeastOnce()).saveSlots(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void create_CandidateNotExists_ThrowsResourceNotFoundException() {
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.candidateService.createSlot("123", MockBuilder.mockCandidateSlotDto()));
        Mockito.verify(candidateSlotService, Mockito.never()).saveSlots(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void findAllInterviewerAvailabilityByCandidateId_ValidCandidateId_ShouldCallCandidateSlotService() {
        Optional<CandidateDocument> candidateDocumentOptional = Optional.of(MockBuilder.mockCandidateDocument());
        Mockito.when(repository.findById(ArgumentMatchers.anyString())).thenReturn(candidateDocumentOptional);

        this.candidateService.findAllInterviewerAvailabilityByCandidateId(candidateDocumentOptional.get().getId());
        Mockito.verify(candidateSlotService, Mockito.atLeastOnce()).findAllInterviewerAvailabilityByCandidate(ArgumentMatchers.any());
    }
}