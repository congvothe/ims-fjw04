package com.fa.ims.service.impl;

import com.fa.ims.dto.CandidateDto;
import com.fa.ims.entities.*;
import com.fa.ims.enums.InterviewResult;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.*;
import com.fa.ims.service.CandidateService;
import com.fa.ims.service.FileStorageService;
import com.fa.ims.service.OfferService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final StatusRepository statusRepository;
    private final InterviewRepository interviewRepository;
    private final UserInterviewRepository userInterviewRepository;
    private final FileStorageService fileStorageService;
    private final OfferService offerService;
    private final OfferRepository offerRepository;
    private final UserOfferRepository userOfferRepository;

    @Override
    public Page<CandidateDto> findAll(String search, String status, int page, int size) {
        Specification<Candidate> specification = undeletedSpec();
        if (StringUtils.hasText(search)) {
            String keyword = search.trim().toUpperCase();
            Specification<Candidate> searchByKeyword = (root, query, builder) -> builder.or(
                    builder.like(builder.upper(root.get("fullName")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.get("email")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.get("phoneNumber")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.join("user").get("username")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.join("position").get("name")), "%" + keyword + "%")
            );
            specification = specification.and(searchByKeyword);
        }
        if (StringUtils.hasText(status)) {
            Specification<Candidate> searchByStatus = (root, query, builder) ->
                    builder.equal(root.join("status").get("name"), status);
            specification = specification.and(searchByStatus);
        }
        Page<Candidate> candidatePage = candidateRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by("lastModifiedDate").descending()));
        return candidatePage.map(CandidateDto::new);
    }

    @Transactional
    @Override
    public Candidate create(CandidateDto candidateDto) throws IOException {
        Candidate candidate = new Candidate();
        candidate.setDeleted(false);
        Status status = statusRepository.findById(candidateDto.getStatusId())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setStatus(status);
        convertCandidateDtoToEntity(candidateDto, candidate);
        Set<CandidateSkill> candidateSkills = Arrays.stream(candidateDto.getSkills())
                .map(skillId -> new CandidateSkill(candidate,
                        skillRepository.findById(skillId).orElseThrow(ResourceNotFoundException::new),true))
                .collect(Collectors.toSet());
        candidate.setCandidateSkills(candidateSkills);
        if (StringUtils.hasText(candidateDto.getCv().getOriginalFilename())) {
            String cvRelativePath = fileStorageService.saveFile(candidateDto.getCv(), candidateDto.getEmail());
            candidate.setCv(cvRelativePath);
        }
        return candidateRepository.save(candidate);
    }

    @Override
    public CandidateDto getInformationCandidate(Long id) {
        CandidateDto candidateDto = new CandidateDto();
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(candidate, candidateDto);
        candidateDto.setStatusId(candidate.getStatus().getId());
        candidateDto.setUriPath(candidate.getCv());
        candidateDto.setUser(candidate.getUser().getUsername());
        candidateDto.setPosition(candidate.getPosition().getName());
        List<CandidateSkill> candidateSkills = candidateSkillRepository.findByCandidateAndIsActiveTrue(candidate);
        Long[] idSkills = candidateSkills.stream()
                .map(CandidateSkill::getSkill)
                .map(Skill::getId)
                .toArray(Long[]::new);
        candidateDto.setSkills(idSkills);
        return candidateDto;
    }

    @Transactional
    @Override
    public void update(CandidateDto candidateDto) {
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(candidateDto.getId())
                .orElseThrow(ResourceNotFoundException::new);
        Status status = statusRepository.findById(candidateDto.getStatusId())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setStatus(status);
        convertCandidateDtoToEntity(candidateDto, candidate);
        List<Long> skillIds = candidateSkillRepository.findByCandidateAndIsActiveTrue(candidate)
                .stream()
                .map(CandidateSkill::getSkill)
                .map(Skill::getId)
                .collect(Collectors.toList());
        List<Long> newSkillIds = Arrays.stream(candidateDto.getSkills()).collect(Collectors.toList());
        Set<CandidateSkill> candidateSkills = newSkillIds.stream()
                .filter(newSkillId -> !skillIds.contains(newSkillId))
                .map(newSkillId -> new CandidateSkill(candidate, new Skill(newSkillId), true))
                .collect(Collectors.toSet());
        candidate.setCandidateSkills(candidateSkills);
        candidateRepository.save(candidate);
        List<Long> inactiveCandidateSkills = skillIds.stream()
                .filter(skillId -> !newSkillIds.contains(skillId))
                .collect(Collectors.toList());
        inactiveCandidateSkills.forEach(skillId -> {
            CandidateSkill candidateSkill = new CandidateSkill(candidate, new Skill(skillId), false);
            candidateSkillRepository.save(candidateSkill);
        });
        if (candidate.getInterview() != null) {
            Interview interview = interviewRepository.findByIdAndDeletedFalse(candidate.getInterview().getId())
                    .orElseThrow(ResourceNotFoundException::new);
            interview.setStatus(status);
            if (status.getName().equals("Banned")) {
                interview.setResult(InterviewResult.CANCEL);
            }
            interviewRepository.save(interview);
        }
        if (candidate.getOffer() != null) {
            Offer offer = offerService.findByIdAndDeletedFalse(candidate.getOffer().getId())
                    .orElseThrow(ResourceNotFoundException::new);
            Status statusOffer = statusRepository.findByNameAndStage("Cancelled offer", "Offer")
                    .orElseThrow(ResourceNotFoundException::new);
            offer.setStatus(statusOffer);
            offerRepository.save(offer);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setDeleted(true);
        candidateRepository.save(candidate);
        Set<CandidateSkill> candidateSkills = candidate.getCandidateSkills();
        candidateSkills.forEach(candidateSkill -> {
            candidateSkill.setIsActive(false);
            candidateSkillRepository.save(candidateSkill);
        });
        if (candidate.getInterview() != null) {
            Interview interview = candidate.getInterview();
            interview.setDeleted(true);
            interviewRepository.save(interview);
            Set<UserInterview> userInterviews = interview.getUserInterviews();
            userInterviews.forEach(userInterview -> {
                userInterview.setIsActive(false);
                userInterviewRepository.save(userInterview);
            });
            if (interview.getOffer() != null) {
                Offer offer = interview.getOffer();
                offer.setDeleted(true);
                offerRepository.save(offer);
                Set<UserOffer> userOffers = offer.getUserOffers();
                userOffers.forEach(userOffer -> {
                    userOffer.setIsActive(false);
                    userOfferRepository.save(userOffer);
                });
            }
        }
    }

    @Override
    public List<Candidate> findAllAndDeletedFalseAndNotExistInterview() {
        return candidateRepository.findAllByDeletedFalseAndInterviewNullOrderByLastModifiedDateDesc();
    }

    @Override
    public boolean existsByEmail(String email) {
        return candidateRepository.existsByEmail(email);
    }

    @Override
    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Override
    public List<CandidateDto> getCandidateDto() {
        return candidateRepository.findAllByDeletedFalse().stream()
                .map(CandidateDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateDto findDtoById(Long id) {
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);
        CandidateDto candidateDto = new CandidateDto();
        candidateDto.setId(candidate.getId());
        candidateDto.setFullName(candidate.getFullName());
        candidateDto.setStatusId(candidate.getStatus().getId());
        return candidateDto;
    }

    @Override
    public Map<Long, Candidate> mapCandidateInformation(List<Long> offerIds) {
        Map<Long, Candidate> candidates = new HashMap<>();
        offerIds.forEach(offerId -> {
            Offer offer = offerService.findByIdAndDeletedFalse(offerId).orElseThrow(ResourceNotFoundException::new);
            candidates.put(offerId, offer.getCandidate());
        });
        return candidates;
    }

    private void convertCandidateDtoToEntity(CandidateDto candidateDto, Candidate candidate) {
        BeanUtils.copyProperties(candidateDto, candidate);
        Position position = positionRepository.findByName(candidateDto.getPosition())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setPosition(position);
        User user = userRepository.findByUsernameAndDeletedFalse(candidateDto.getUser())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setUser(user);
    }
}
