package com.fa.ims.entities;

import com.fa.ims.enums.Gender;
import com.fa.ims.enums.HighestLevel;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Candidate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private LocalDate dateOfBirth;

    private String address;

    private String phoneNumber;

    private Gender gender;

    private String cv;

    private Integer yearOfExperience;

    private HighestLevel highestLevel;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private Set<CandidateSkill> candidateSkills;

    @OneToOne(mappedBy = "candidate", fetch = FetchType.LAZY)
    private Interview interview;

    @OneToOne(mappedBy = "candidate", fetch = FetchType.LAZY)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;
}
