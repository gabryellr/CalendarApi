package com.gabryellr.calendarapi.candidate.repository;

import com.gabryellr.calendarapi.candidate.entities.CandidateSlotDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateSlotRepository extends MongoRepository<CandidateSlotDocument, String> {

    Optional<CandidateSlotDocument> findAllByCandidateId(String id);

    boolean existsByCandidateId(String id);
}
