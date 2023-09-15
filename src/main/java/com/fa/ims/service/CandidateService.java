package com.fa.ims.service;

import com.fa.ims.dto.CandidateDto;
import com.fa.ims.entities.Candidate;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CandidateService extends BaseService<Candidate> {
    Page<CandidateDto> findAll(String search, String status, int page, int size);

    Candidate create(CandidateDto candidateDto) throws IOException;

    CandidateDto getInformationCandidate(Long id);

    void update(CandidateDto candidateDto);

    void delete(Long id);

    List<Candidate> findAllAndDeletedFalseAndNotExistInterview();

    boolean existsByEmail(String email);

    Optional<Candidate> findById(Long id);

    List<CandidateDto> getCandidateDto();

    CandidateDto findDtoById(Long id);

    Map<Long, Candidate> mapCandidateInformation(List<Long> offerIds);
}
