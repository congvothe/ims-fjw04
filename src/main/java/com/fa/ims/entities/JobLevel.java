package com.fa.ims.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class JobLevel extends AbstractAuditingEntity {
    @EmbeddedId
    private JobLevelId id;

    @MapsId("jobId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @MapsId("levelId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    private Boolean isActive;

    public JobLevel(Job job, Level level, Boolean isActive) {
        this.id = new JobLevelId(job.getId(), level.getId());
        this.job = job;
        this.level = level;
        this.isActive = isActive;
    }
}
