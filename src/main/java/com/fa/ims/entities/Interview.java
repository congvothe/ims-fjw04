package com.fa.ims.entities;

import com.fa.ims.enums.InterviewResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Interview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate scheduleDate;

    private LocalTime scheduleFrom;

    private LocalTime scheduleTo;

    private String location;

    private String notes;

    private String meetingId;

    @Enumerated(EnumType.STRING)
    private InterviewResult result;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private Set<UserInterview> userInterviews;

    @OneToOne(fetch = FetchType.LAZY)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    @OneToOne(mappedBy = "interview")
    private Offer offer;

    public Interview(Long id) {
        this.id = id;
    }
}
