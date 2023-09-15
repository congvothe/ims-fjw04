package com.fa.ims.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class JobSkill extends AbstractAuditingEntity {
    @EmbeddedId
    private JobSkillId id;

    @MapsId("jobId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private Boolean isActive;

    public JobSkill(Job job, Skill skill, Boolean isActive) {
        this.id = new JobSkillId(job.getId(), skill.getId());
        this.job = job;
        this.skill = skill;
        this.isActive = isActive;
    }
}
