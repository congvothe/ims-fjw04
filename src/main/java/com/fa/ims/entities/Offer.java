package com.fa.ims.entities;

import com.fa.ims.enums.ContractType;
import com.fa.ims.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Offer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    private Department department;

    private LocalDate contractStartFrom;

    private LocalDate contractStartTo;

    private Double basicSalary;

    private String note;

    @OneToOne(fetch = FetchType.LAZY)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    private Skill skill;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private Set<UserOffer> userOffers;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    @OneToOne
    private Interview interview;
}
