package com.fa.ims.repository;

import com.fa.ims.entities.Job;
import com.fa.ims.entities.JobSkill;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends BaseRepository<JobSkill, Long> {
    List<JobSkill> findAllByJob_IdAndIsActiveTrue(Long id);
    List<JobSkill> findAllByJobAndIsActiveTrue(Job job);
}
