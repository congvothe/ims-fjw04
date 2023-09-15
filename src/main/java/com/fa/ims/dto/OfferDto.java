package com.fa.ims.dto;

import com.fa.ims.entities.Offer;
import com.fa.ims.entities.UserOffer;
import com.fa.ims.enums.ContractType;
import com.fa.ims.enums.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class OfferDto {
    private Long id;

    @NotNull(message = "{offer.contractType.required}")
    private ContractType contractType;

    @NotNull(message = "{offer.department.required}")
    private Department department;

    private LocalDate contractStartFrom;

    private LocalDate contractStartTo;

    private Double basicSalary;

    private Long statusId;

    private String interviewNotes;

    private String notes;

    private Long candidateId;

    @NotNull(message = "{offer.position.required}")
    private Long positionId;

    @NotNull(message = "{offer.level.required}")
    private Long skillId;

    @NotNull(message = "{offer.level.required}")
    private Long levelId;

    @NotNull(message = "{offer.recruiter.required}")
    private Long recruiterId;

    @NotNull(message = "{offer.manager.required}")
    private Long managerId;

    private Long interviewId;

    private List<Long> userInterviewers;

    public OfferDto(Offer offer) {
        this.id = offer.getId();
        this.department = offer.getDepartment();
        this.statusId = offer.getStatus().getId();
        this.notes = offer.getNote();
        this.candidateId = offer.getCandidate().getId();
        if (offer.getUserOffers() != null) {
            this.managerId = offer.getUserOffers().stream().map(UserOffer::getUser)
                    .filter(user -> user.getRole().getRole().equals("Manager"))
                    .collect(Collectors.toList()).get(0).getId();
        }
    }
}
