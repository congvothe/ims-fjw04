package com.fa.ims.service.impl;

import com.fa.ims.dto.InterviewDto;
import com.fa.ims.entities.*;
import com.fa.ims.enums.InterviewResult;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.*;
import com.fa.ims.service.InterviewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final UserInterviewRepository userInterviewRepository;
    private final StatusRepository statusRepository;

    @Override
    public Page<InterviewDto> findAll(String search, LocalDate date, InterviewResult result,
                                      String status, int page, int size) {
        Specification<Interview> specification = undeletedSpec();
        if (StringUtils.hasText(search)) {
            String keyword = search.trim().toUpperCase();
            Specification<Interview> searchByKeyword = (root, query, builder) -> builder.or(
                    builder.like(builder.upper(root.get("title")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.join("candidate").get("fullName")), "%" + keyword + "%")
            );
            Specification<Interview> searchByInterviewer = joinTableInterview(keyword);
            searchByKeyword = searchByKeyword.or(searchByInterviewer);
            specification = specification.and(searchByKeyword);
        }
        if (date != null) {
            Specification<Interview> searchByDate = (root, query, builder) ->
                    builder.equal(root.get("scheduleDate"), date);
            specification = specification.and(searchByDate);
        }
        if (result != null) {
            Specification<Interview> searchByResult = (root, query, builder) ->
                    builder.equal(root.get("result"), result);
            specification = specification.and(searchByResult);
        }
        if (StringUtils.hasText(status)) {
            Specification<Interview> searchByStatus = (root, query, builder) ->
                    builder.equal(root.join("status").get("name"), status);
            specification = specification.and(searchByStatus);
        }
        Page<Interview> interviewPage = interviewRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by("lastModifiedDate").descending()));
        return interviewPage.map(InterviewDto::new);
    }

    @Transactional
    @Override
    public Interview create(InterviewDto interviewDto) {
        Interview interview = new Interview();
        interview.setDeleted(false);
        BeanUtils.copyProperties(interviewDto, interview);
        Candidate candidate = candidateRepository.findById(interviewDto.getCandidateId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setCandidate(candidate);
        Status status = statusRepository.findByNameAndStage("Waiting for interview", "Interview")
                .orElseThrow(ResourceNotFoundException::new);
        interview.setStatus(status);
        interview.setResult(InterviewResult.OPEN);
        Set<UserInterview> userInterviews = Arrays.stream(interviewDto.getUserIds())
                .map(userId -> new UserInterview(
                        userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new),
                        interview, true))
                .collect(Collectors.toSet());
        User userByRoleInterviewer = userRepository.findById(interviewDto.getRecruiterId())
                .orElseThrow(ResourceNotFoundException::new);
        userInterviews.add(new UserInterview(userByRoleInterviewer, interview, true));
        interview.setUserInterviews(userInterviews);
        interviewRepository.save(interview);
        candidate.setStatus(status);
        candidateRepository.save(candidate);
        return interview;
    }

    @Override
    public InterviewDto getInformationInterview(Long id) {
        InterviewDto interviewDto = new InterviewDto();
        Interview interview = interviewRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(interview, interviewDto);
        interviewDto.setStatusId(interview.getStatus().getId());
        interviewDto.setCandidateId(interview.getCandidate().getId());
        List<UserInterview> userInterviews = userInterviewRepository.findAllByInterviewAndIsActiveTrue(interview);
        Long[] userIds = userInterviews.stream()
                .map(UserInterview::getUser)
                .map(User::getId)
                .toArray(Long[]::new);
        interviewDto.setUserIds(userIds);
        List<Long> recruiterIds = userInterviews.stream()
                .map(UserInterview::getUser)
                .filter(user -> user.getRole().getRole().equals("Recruiter"))
                .map(User::getId)
                .collect(Collectors.toList());
        Long recruiterId = recruiterIds.get(0);
        interviewDto.setRecruiterId(recruiterId);
        return interviewDto;
    }

    @Transactional
    @Override
    public void update(InterviewDto interviewDto) {
        Interview interview = interviewRepository.findByIdAndDeletedFalse(interviewDto.getId())
                .orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(interviewDto, interview, "result");
        Candidate candidate = candidateRepository.findById(interviewDto.getCandidateId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setCandidate(candidate);
        List<Long> userIds = userInterviewRepository
                .findAllByInterviewAndIsActiveTrue(interview).stream()
                .map(UserInterview::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        List<Long> newUserIds = Arrays.stream(interviewDto.getUserIds()).collect(Collectors.toList());
        newUserIds.add(interviewDto.getRecruiterId());
        Set<UserInterview> userInterviews = newUserIds.stream()
                .filter(newUserId -> !userIds.contains(newUserId))
                .map(newUserId -> new UserInterview(new User(newUserId), interview, true))
                .collect(Collectors.toSet());
        interview.setUserInterviews(userInterviews);
        interviewRepository.save(interview);
        List<Long> inactiveInterviewSkills = userIds.stream()
                .filter(userId -> !newUserIds.contains(userId))
                .collect(Collectors.toList());
        inactiveInterviewSkills.forEach(userId -> {
            UserInterview userInterview = new UserInterview(new User(userId), interview, false);
            userInterviewRepository.save(userInterview);
        });
    }

    @Transactional
    @Override
    public void updateResultInterview(InterviewDto interviewDto) {
        Interview interview = interviewRepository.findByIdAndDeletedFalse(interviewDto.getId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setResult(interviewDto.getResult());
        interview.setNotes(interviewDto.getNotes());
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(interviewDto.getCandidateId())
                .orElseThrow(ResourceNotFoundException::new);
        if (interview.getResult().getLabel().equals("Fail")) {
            Status status = statusRepository.findByNameAndStage("Failed interview", "Interview")
                    .orElseThrow(ResourceNotFoundException::new);
            interview.setStatus(status);
            candidate.setStatus(status);
        }
        if (interview.getResult().getLabel().equals("Pass")) {
            Status status = statusRepository.findByNameAndStage("Passed Interview", "Interview")
                    .orElseThrow(ResourceNotFoundException::new);
            interview.setStatus(status);
            candidate.setStatus(status);
        }
        if (interview.getResult().getLabel().equals("Cancel")) {
            Status status = statusRepository.findByNameAndStage("Cancelled interview", "Interview")
                    .orElseThrow(ResourceNotFoundException::new);
            interview.setStatus(status);
            candidate.setStatus(status);
        }
        interviewRepository.save(interview);
        candidateRepository.save(candidate);
    }

    @Override
    public List<InterviewDto> candidateResultPass() {
        List<Interview> interviews = interviewRepository
                .findAllByResultAndDeletedFalseAndOfferNullOrderByLastModifiedDateDesc(InterviewResult.PASS);
        return interviews.stream().map(interview -> new InterviewDto(interview.getId(), interview.getCandidate().getId()))
                .collect(Collectors.toList());
    }

    private Specification<Interview> joinTableInterview(String keyword) {
        return (root, query, builder) -> {
            query.distinct(true);
            Join<Interview, UserInterview> userInterviewJoin = root.join("userInterviews");
            Join<UserInterview, User> userJoin = userInterviewJoin.join("user");
            Join<User, Role> roleJoin = userJoin.join("role");
            return builder.and(
                    builder.equal(userInterviewJoin.get("isActive"), true),
                    builder.like(builder.upper(userJoin.get("fullName")), "%" + keyword + "%"),
                    builder.equal(roleJoin.get("role"), "Interviewer")
            );
        };
    }
}
