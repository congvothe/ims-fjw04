package com.fa.ims.entities;

import com.fa.ims.enums.Department;
import com.fa.ims.enums.Gender;
import com.fa.ims.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String email;

    private String address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Department department;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Candidate> candidates;

    @OneToMany(mappedBy = "user")
    private Set<UserOffer> userOffers;

    public User(Long id) {
        this.id = id;
    }
}
