package com.fa.ims.repository;

import com.fa.ims.entities.Job;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends BaseRepository<Job,Long> {
    List<Job> findAllByDeletedFalse();
    Optional<Job> findByIdAndDeletedFalse(Long id);
}
