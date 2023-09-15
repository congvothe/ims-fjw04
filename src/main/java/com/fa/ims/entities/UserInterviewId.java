package com.fa.ims.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserInterviewId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "interview_id")
    private Long interviewId;
}
