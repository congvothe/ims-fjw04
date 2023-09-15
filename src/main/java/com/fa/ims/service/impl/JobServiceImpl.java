package com.fa.ims.service.impl;

import com.fa.ims.dto.JobDto;
import com.fa.ims.entities.*;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.*;
import com.fa.ims.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final LevelRepository levelRepository;
    private final SkillRepository skillRepository;
    private final JobLevelRepository jobLevelRepository;
    private final JobSkillRepository jobSkillRepository;
    private final StatusRepository statusRepository;

    @Override
    public Page<JobDto> findAll(String search, LocalDate date, String status, int page, int size) {
        Specification<Job> specification = undeletedSpec();
        if (StringUtils.hasText(search)) {
            String keyword = search.trim().toUpperCase();
            Specification<Job> searchByKeyword = (root, query, builder) -> builder.or(
                    builder.like(builder.upper(root.get("title")), "%" + keyword + "%")
            );
            Specification<Job> searchBySkillOrLevel = joinTableJob(keyword);
            searchByKeyword = searchByKeyword.or(searchBySkillOrLevel);
            specification = specification.and(searchByKeyword);
        }
        if (date != null) {
            Specification<Job> searchByDate = (root, query, builder) ->
                    builder.or(builder.equal(root.get("startDate"), date),
                               builder.equal(root.get("endDate"), date));
            specification = specification.and(searchByDate);
        }
        if (StringUtils.hasText(status)) {
            Specification<Job> searchByStatus = (root, query, builder) -> builder.equal(root.join("status")
                    .get("name"), status);
            specification = specification.and(searchByStatus);
        }
        Page<Job> jobPage = jobRepository.findAll(specification,
                PageRequest.of(page, size, Sort.by("lastModifiedDate").descending()));
        return jobPage.map(JobDto::new);
    }

    @Transactional
    @Override
    public Job create(JobDto jobDto) {
        Job job = new Job();
        BeanUtils.copyProperties(jobDto, job);
        job.setDeleted(false);
        Status status = statusRepository.findByNameAndStage("Open", "Job")
                .orElseThrow(ResourceNotFoundException::new);
        job.setStatus(status);
        Set<JobLevel> jobLevels = Arrays.stream(jobDto.getLevelIds())
                .map(levelId -> new JobLevel(job,
                        levelRepository.findById(levelId).orElseThrow(ResourceNotFoundException::new),
                        true))
                .collect(Collectors.toSet());
        Set<JobSkill> jobSkills = Arrays.stream(jobDto.getSkillIds())
                .map(skillId -> new JobSkill(job,
                        skillRepository.findById(skillId).orElseThrow(ResourceNotFoundException::new),
                        true))
                .collect(Collectors.toSet());
        job.setJobLevels(jobLevels);
        job.setJobSkills(jobSkills);
        return jobRepository.save(job);
    }

    @Override
    public List<String> jobTitleList() {
        return jobRepository.findAllByDeletedFalse().stream()
                .map(Job::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public JobDto getInformationJob(Long id) {
        JobDto jobDto = new JobDto();
        Job job = jobRepository.findByIdAndDeletedFalse(id).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(job, jobDto);
        jobDto.setStatusId(job.getStatus().getId());
        List<JobLevel> jobLevels = jobLevelRepository.findAllByJobAndIsActiveTrue(job);
        Long[] levelIds = jobLevels.stream()
                .map(JobLevel::getLevel)
                .map(Level::getId)
                .toArray(Long[]::new);
        List<JobSkill> jobSkills = jobSkillRepository.findAllByJobAndIsActiveTrue(job);
        Long[] skillIds = jobSkills.stream()
                .map(JobSkill::getSkill)
                .map(Skill::getId)
                .toArray(Long[]::new);
        jobDto.setLevelIds(levelIds);
        jobDto.setSkillIds(skillIds);
        return jobDto;
    }

    @Transactional
    @Override
    public void update(JobDto jobDto) {
        Job job = jobRepository.findByIdAndDeletedFalse(jobDto.getId()).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(jobDto, job);
        job.setStatus(statusRepository.findById(jobDto.getStatusId()).orElseThrow(ResourceNotFoundException::new));
        List<Long> levelIds = jobLevelRepository.findAllByJobAndIsActiveTrue(job).stream()
                .map(JobLevel::getLevel)
                .map(Level::getId)
                .collect(Collectors.toList());
        List<Long> newLevelIds = Arrays.stream(jobDto.getLevelIds()).collect(Collectors.toList());
        Set<JobLevel> jobLevels = newLevelIds.stream()
                .filter(newLevelId -> !levelIds.contains(newLevelId))
                .map(newLevelId -> new JobLevel(job, new Level(newLevelId),true))
                .collect(Collectors.toSet());
        List<Long> skillIds = jobSkillRepository.findAllByJobAndIsActiveTrue(job).stream()
                .map(JobSkill::getSkill)
                .map(Skill::getId)
                .collect(Collectors.toList());
        List<Long> newSkillIds = Arrays.stream(jobDto.getSkillIds()).collect(Collectors.toList());
        Set<JobSkill> jobSkills = newSkillIds.stream()
                .filter(newSkillId -> !skillIds.contains(newSkillId))
                .map(newSkillId -> new JobSkill(job, new Skill(newSkillId),true))
                .collect(Collectors.toSet());
        job.setJobLevels(jobLevels);
        job.setJobSkills(jobSkills);
        jobRepository.save(job);
        List<Long> inactiveJobLevels = levelIds.stream()
                .filter(levelId -> !newLevelIds.contains(levelId))
                .collect(Collectors.toList());
        inactiveJobLevels.forEach(levelId -> {
            JobLevel jobLevel = new JobLevel(job, new Level(levelId), false);
            jobLevelRepository.save(jobLevel);
        });
        List<Long> inactiveJobSkills = skillIds.stream()
                .filter(skillId -> !newSkillIds.contains(skillId))
                .collect(Collectors.toList());
        inactiveJobSkills.forEach(skillId -> {
            JobSkill jobSkill = new JobSkill(job, new Skill(skillId), false);
            jobSkillRepository.save(jobSkill);
        });
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Job job = jobRepository.findByIdAndDeletedFalse(id).orElseThrow(ResourceNotFoundException::new);
        job.setDeleted(true);
        jobRepository.save(job);
        Set<JobLevel> jobLevels = job.getJobLevels();
        jobLevels.forEach(jobLevel -> {
            jobLevel.setIsActive(false);
            jobLevelRepository.save(jobLevel);
        });
        Set<JobSkill> jobSkills = job.getJobSkills();
        jobSkills.forEach(jobSkill -> {
            jobSkill.setIsActive(false);
            jobSkillRepository.save(jobSkill);
        });
    }

    private Specification<Job> joinTableJob(String keyword) {
        Specification<Job> searchBySkill = (root, query, builder) -> {
            query.distinct(true);
            Join<Job, JobSkill> jobSkillJoin = root.join("jobSkills");
            Join<JobSkill, Skill> skillJoin = jobSkillJoin.join("skill");
            return builder.and(builder.equal(jobSkillJoin.get("isActive"), true),
                               builder.like(builder.upper(skillJoin.get("name")), "%" + keyword + "%"));
        };
        Specification<Job> searchByLevel = (root, query, builder) -> {
            query.distinct(true);
            Join<Job, JobLevel> jobLevelJoin = root.join("jobLevels");
            Join<JobLevel, Level> levelJoin = jobLevelJoin.join("level");
            return builder.and(builder.equal(jobLevelJoin.get("isActive"), true),
                               builder.like(builder.upper(levelJoin.get("name")), "%" + keyword + "%"));
        };
        return searchBySkill.or(searchByLevel);
    }
}
