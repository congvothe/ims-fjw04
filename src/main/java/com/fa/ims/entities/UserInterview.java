package com.fa.ims.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserInterview extends AbstractAuditingEntity {
    @EmbeddedId
    private UserInterviewId id;

    @MapsId(value = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId(value = "interviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    private Boolean isActive;

    public UserInterview(User user, Interview interview, Boolean isActive) {
        this.id = new UserInterviewId(user.getId(), interview.getId());
        this.user = user;
        this.interview = interview;
        this.isActive = isActive;
    }
}
