package com.fa.ims.controller;

import com.fa.ims.dto.CandidateDto;
import com.fa.ims.dto.InterviewDto;
import com.fa.ims.dto.OfferDto;
import com.fa.ims.entities.*;
import com.fa.ims.enums.Department;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.security.SecurityUtil;
import com.fa.ims.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class OfferController extends BaseController {
    private final LevelService levelService;
    private final PositionService positionService;
    private final CandidateService candidateService;
    private final InterviewService interviewService;
    private final StatusService statusService;
    private final OfferService offerService;
    private final UserService userService;
    private final SkillService skillService;
    private final UserInterviewService userInterviewService;

    @GetMapping("/view-offer")
    public String getViewOffer(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "department", required = false) Department department,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Page<OfferDto> offerDtos = offerService.findAll(search, department, status, page, size);
        model.addAttribute("offerDtos", offerDtos);
        List<Long> offerIds = offerDtos.getContent().stream()
                .map(OfferDto::getId).collect(Collectors.toList());
        Map<Long, Candidate> candidateMap = candidateService.mapCandidateInformation(offerIds);
        model.addAttribute("candidateMap", candidateMap);
        Map<Long, String> userMap = userService.mapUserInformation(offerIds);
        model.addAttribute("userMap", userMap);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("department", department);
        getStatusWithStageOffer(model);
        model.addAttribute("status", status);
        String username = SecurityUtil.getCurrentUserLogin().orElseThrow(ResourceNotFoundException::new);
        model.addAttribute("username", username);
        return "/offer/offer-list";
    }

    @GetMapping("/select-offer-candidate")
    public String getSelectCandidateOffer(@ModelAttribute("offerDto") OfferDto offerDto,
                                          Model model) {
        List<InterviewDto> candidateResultPass = interviewService.candidateResultPass();
        model.addAttribute("candidateResultPass", candidateResultPass);
        List<CandidateDto> candidateDtoList = candidateService.getCandidateDto();
        model.addAttribute("candidateDtoList", candidateDtoList);
        return "/offer/select-candidate-offer";
    }

    @GetMapping("/create-offer")
    public String getCreateOffer(@RequestParam("idInterview") Long idInterview,
                                 Model model) {
        OfferDto offerDto = offerService.getDataFromInterviewEntity(idInterview);
        model.addAttribute("offerDto", offerDto);
        CandidateDto candidateDto = candidateService.findDtoById(offerDto.getCandidateId());
        model.addAttribute("candidateDto", candidateDto);
        String uri = "offer-form";
        return getDataOfferDto(model, offerDto, candidateDto, uri);
    }

    @PostMapping("/create-offer")
    public String createOffer(@RequestParam("idInterview") Long idInterview,
                              @ModelAttribute(name = "offerDto") @Valid OfferDto offerDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            OfferDto fromInterviewEntity = offerService.getDataFromInterviewEntity(idInterview);
            CandidateDto candidateDto = candidateService.findDtoById(fromInterviewEntity.getCandidateId());
            model.addAttribute("candidateDto", candidateDto);
            offerDto.setInterviewId(fromInterviewEntity.getInterviewId());
            String uri = "offer-form";
            return getDataOfferDto(model, fromInterviewEntity, candidateDto, uri);
        }
        offerDto.setInterviewId(idInterview);
        offerService.create(offerDto);
        redirectAttributes.addFlashAttribute("createOfferSuccess","offer.create.success");
        return "redirect:/create-offer?idInterview=" + idInterview;
    }

    @GetMapping("/update-offer/{id}")
    public String getUpdateOffer(@PathVariable("id") Long id,
                                 Model model) {
        OfferDto offerDto = offerService.getInformationInterview(id);
        model.addAttribute("offerDto", offerDto);
        CandidateDto candidateDto = candidateService.findDtoById(offerDto.getCandidateId());
        model.addAttribute("candidateDto", candidateDto);
        getStatusWithStageOffer(model);
        String uri = "offer-form";
        return getDataOfferDto(model, offerDto, candidateDto, uri);
    }

    @PostMapping("/update-offer/{id}")
    public String updateOffer(@ModelAttribute(name = "offerDto") OfferDto offerDto,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        getStatusWithStageOffer(model);
        offerService.update(offerDto);
        redirectAttributes.addFlashAttribute("updateOfferSuccess","offer.update.success");
        return "redirect:/update-offer/{id}";
    }

    @GetMapping("/result-offer/{id}")
    public String getResultOffer(@PathVariable("id") Long id,
                                 Model model) {
        OfferDto offerDto = offerService.getInformationInterview(id);
        model.addAttribute("offerDto", offerDto);
        CandidateDto candidateDto = candidateService.findDtoById(offerDto.getCandidateId());
        model.addAttribute("candidateDto", candidateDto);
        getStatusWithStageOffer(model);
        String uri = "offer-result";
        return getDataOfferDto(model, offerDto, candidateDto, uri);
    }

    @PostMapping("/result-offer/{id}")
    public String updateResultOffer(@ModelAttribute(name = "offerDto") OfferDto offerDto,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        getStatusWithStageOffer(model);
        offerService.updateResultOffer(offerDto);
        redirectAttributes.addFlashAttribute("updateOfferSuccess","offer.update.success");
        return "redirect:/result-offer/{id}";
    }

    private String getDataOfferDto(Model model, OfferDto offerDto, CandidateDto candidateDto, String uri) {
        Position position = positionService.findById(offerDto.getPositionId());
        model.addAttribute("position", position);
        Status status = statusService.findById(candidateDto.getStatusId());
        model.addAttribute("status", status);
        String usersInterviewer = userInterviewService.getAllInterviewers(offerDto.getInterviewId());
        model.addAttribute("usersInterviewer", usersInterviewer);
        getListAttributesOffer(model);
        return "/offer/" + uri;
    }

    private void getListAttributesOffer(Model model) {
        List<Level> levels = levelService.findAll();
        List<Position> positions = positionService.findAll();
        List<Skill> skills = skillService.fillAll();
        List<User> usersByRoleManager = userService.findAllUserByRole("Manager");
        List<User> usersByRoleRecruiter = userService.findAllUserByRole("Recruiter");
        model.addAttribute("levels", levels);
        model.addAttribute("positions", positions);
        model.addAttribute("skills", skills);
        model.addAttribute("usersByRoleManager", usersByRoleManager);
        model.addAttribute("usersByRoleRecruiter", usersByRoleRecruiter);
    }

    private void getStatusWithStageOffer(Model model) {
        Map<Long, String> statuses = statusService.getNameStatusWithStage("Offer");
        model.addAttribute("statuses", statuses);
    }
}
