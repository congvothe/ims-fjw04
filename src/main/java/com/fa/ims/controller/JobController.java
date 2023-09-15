package com.fa.ims.controller;

import com.fa.ims.dto.JobDto;
import com.fa.ims.entities.Level;
import com.fa.ims.entities.Skill;
import com.fa.ims.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class JobController extends BaseController {
    private final JobService jobService;
    private final SkillService skillService;
    private final LevelService levelService;
    private final JobSkillService jobSkillService;
    private final JobLevelService jobLevelService;
    private final StatusService statusService;

    @GetMapping("/view-job")
    public String getJobManagement(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "date", required = false)
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<JobDto> jobDtos = jobService.findAll(search, date, status, page, size);
        model.addAttribute("jobDtos", jobDtos);
        List<Long> jobIds = jobDtos.getContent().stream()
                .map(JobDto::getId)
                .collect(Collectors.toList());
        Map<Long, String> skills = jobSkillService.mapSkillNames(jobIds);
        model.addAttribute("skills", skills);
        Map<Long, String> levels = jobLevelService.mapLevelNames(jobIds);
        model.addAttribute("levels", levels);
        getStatusWithStageJob(model);
        List<String> jobTitleList = jobService.jobTitleList();
        model.addAttribute("jobTitleList", jobTitleList);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("date", date);
        model.addAttribute("status", status);
        return "/job/job-list";
    }

    @GetMapping("/create-job")
    public String getCreateJob(@ModelAttribute("jobDto") JobDto jobDto,
                               Model model) {
        getListAttributesJob(model);
        return "/job/job-form";
    }

    @PostMapping("/create-job")
    public String createJob(@ModelAttribute("jobDto") @Valid JobDto jobDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if(bindingResult.hasErrors()) {
            getListAttributesJob(model);
            return "/job/job-form";
        }
        jobService.create(jobDto);
        redirectAttributes.addFlashAttribute("createJobSuccess","job.create.success");
        return "redirect:/create-job";
    }

    @GetMapping("/update-job/{id}")
    public String getUpdateInterview(@PathVariable Long id,
                                     @RequestParam Optional<Integer> page,
                                     Model model) {
        JobDto jobDto = jobService.getInformationJob(id);
        model.addAttribute("jobDto", jobDto);
        getStatusWithStageJob(model);
        getListAttributesJob(model);
        getPageNumber(page, model);
        return "/job/job-form";
    }

    @PostMapping("/update-job/{id}")
    public String updateInterview(@RequestParam Optional<Integer> page,
                                  @ModelAttribute(name = "jobDto") @Valid JobDto jobDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            getStatusWithStageJob(model);
            getListAttributesJob(model);
            getPageNumber(page, model);
            return "/job/job-form";
        }
        jobService.update(jobDto);
        redirectAttributes.addFlashAttribute("updateJobSuccess", "job.update.success");
        int pageNumber = page.orElse(0);
        return "redirect:/update-job/{id}?page=" + pageNumber;
    }

    @GetMapping("/delete-job/{id}")
    public String deleteJob(@PathVariable Long id,
                            @RequestParam Optional<Integer> page,
                            RedirectAttributes redirectAttributes) {
        jobService.delete(id);
        redirectAttributes.addFlashAttribute("deleteJobSuccess", "job.delete.success");
        int pageNumber = page.orElse(0);
        return "redirect:/view-job?page=" + pageNumber;
    }

    private void getStatusWithStageJob(Model model) {
        Map<Long, String> statuses = statusService.getNameStatusWithStage("Job");
        model.addAttribute("statuses", statuses);
    }

    private void getListAttributesJob(Model model) {
        List<Skill> skills = skillService.fillAll();
        List<Level> levels = levelService.findAll();
        model.addAttribute("skills", skills);
        model.addAttribute("levels", levels);
    }
}
