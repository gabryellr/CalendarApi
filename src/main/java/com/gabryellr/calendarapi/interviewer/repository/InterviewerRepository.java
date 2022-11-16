package com.gabryellr.calendarapi.interviewer.repository;

import com.gabryellr.calendarapi.interviewer.entities.InterviewerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewerRepository extends MongoRepository<InterviewerDocument, String> {

    boolean existsByName(String name);

}