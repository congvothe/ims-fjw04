package com.fa.ims.dto;

import com.fa.ims.entities.User;
import com.fa.ims.enums.Department;
import com.fa.ims.enums.Gender;
import com.fa.ims.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "{user.fullName.required}")
    private String fullName;

    @NotBlank(message = "{user.email.required}")
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String address;

    private String phoneNumber;

    @NotNull(message = "{user.gender.required}")
    private Gender gender;

    @NotNull(message = "{user.department.required}")
    private Department department;

    @NotBlank(message = "{user.username.required}")
    private String username;

    @NotBlank(message = "{user.password.required}")
    private String password;

    @NotNull(message = "{user.status.required}")
    private UserStatus status;

    private String note;

    @NotEmpty(message = "{user.role.required}")
    private String role;

    public UserDto(User user) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.department = user.getDepartment();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.role = user.getRole().getRole();
    }
}
