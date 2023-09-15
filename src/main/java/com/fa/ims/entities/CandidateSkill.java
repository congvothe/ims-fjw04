package com.fa.ims.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CandidateSkill extends AbstractAuditingEntity {
    @EmbeddedId
    private CandidateSkillId id;

    @MapsId(value = "candidateId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @MapsId(value = "skillId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private Boolean isActive;

    public CandidateSkill(Candidate candidate, Skill skill, Boolean isActive) {
        this.id = new CandidateSkillId(candidate.getId(), skill.getId());
        this.candidate = candidate;
        this.skill = skill;
        this.isActive = isActive;
    }
}
