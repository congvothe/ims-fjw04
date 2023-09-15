package com.fa.ims.service;

import java.util.List;
import java.util.Map;

public interface UserInterviewService {
    Map<Long, String> mapUserNames(List<Long> interviewIds);

    String getAllInterviewers(Long interviewId);
}
