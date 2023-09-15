package com.fa.ims.service.impl;

import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.InterviewRepository;
import com.fa.ims.service.CandidateInterviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CandidateInterviewServiceImpl implements CandidateInterviewService {

    private final InterviewRepository interviewRepository;

    @Override
    public Map<Long, String> mapCandidateName(List<Long> interviewIds) {
        Map<Long, String> interviewIdAndCandidateFullName = new HashMap<>();
        interviewIds.forEach(interviewId -> interviewIdAndCandidateFullName
                .put(interviewId, interviewRepository.findByIdAndDeletedFalse(interviewId)
                        .orElseThrow(ResourceNotFoundException::new)
                        .getCandidate()
                        .getFullName()));
        return interviewIdAndCandidateFullName;
    }
}
