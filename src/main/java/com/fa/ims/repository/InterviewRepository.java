package com.fa.ims.repository;

import com.fa.ims.entities.Interview;
import com.fa.ims.enums.InterviewResult;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends BaseRepository<Interview, Long> {
    Optional<Interview> findByIdAndDeletedFalse(Long id);

    List<Interview> findAllByResultAndDeletedFalseAndOfferNullOrderByLastModifiedDateDesc(InterviewResult interviewResult);
}
