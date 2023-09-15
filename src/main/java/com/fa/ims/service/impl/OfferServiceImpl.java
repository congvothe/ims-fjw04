package com.fa.ims.service.impl;

import com.fa.ims.dto.OfferDto;
import com.fa.ims.entities.*;
import com.fa.ims.enums.Department;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.*;
import com.fa.ims.service.InterviewService;
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

import javax.persistence.criteria.Join;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final StatusRepository statusRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final LevelRepository levelRepository;

    @Override
    public OfferDto getDataFromInterviewEntity(Long id) {
        Interview interview = interviewRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);
        OfferDto offerDto = new OfferDto();
        BeanUtils.copyProperties(interview, offerDto, "id", "notes");
        offerDto.setInterviewId(interview.getId());
        offerDto.setCandidateId(interview.getCandidate().getId());
        offerDto.setPositionId(interview.getCandidate().getPosition().getId());
        offerDto.setInterviewNotes(interview.getNotes());
        Set<UserInterview> userInterviews = interview.getUserInterviews();
        List<Long> userIdWithRoleInterviewer = userInterviews.stream().map(UserInterview::getUser)
                .filter(user -> user.getRole().getRole().equals("Interviewer"))
                .map(User::getId).collect(Collectors.toList());
        offerDto.setUserInterviewers(userIdWithRoleInterviewer);
        return offerDto;
    }

    @Transactional
    @Override
    public Offer create(OfferDto offerDto) {
        Offer offer = new Offer();
        offer.setDeleted(false);
        BeanUtils.copyProperties(offerDto, offer);
        Status status = statusRepository.findByNameAndStage("Waiting for approval", "Offer")
                .orElseThrow(ResourceNotFoundException::new);
        offer.setStatus(status);
        offer.setSkill(new Skill(offerDto.getSkillId()));
        offer.setLevel(new Level(offerDto.getLevelId()));
        offer.setInterview(new Interview(offerDto.getInterviewId()));
        offer.setPosition(new Position(offerDto.getPositionId()));
        offer.setNote(offerDto.getNotes());
        Set<UserOffer> offers = new HashSet<>();
        offers.add(new UserOffer(userRepository.findById(offerDto.getRecruiterId())
                .orElseThrow(ResourceNotFoundException::new), offer, true));
        offers.add(new UserOffer(userRepository.findById(offerDto.getManagerId())
                .orElseThrow(ResourceNotFoundException::new), offer, true));
        offer.setUserOffers(offers);
        offer.setDepartment(offerDto.getDepartment());
        offer.setContractType(offerDto.getContractType());
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(offerDto.getCandidateId())
                .orElseThrow(ResourceNotFoundException::new);
        offer.setCandidate(candidate);
        offerRepository.save(offer);
        Interview interview = interviewRepository.findByIdAndDeletedFalse(offerDto.getInterviewId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setStatus(status);
        interviewRepository.save(interview);
        candidate.setStatus(status);
        candidateRepository.save(candidate);
        return offer;
    }

    @Override
    public Page<OfferDto> findAll(String search, Department department, String status, int page, int size) {
        Specification<Offer> specification = undeletedSpec();
        if (StringUtils.hasText(search)) {
            String keyword = search.trim().toUpperCase();
            Specification<Offer> searchByKeyword = (root, query, builder) -> builder.or(
                    builder.like(builder.upper(root.get("note")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.join("candidate").get("fullName")), "%" + keyword + "%"),
                    builder.like(builder.upper(root.join("candidate").get("email")), "%" + keyword + "%")
            );
            Specification<Offer> searchByInterviewer = joinTableOffer(keyword);
            searchByKeyword = searchByKeyword.or(searchByInterviewer);
            specification = specification.and(searchByKeyword);
        }
        if (department != null) {
            Specification<Offer> searchByDepartment = (root, query, builder) ->
                    builder.equal(root.get("department"), department);
            specification = specification.and(searchByDepartment);
        }
        if (StringUtils.hasText(status)) {
            Specification<Offer> searchByStatus = (root, query, builder) ->
                    builder.equal(root.join("status").get("name"), status);
            specification = specification.and(searchByStatus);
        }
        Page<Offer> offerPage = offerRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by("lastModifiedDate").descending()));
        return offerPage.map(OfferDto::new);
    }

    @Override
    public Optional<Offer> findByIdAndDeletedFalse(Long id) {
        return offerRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public OfferDto getInformationInterview(Long id) {
        OfferDto offerDto = new OfferDto();
        Offer offer = offerRepository.findByIdAndDeletedFalse(id).orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(offer, offerDto);
        offerDto.setCandidateId(offer.getCandidate().getId());
        offerDto.setLevelId(offer.getLevel().getId());
        offerDto.setSkillId(offer.getSkill().getId());
        offerDto.setPositionId(offer.getPosition().getId());
        offerDto.setStatusId(offer.getStatus().getId());
        offerDto.setInterviewId(offer.getInterview().getId());
        offerDto.setInterviewNotes(offer.getInterview().getNotes());
        offerDto.setNotes(offer.getNote());
        Long userIdWithRoleManager = offer.getUserOffers().stream()
                .map(UserOffer::getUser)
                .filter(user -> user.getRole().getRole().equals("Manager"))
                .collect(Collectors.toList()).get(0).getId();
        Long userIdWithRoleRecruiter = offer.getUserOffers().stream()
                .map(UserOffer::getUser)
                .filter(user -> user.getRole().getRole().equals("Recruiter"))
                .collect(Collectors.toList()).get(0).getId();
        offerDto.setRecruiterId(userIdWithRoleRecruiter);
        offerDto.setManagerId(userIdWithRoleManager);
        offerDto.setUserInterviewers(offer.getInterview().getUserInterviews()
                .stream()
                .map(UserInterview::getUser)
                .map(User::getId)
                .collect(Collectors.toList()));
        return offerDto;
    }

    @Transactional
    @Override
    public void update(OfferDto offerDto) {
        Offer offer = offerRepository.findByIdAndDeletedFalse(offerDto.getId())
                .orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(offerDto, offer);
        Status status = statusRepository.findByNameAndStage("Waiting for response", "Offer")
                .orElseThrow(ResourceNotFoundException::new);
        offer.setStatus(status);
        Skill skill = skillRepository.findById(offerDto.getSkillId()).orElseThrow(ResourceNotFoundException::new);
        offer.setSkill(skill);
        Level level = levelRepository.findById(offerDto.getLevelId()).orElseThrow(ResourceNotFoundException::new);
        offer.setLevel(level);
        offer.setNote(offerDto.getNotes());
        offerRepository.save(offer);
        Interview interview = interviewRepository.findByIdAndDeletedFalse(offer.getInterview().getId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setStatus(status);
        interviewRepository.save(interview);
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(offer.getCandidate().getId())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setStatus(status);
        candidateRepository.save(candidate);
    }

    @Transactional
    @Override
    public void updateResultOffer(OfferDto offerDto) {
        Offer offer = offerRepository.findByIdAndDeletedFalse(offerDto.getId())
                .orElseThrow(ResourceNotFoundException::new);
        offer.setNote(offerDto.getNotes());
        Status status = statusRepository.findById(offerDto.getStatusId()).orElseThrow(ResourceNotFoundException::new);
        offer.setStatus(status);
        offerRepository.save(offer);
        Interview interview = interviewRepository.findByIdAndDeletedFalse(offer.getInterview().getId())
                .orElseThrow(ResourceNotFoundException::new);
        interview.setStatus(status);
        Candidate candidate = candidateRepository.findByIdAndDeletedFalse(offer.getCandidate().getId())
                .orElseThrow(ResourceNotFoundException::new);
        candidate.setStatus(status);
        interviewRepository.save(interview);
        candidateRepository.save(candidate);
    }

    private Specification<Offer> joinTableOffer(String keyword) {
        return (root, query, builder) -> {
            query.distinct(true);
            Join<Offer, UserOffer> userOfferJoin = root.join("userOffers");
            Join<UserOffer, User> userJoin = userOfferJoin.join("user");
            Join<User, Role> roleJoin = userJoin.join("role");
            return builder.and(
                    builder.equal(userOfferJoin.get("isActive"), true),
                    builder.like(builder.upper(userJoin.get("fullName")), "%" + keyword + "%"),
                    builder.equal(roleJoin.get("role"), "Manager")
            );
        };
    }
}
