package com.fa.ims.service.impl;

import com.fa.ims.entities.JobLevel;
import com.fa.ims.entities.Level;
import com.fa.ims.repository.JobLevelRepository;
import com.fa.ims.service.JobLevelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobLevelServiceImpl implements JobLevelService {
    private final JobLevelRepository jobLevelRepository;

    @Override
    public Map<Long, String> mapLevelNames(List<Long> jobIds) {
        Map<Long, String> jobIdAndLevelName = new HashMap<>();
        jobIds.forEach(jobId -> {
            List<JobLevel> jobLevels = jobLevelRepository.findAllByJob_IdAndIsActiveTrue(jobId);
            String allLevelNames = jobLevels.stream()
                    .map(JobLevel::getLevel)
                    .map(Level::getName)
                    .collect(Collectors.joining(", "));
            jobIdAndLevelName.put(jobId, allLevelNames);
        });
        return jobIdAndLevelName;
    }
}
