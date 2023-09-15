package com.fa.ims.repository;

import com.fa.ims.entities.Job;
import com.fa.ims.entities.JobLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLevelRepository extends BaseRepository<JobLevel, Long> {
    List<JobLevel> findAllByJob_IdAndIsActiveTrue(Long id);
    List<JobLevel> findAllByJobAndIsActiveTrue(Job job);
}
