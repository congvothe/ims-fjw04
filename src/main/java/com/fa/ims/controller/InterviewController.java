package com.fa.ims.controller;

import com.fa.ims.dto.InterviewDto;
import com.fa.ims.entities.Candidate;
import com.fa.ims.entities.User;
import com.fa.ims.enums.InterviewResult;
import com.fa.ims.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class InterviewController extends BaseController {
    private final InterviewService interviewService;
    private final CandidateService candidateService;
    private final UserService userService;
    private final StatusService statusService;
    private final CandidateInterviewService candidateInterviewService;
    private final UserInterviewService userInterviewService;

    @GetMapping("/view-interview")
    public String getViewInterview(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "date", required = false)
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                   @RequestParam(value = "result", required = false) InterviewResult result,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<InterviewDto> interviewDtos = interviewService
                .findAll(search, date, result, status, page, size);
        model.addAttribute("interviewDtos", interviewDtos);
        List<Long> interviewIds = interviewDtos.getContent().stream()
                .map(InterviewDto::getId).collect(Collectors.toList());
        Map<Long, String> candidates = candidateInterviewService.mapCandidateName(interviewIds);
        model.addAttribute("candidates", candidates);
        Map<Long, String> users = userInterviewService.mapUserNames(interviewIds);
        model.addAttribute("users", users);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("date", date);
        model.addAttribute("result", result);
        model.addAttribute("status", status);
        Map<Long, String> statusWithStateInterview = statusService.getNameStatusWithStage("Interview");
        model.addAttribute("statusWithStateInterview", statusWithStateInterview);
        getAllStatus(model);
        return "/interview/interview-list";
    }

    @GetMapping("/create-interview")
    public String getCreateInterview(@ModelAttribute(name = "interviewDto") InterviewDto interviewDto,
                                     Model model) {
        getListAttributesInterview(model, null);
        return "/interview/interview-form";
    }

    @PostMapping("/create-interview")
    public String createInterview(@ModelAttribute(name = "interviewDto") @Valid InterviewDto interviewDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            getStatusWithStageInterview(model);
            getListAttributesInterview(model, null);
            return "/interview/interview-form";
        }
        interviewService.create(interviewDto);
        redirectAttributes.addFlashAttribute("createInterviewSuccess", "interview.create.success");
        return "redirect:/create-interview";
    }

    @GetMapping("/update-interview/{id}")
    public String getUpdateInterview(@PathVariable Long id,
                                     @RequestParam Optional<Integer> page,
                                     Model model) {
        InterviewDto interviewDto = interviewService.getInformationInterview(id);
        model.addAttribute("interviewDto", interviewDto);
        getAllStatus(model);
        Map<Long, String> statusInterview = statusService.getNameStatusWithStage("Interview");
        model.addAttribute("statusInterview", statusInterview);
        getListAttributesInterview(model, interviewDto.getCandidateId());
        getPageNumber(page, model);
        return "/interview/interview-form";
    }

    @PostMapping("/update-interview/{id}")
    public String updateInterview(@RequestParam Optional<Integer> page,
                                  @ModelAttribute(name = "interviewDto") @Valid InterviewDto interviewDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            getStatusWithStageInterview(model);
            getListAttributesInterview(model, interviewDto.getCandidateId());
            getPageNumber(page, model);
            return "/interview/interview-form";
        }
        interviewService.update(interviewDto);
        redirectAttributes.addFlashAttribute("updateInterviewSuccess", "interview.update.success");
        int pageNumber = page.orElse(0);
        return "redirect:/update-interview/{id}?page=" + pageNumber;
    }

    @GetMapping("/result-interview/{id}")
    public String getResultCandidate(@PathVariable Long id,
                                     @RequestParam Optional<Integer> page,
                                     Model model) {
        InterviewDto interviewDto = interviewService.getInformationInterview(id);
        model.addAttribute("interviewDto", interviewDto);
        getAllStatus(model);
        getListAttributesInterview(model, interviewDto.getCandidateId());
        Map<Long, String> statusesInterview = statusService.getNameStatusWithStage("Interview");
        model.addAttribute("statusesInterview", statusesInterview);
        getPageNumber(page, model);
        return "/interview/interview-result";
    }

    @PostMapping("/result-interview/{id}")
    public String submitResultInterview(@RequestParam Optional<Integer> page,
                                        @ModelAttribute(name = "interviewDto") InterviewDto interviewDto,
                                        RedirectAttributes redirectAttributes) {
        interviewService.updateResultInterview(interviewDto);
        redirectAttributes.addFlashAttribute("updateInterviewSuccess", "interview.update.success");
        int pageNumber = page.orElse(0);
        return "redirect:/result-interview/{id}?page=" + pageNumber;
    }

    private void getListAttributesInterview(Model model, Long candidateId) {
        List<Candidate> candidates = candidateService.findAllAndDeletedFalseAndNotExistInterview();
        if (candidateId != null) {
            Candidate candidate = candidateService.findById(candidateId).orElseThrow(ResourceNotFoundException::new);
            candidates.add(candidate);
        }
        List<User> usersByRoleInterviewer = userService.findAllUserByRole("Interviewer");
        List<User> usersByRoleRecruiter = userService.findAllUserByRole("Recruiter");
        model.addAttribute("candidates", candidates);
        model.addAttribute("usersByRoleInterviewer", usersByRoleInterviewer);
        model.addAttribute("usersByRoleRecruiter", usersByRoleRecruiter);
    }

    private void getStatusWithStageInterview(Model model) {
        Map<Long, String> statuses = statusService.getNameStatusWithStage("Interview");
        model.addAttribute("statuses", statuses);
    }

    private void getAllStatus(Model model) {
        Map<Long, String> statuses = statusService.getAllNameStatus();
        model.addAttribute("statuses", statuses);
    }
}
