package com.fa.ims.service.impl;

import com.fa.ims.dto.JobDto;
import com.fa.ims.entities.*;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
//Linh test
@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private JobLevelRepository jobLevelRepository;

    @Mock
    private JobSkillRepository jobSkillRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    public void givenJobDto_whenCreateJob_thenReturnJobObject() {
        Status status = Status.builder()
                .id(1L)
                .name("Open")
                .stage("Job")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();
        Level level1 = Level.builder()
                .id(1L)
                .name("Fresher 1")
                .build();
        Level level2 = Level.builder()
                .id(2L)
                .name("Junior 2.1")
                .build();

        Long[] skillIds = {skill1.getId(), skill2.getId()};
        Long[] levelIds = {level1.getId(), level2.getId()};
        JobDto jobDto = JobDto.builder()
                .id(1L)
                .title("Java Job")
                .startDate(LocalDate.of(2023, 04, 01))
                .endDate(LocalDate.of(2023, 04, 23))
                .statusId(status.getId())
                .salaryFrom(500.0)
                .salaryTo(1000.0)
                .skillIds(skillIds)
                .levelIds(levelIds)
                .build();

        given(statusRepository.findByNameAndStage("Open", "Job")).willReturn(Optional.of(status));
        given(levelRepository.findById(skill1.getId())).willReturn(Optional.of(level1));
        given(levelRepository.findById(skill2.getId())).willReturn(Optional.of(level2));
        given(skillRepository.findById(skill1.getId())).willReturn(Optional.of(skill1));
        given(skillRepository.findById(skill2.getId())).willReturn(Optional.of(skill2));
        Job job = jobService.create(jobDto);
        assertThat(job).isNotNull();
        assertThat(job.getTitle()).isEqualTo(jobDto.getTitle());
        assertThat(job.getStartDate()).isEqualTo(jobDto.getStartDate());
        assertThat(job.getEndDate()).isEqualTo(jobDto.getEndDate());
        Assertions.assertThat(job.getJobSkills()).hasSize(2);
        Assertions.assertThat(job.getJobLevels()).hasSize(2);
    }

    @Test
    public void givenJobDto_whenCreateJob_thenThrowException() {
        Long invalidStatusId = 999L;
        Long invalidSkillId = 999L;
        Long invalidLevelId = 999L;

        Long[] skillIds = {invalidSkillId};
        Long[] levelIds = {invalidLevelId};

        JobDto jobDto = JobDto.builder()
                .title("Invalid Job")
                .statusId(invalidStatusId)
                .skillIds(skillIds)
                .levelIds(levelIds)
                .build();

        Assertions.assertThatThrownBy(() -> jobService.create(jobDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void givenJobId_whenGetInformationJob_thenReturnJobDto() {
        // Create a new Job
        Status status = Status.builder()
                .id(1L)
                .name("Open")
                .stage("Job")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();
        Level level1 = Level.builder()
                .id(1L)
                .name("Fresher 1")
                .build();
        Level level2 = Level.builder()
                .id(2L)
                .name("Junior 2.1")
                .build();

        Long[] skillIds = {skill1.getId(), skill2.getId()};
        Long[] levelIds = {level1.getId(), level2.getId()};
        JobDto jobDto = JobDto.builder()
                .id(1L)
                .title("Java Job")
                .startDate(LocalDate.of(2023, 04, 01))
                .endDate(LocalDate.of(2023, 04, 23))
                .statusId(status.getId())
                .salaryFrom(500.0)
                .salaryTo(1000.0)
                .skillIds(skillIds)
                .levelIds(levelIds)
                .build();

        given(statusRepository.findByNameAndStage("Open", "Job")).willReturn(Optional.of(status));
        given(levelRepository.findById(skill1.getId())).willReturn(Optional.of(level1));
        given(levelRepository.findById(skill2.getId())).willReturn(Optional.of(level2));
        given(skillRepository.findById(skill1.getId())).willReturn(Optional.of(skill1));
        given(skillRepository.findById(skill2.getId())).willReturn(Optional.of(skill2));
        Job job = jobService.create(jobDto);

        // List JobLevel
        List<JobLevel> jobLevels = new ArrayList<>();
        JobLevelId jobLevelId1 = JobLevelId.builder()
                .jobId(job.getId())
                .levelId(level1.getId())
                .build();
        JobLevel jobLevel1 = JobLevel.builder()
                .id(jobLevelId1)
                .job(job)
                .level(level1)
                .isActive(true)
                .build();
        JobLevelId jobLevelId2 = JobLevelId.builder()
                .jobId(job.getId())
                .levelId(level2.getId())
                .build();
        JobLevel jobLevel2 = JobLevel.builder()
                .id(jobLevelId2)
                .job(job)
                .level(level2)
                .isActive(true)
                .build();
        jobLevels.add(jobLevel1);
        jobLevels.add(jobLevel2);

        // List JobSkill
        List<JobSkill> jobSkills = new ArrayList<>();
        JobSkillId jobSkillId1 = JobSkillId.builder()
                .jobId(job.getId())
                .skillId(skill1.getId())
                .build();
        JobSkill jobSkill1 = JobSkill.builder()
                .id(jobSkillId1)
                .job(job)
                .skill(skill1)
                .isActive(true)
                .build();
        JobSkillId jobSkillId2 = JobSkillId.builder()
                .jobId(job.getId())
                .skillId(skill2.getId())
                .build();
        JobSkill jobSkill2 = JobSkill.builder()
                .id(jobSkillId2)
                .job(job)
                .skill(skill2)
                .isActive(true)
                .build();
        jobSkills.add(jobSkill1);
        jobSkills.add(jobSkill2);

        //Check get Information Job
        Long jobId = 1L;
        given(jobRepository.findByIdAndDeletedFalse(jobId)).willReturn(Optional.of(job));
        given(jobLevelRepository.findAllByJobAndIsActiveTrue(job)).willReturn(jobLevels);
        given(jobSkillRepository.findAllByJobAndIsActiveTrue(job)).willReturn(jobSkills);
        JobDto newJobDto = jobService.getInformationJob(jobId);
        assertThat(newJobDto).isNotNull();
    }

    @Test
    public void givenJobDto_whenUpdateJob_thenJobIsUpdated() {
        Status status = Status.builder()
                .id(1L)
                .name("Open")
                .stage("Job")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();
        Skill skill3 = Skill.builder()
                .id(3L)
                .name(".NET")
                .build();
        Level level1 = Level.builder()
                .id(1L)
                .name("Fresher 1")
                .build();
        Level level2 = Level.builder()
                .id(2L)
                .name("Junior 2.1")
                .build();
        Level level3 = Level.builder()
                .id(3L)
                .name("Senior 3.1")
                .build();

        Long[] skillIds = {skill1.getId(), skill2.getId()};
        Long[] levelIds = {level1.getId(), level2.getId()};

        JobDto jobDto = JobDto.builder()
                .id(1L)
                .title("Java Job")
                .startDate(LocalDate.of(2023, 04, 01))
                .endDate(LocalDate.of(2023, 04, 23))
                .statusId(status.getId())
                .salaryFrom(500.0)
                .salaryTo(1000.0)
                .skillIds(skillIds)
                .levelIds(levelIds)
                .build();

        given(statusRepository.findByNameAndStage("Open", "Job")).willReturn(Optional.of(status));
        given(levelRepository.findById(skill1.getId())).willReturn(Optional.of(level1));
        given(levelRepository.findById(skill2.getId())).willReturn(Optional.of(level2));
        given(skillRepository.findById(skill1.getId())).willReturn(Optional.of(skill1));
        given(skillRepository.findById(skill2.getId())).willReturn(Optional.of(skill2));
        Job job = jobService.create(jobDto);

        // Update a job existed
        // Set newSkillIds and newLevelIds to update
        Long[] newSkillIds = {skill1.getId(), skill3.getId()};
        Long[] newLevelIds = {level1.getId(), level3.getId()};
        JobDto jobDtoUpdate = JobDto.builder()
                .id(1L)
                .title("Java Job Update")
                .startDate(LocalDate.of(2023, 04, 01))
                .endDate(LocalDate.of(2023, 04, 23))
                .statusId(status.getId())
                .salaryFrom(1000.0)
                .salaryTo(2000.0)
                .skillIds(newSkillIds)
                .levelIds(newLevelIds)
                .build();

        // List JobLevel
        List<JobLevel> jobLevels = new ArrayList<>();
        JobLevelId jobLevelId1 = JobLevelId.builder()
                .jobId(job.getId())
                .levelId(level1.getId())
                .build();
        JobLevel jobLevel1 = JobLevel.builder()
                .id(jobLevelId1)
                .job(job)
                .level(level1)
                .isActive(true)
                .build();
        JobLevelId jobLevelId2 = JobLevelId.builder()
                .jobId(job.getId())
                .levelId(level2.getId())
                .build();
        JobLevel jobLevel2 = JobLevel.builder()
                .id(jobLevelId2)
                .job(job)
                .level(level2)
                .isActive(true)
                .build();
        jobLevels.add(jobLevel1);
        jobLevels.add(jobLevel2);

        // List JobSkill
        List<JobSkill> jobSkills = new ArrayList<>();
        JobSkillId jobSkillId1 = JobSkillId.builder()
                .jobId(job.getId())
                .skillId(skill1.getId())
                .build();
        JobSkill jobSkill1 = JobSkill.builder()
                .id(jobSkillId1)
                .job(job)
                .skill(skill1)
                .isActive(true)
                .build();
        JobSkillId jobSkillId2 = JobSkillId.builder()
                .jobId(job.getId())
                .skillId(skill2.getId())
                .build();
        JobSkill jobSkill2 = JobSkill.builder()
                .id(jobSkillId2)
                .job(job)
                .skill(skill2)
                .isActive(true)
                .build();
        jobSkills.add(jobSkill1);
        jobSkills.add(jobSkill2);

        given(statusRepository.findById(status.getId())).willReturn(Optional.of(status));
        given(jobRepository.findByIdAndDeletedFalse(jobDtoUpdate.getId())).willReturn(Optional.of(job));
        given(jobLevelRepository.findAllByJobAndIsActiveTrue(job)).willReturn(jobLevels);
        given(jobSkillRepository.findAllByJobAndIsActiveTrue(job)).willReturn(jobSkills);

        jobService.update(jobDtoUpdate);
        assertThat(job.getTitle().equals(jobDtoUpdate.getTitle()));
    }

    @Test
    public void givenJobDto_whenUpdateJob_thenThrowException() {
        JobDto jobDtoUpdate = JobDto.builder()
                .id(1L)
                .build();

        Assertions.assertThatThrownBy(() -> jobService.update(jobDtoUpdate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void givenJobId_whenDeleteJob_thenJobIsDeleted() {
        // given
        Long jobId = 1L;
        Job job = new Job();
        job.setId(jobId);
        job.setDeleted(false);
        Set<JobSkill> jobSkills = new HashSet<>();
        jobSkills.add(new JobSkill());
        job.setJobSkills(jobSkills);
        Set<JobLevel> jobLevels = new HashSet<>();
        jobLevels.add(new JobLevel());
        job.setJobLevels(jobLevels);
        given(jobRepository.findByIdAndDeletedFalse(jobId)).willReturn(Optional.of(job));
        // when
        jobService.delete(jobId);
        // then
        verify(jobRepository, times(1)).save(job);
        jobSkills.forEach(skill -> {
            verify(jobSkillRepository, times(1)).save(skill);
            assertFalse(skill.getIsActive());
        });
        jobLevels.forEach(level -> {
            verify(jobLevelRepository, times(1)).save(level);
            assertFalse(level.getIsActive());
        });
        assertTrue(job.getDeleted());
    }

    @Test
    public void givenJobId_whenDeleteJob_thenJobThrowException() {
        // given
        Long jobId = 1L;
        Assertions.assertThatThrownBy(() -> jobService.delete(jobId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
