package com.fa.ims.entities;

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
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double salaryFrom;

    private Double salaryTo;

    private String address;

    private String description;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<JobLevel> jobLevels;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<JobSkill> jobSkills;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;
}
