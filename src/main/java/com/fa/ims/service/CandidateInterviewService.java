package com.fa.ims.service;

import java.util.List;
import java.util.Map;

public interface CandidateInterviewService {
    Map<Long, String> mapCandidateName(List<Long> interviewIds);
}
