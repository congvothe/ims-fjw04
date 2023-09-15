package com.fa.ims.dto;

import com.fa.ims.entities.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long id;

    @NotBlank(message = "{job.title.required}")
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{job.startDate.required}")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{job.endDate.required}")
    private LocalDate endDate;

    private Long statusId;

    private Double salaryFrom;

    @NotNull(message = "{job.salaryTo.required}")
    private Double salaryTo;

    private String address;

    private String description;

    @NotEmpty(message = "{job.jobSkills.required}")
    private Long[] skillIds;

    @NotEmpty(message = "{job.jobLevels.required}")
    private Long[] levelIds;

    public JobDto(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.startDate = job.getStartDate();
        this.endDate = job.getEndDate();
        this.statusId = job.getStatus().getId();
        this.salaryFrom = job.getSalaryFrom();
        this.salaryTo = job.getSalaryTo();
        this.address = job.getAddress();
        this.description = job.getDescription();
        this.skillIds = job.getJobSkills().stream()
                .map(JobSkill::getId)
                .map(JobSkillId::getSkillId)
                .toArray(Long[]::new);
        this.levelIds = job.getJobLevels().stream()
                .map(JobLevel::getId)
                .map(JobLevelId::getLevelId)
                .toArray(Long[]::new);
    }
}
