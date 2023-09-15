package com.fa.ims.controller;

import com.fa.ims.dto.CandidateDto;
import com.fa.ims.entities.Position;
import com.fa.ims.entities.Skill;
import com.fa.ims.entities.User;
import com.fa.ims.service.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class CandidateController extends BaseController {
    private final PositionService positionService;
    private final SkillService skillService;
    private final UserService userService;
    private final CandidateService candidateService;
    private final FileStorageService fileStorageService;
    private final StatusService statusService;

    @GetMapping("/view-candidate")
    public String getViewCandidate(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<CandidateDto> candidateDtos = candidateService.findAll(search, status, page, size);
        getAllStatus(model);
        Map<Long, String> statusWithStageCandidate = statusService.getNameStatusWithStage("Candidate");
        model.addAttribute("statusWithStageCandidate", statusWithStageCandidate);
        model.addAttribute("candidateDtos", candidateDtos);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "/candidate/candidate-list";
    }

    @GetMapping("/create-candidate")
    public String getCreateCandidate(@ModelAttribute(name = "candidateDto") CandidateDto candidateDto,
                                     Model model) {
        getStatusWithStageCandidate(model);
        getListAttributesCandidate(model);
        return "/candidate/candidate-form";
    }

    @PostMapping("/create-candidate")
    public String createCandidate(@ModelAttribute(name = "candidateDto") @Valid CandidateDto candidateDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            getStatusWithStageCandidate(model);
            getListAttributesCandidate(model);
            return "/candidate/candidate-form";
        }
        if (candidateService.existsByEmail(candidateDto.getEmail())) {
            getListAttributesCandidate(model);
            getStatusWithStageCandidate(model);
            bindingResult.rejectValue("email",
                    "candidate.email.exists",
                    "Email already exists. Please try again!");
            return "/candidate/candidate-form";
        }
        candidateService.create(candidateDto);
        redirectAttributes.addFlashAttribute("createCandidateSuccess",
                "candidate.create.success");
        return "redirect:/create-candidate";
    }

    @GetMapping("/information-candidate/{id}")
    public String getInformationCandidate(@PathVariable Long id,
                                          @RequestParam Optional<Integer> page,
                                          Model model) {
        CandidateDto candidateDto = candidateService.getInformationCandidate(id);
        getNameCv(model, candidateDto);
        getAllStatus(model);
        getListAttributesCandidate(model);
        getPageNumber(page, model);
        model.addAttribute("candidateDto", candidateDto);
        return "/candidate/candidate-information";
    }

    @GetMapping("/update-candidate/{id}")
    public String getUpdateCandidate(@PathVariable Long id,
                                     @RequestParam Optional<Integer> page,
                                     Model model) {
        CandidateDto candidateDto = candidateService.getInformationCandidate(id);
        getNameCv(model, candidateDto);
        getAllStatus(model);
        getListAttributesCandidate(model);
        getPageNumber(page, model);
        model.addAttribute("candidateDto", candidateDto);
        return "/candidate/candidate-form";
    }

    @PostMapping("/update-candidate/{id}")
    public String updateCandidate(@RequestParam Optional<Integer> page,
                                  @ModelAttribute(name = "candidateDto") @Valid CandidateDto candidateDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            getNameCv(model, candidateDto);
            getAllStatus(model);
            getListAttributesCandidate(model);
            getPageNumber(page, model);
            return "/candidate/candidate-form";
        }
        candidateService.update(candidateDto);
        getNameCv(model, candidateDto);
        redirectAttributes.addFlashAttribute("updateCandidateSuccess", "candidate.update.success");
        int pageNumber = page.orElse(0);
        return "redirect:/update-candidate/{id}?page=" + pageNumber;
    }

    @GetMapping("/delete-candidate/{id}")
    private String deleteCandidate(@PathVariable Long id,
                                   @RequestParam Optional<Integer> page,
                                   RedirectAttributes redirectAttributes) {
        candidateService.delete(id);
        redirectAttributes.addFlashAttribute("deleteCandidateSuccess", "candidate.delete.success");
        int pageNumber = page.orElse(0);
        return "redirect:/view-candidate?page=" + pageNumber;
    }

    @ResponseBody
    @GetMapping("/cv/**")
    public ResponseEntity<Resource> getCvByUrl(HttpServletRequest httpRequest)
            throws IOException {
        String relativePath = httpRequest.getRequestURL()
                .toString().split("cv/")[1];
        Resource resource = fileStorageService.loadFileAsResource(relativePath);
        String mimeType = Files.probeContentType(resource.getFile().toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }

    private void getListAttributesCandidate(Model model) {
        List<Position> positions = positionService.findAll();
        List<Skill> skills = skillService.fillAll();
        List<User> usersWithRoleRecruiter = userService.findAllUserByRole("Recruiter");
        model.addAttribute("positions", positions);
        model.addAttribute("skills", skills);
        model.addAttribute("users", usersWithRoleRecruiter);
    }

    private void getStatusWithStageCandidate(Model model) {
        Map<Long, String> statuses = statusService.getNameStatusWithStage("Candidate");
        model.addAttribute("statuses", statuses);
    }

    private void getAllStatus(Model model) {
        Map<Long, String> statuses = statusService.getAllNameStatus();
        model.addAttribute("statuses", statuses);
    }

    private void getNameCv(Model model, CandidateDto candidateDto) {
        if (StringUtils.hasText(candidateDto.getUriPath())) {
            String nameCv = candidateDto.getUriPath().split("\\\\")[1];
            model.addAttribute("nameCv", nameCv);
        }
    }
}
