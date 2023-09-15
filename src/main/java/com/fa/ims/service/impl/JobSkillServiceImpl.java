package com.fa.ims.service.impl;

import com.fa.ims.entities.JobSkill;
import com.fa.ims.entities.Skill;
import com.fa.ims.repository.JobSkillRepository;
import com.fa.ims.service.JobSkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobSkillServiceImpl implements JobSkillService {
    private final JobSkillRepository jobSkillRepository;

    @Override
    public Map<Long, String> mapSkillNames(List<Long> jobIds) {
        Map<Long, String> jobIdAndSkillName = new HashMap<>();
        jobIds.forEach(jobId -> {
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob_IdAndIsActiveTrue(jobId);
            String allSkillNames = jobSkills.stream()
                    .map(JobSkill::getSkill)
                    .map(Skill::getName)
                    .collect(Collectors.joining(", "));
            jobIdAndSkillName.put(jobId, allSkillNames);
        });
        return jobIdAndSkillName;
    }
}
