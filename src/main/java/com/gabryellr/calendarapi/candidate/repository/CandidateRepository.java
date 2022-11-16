package com.gabryellr.calendarapi.candidate.repository;

import com.gabryellr.calendarapi.candidate.entities.CandidateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends MongoRepository<CandidateDocument, String> {

    boolean existsByName(String name);

}