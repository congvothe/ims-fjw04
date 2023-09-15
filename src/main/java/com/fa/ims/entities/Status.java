package com.fa.ims.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String stage;

    @OneToMany(mappedBy = "status")
    private List<Job> jobs;

    @OneToMany(mappedBy = "status")
    private List<Candidate> candidates;

    @OneToMany(mappedBy = "status")
    private List<Interview> interviews;

    @OneToMany(mappedBy = "status")
    private List<Offer> offers;
}
