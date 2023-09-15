package com.fa.ims.repository;

import com.fa.ims.entities.Candidate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends BaseRepository<Candidate, Long> {
    Optional<Candidate> findByIdAndDeletedFalse(Long id);
    List<Candidate> findAllByDeletedFalseAndInterviewNullOrderByLastModifiedDateDesc();
    List<Candidate> findAllByDeletedFalse();
    boolean existsByEmail(String email);
}
