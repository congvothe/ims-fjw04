package com.fa.ims.service;

import com.fa.ims.dto.JobDto;
import com.fa.ims.entities.Job;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface JobService extends BaseService<Job> {
    Page<JobDto> findAll(String search, LocalDate date, String status, int page, int size);

    Job create(JobDto jobDto);

    List<String> jobTitleList();

    JobDto getInformationJob(Long id);

    void update(JobDto jobDto);

    void delete(Long id);

}
