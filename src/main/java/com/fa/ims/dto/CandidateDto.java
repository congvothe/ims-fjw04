package com.fa.ims.dto;

import com.fa.ims.entities.Candidate;
import com.fa.ims.enums.Gender;
import com.fa.ims.enums.HighestLevel;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDto {
    private Long id;

    @NotBlank(message = "{candidate.fullName.required}")
    private String fullName;

    @NotBlank(message = "{candidate.email.required}")
    private String email;

    @NotNull(message = "{candidate.dob.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String address;

    @NotBlank(message = "{candidate.phoneNumber.required}")
    private String phoneNumber;

    @NotNull(message = "{candidate.gender.required}")
    private Gender gender;

    private MultipartFile cv;

    private String uriPath;

    @NotNull(message = "{candidate.status.required}")
    private Long statusId;

    private Integer yearOfExperience;

    @NotNull(message = "{candidate.highestLevel.required}")
    private HighestLevel highestLevel;

    private String note;

    @NotEmpty(message = "{candidate.position.required}")
    private String position;

    @NotEmpty(message = "{candidate.recruiter.required}")
    private String user;

    @NotEmpty(message = "{candidate.skill.required}")
    private Long[] skills;

    public CandidateDto(Candidate candidate) {
        this.id = candidate.getId();
        this.fullName = candidate.getFullName();
        this.email = candidate.getEmail();
        this.phoneNumber = candidate.getPhoneNumber();
        this.statusId = candidate.getStatus().getId();
        this.position = candidate.getPosition().getName();
        this.user = candidate.getUser().getUsername();
    }
}
