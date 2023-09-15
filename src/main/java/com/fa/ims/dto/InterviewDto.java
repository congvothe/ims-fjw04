package com.fa.ims.dto;

import com.fa.ims.entities.Interview;
import com.fa.ims.entities.UserInterview;
import com.fa.ims.entities.UserInterviewId;
import com.fa.ims.enums.InterviewResult;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewDto {
    private Long id;

    @NotBlank(message = "{interview.title.required}")
    private String title;

    @NotNull(message = "{interview.scheduleDate.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    private LocalTime scheduleFrom;

    private LocalTime scheduleTo;

    private String location;

    private String notes;

    private String meetingId;

    private Long statusId;

    private InterviewResult result;

    @NotEmpty(message = "{interview.interviewer.required}")
    private Long[] userIds;

    @NotNull(message = "{interview.recruiter.required}")
    private Long recruiterId;

    @NotNull(message = "{interview.candidate.required}")
    private Long candidateId;

    public InterviewDto(Interview interview) {
        this.id = interview.getId();
        this.title = interview.getTitle();
        this.scheduleDate = interview.getScheduleDate();
        this.scheduleFrom = interview.getScheduleFrom();
        this.scheduleTo = interview.getScheduleTo();
        this.statusId = interview.getStatus().getId();
        this.result = interview.getResult();
        this.candidateId = interview.getCandidate().getId();
        this.userIds = interview.getUserInterviews().stream()
                .map(UserInterview::getId)
                .map(UserInterviewId::getUserId)
                .toArray(Long[]::new);
    }

    public InterviewDto(Long id, Long candidateId) {
        this.id = id;
        this.candidateId = candidateId;
    }
}
