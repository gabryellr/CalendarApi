package com.gabryellr.calendarapi.interviewer.repository;

import com.gabryellr.calendarapi.interviewer.entities.InterviewerSlotDocument;
import com.gabryellr.calendarapi.interviewer.status.StatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewerSlotRepository extends MongoRepository<InterviewerSlotDocument, String> {

    Optional<InterviewerSlotDocument> findAllByInterviewerId(String id);

    Optional<InterviewerSlotDocument> findAllByInterviewerIdAndSlotListDate(String id, LocalDate date);

    List<InterviewerSlotDocument> findAllBySlotListDateAndSlotListSlotTimeListStatus(LocalDate date, StatusEnum status);

    boolean existsByInterviewerId(String id);
}
