package com.fa.ims.service.impl;

import com.fa.ims.entities.User;
import com.fa.ims.entities.UserInterview;
import com.fa.ims.repository.UserInterviewRepository;
import com.fa.ims.service.UserInterviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInterviewServiceImpl implements UserInterviewService {

    private final UserInterviewRepository userInterviewRepository;

    @Override
    public Map<Long, String> mapUserNames(List<Long> interviewIds) {
        Map<Long, String> interviewIdAndUserFullName = new HashMap<>();
        interviewIds.forEach(interviewId -> {
            List<UserInterview> userInterviews = userInterviewRepository
                    .findAllByInterview_IdAndUser_Role_RoleAndIsActiveTrue(interviewId, "Interviewer");
            String allUsersByRoleInterviewer = userInterviews.stream()
                    .map(UserInterview::getUser)
                    .map(User::getFullName)
                    .collect(Collectors.joining(", "));
            interviewIdAndUserFullName.put(interviewId, allUsersByRoleInterviewer);
        });
        return interviewIdAndUserFullName;
    }

    @Override
    public String getAllInterviewers(Long interviewId) {
        List<String> usernameInterviews = new ArrayList<>();
        List<UserInterview> userInterviews = userInterviewRepository
                .findAllByInterview_IdAndUser_Role_RoleAndIsActiveTrue(interviewId, "Interviewer");
        userInterviews.forEach(userInterview -> usernameInterviews.add(userInterview.getUser().getUsername()));
        return String.join(", ", usernameInterviews);
    }
}
