package com.fa.ims.service.impl;

import com.fa.ims.dto.CandidateDto;
import com.fa.ims.entities.*;
import com.fa.ims.enums.Department;
import com.fa.ims.enums.Gender;
import com.fa.ims.enums.HighestLevel;
import com.fa.ims.enums.UserStatus;
import com.fa.ims.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceImplTest {
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private CandidateSkillRepository candidateSkillRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    @Test
    public void givenCandidateDto_whenCreateCandidate_thenReturnCandidateObject() throws IOException {
        Role role = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        User user = User.builder()
                .id(1L)
                .fullName("Vo The Cong")
                .dateOfBirth(LocalDate.of(2001, 05, 18))
                .email("cong@gmail.com")
                .address("Hà ")
                .phoneNumber("0987676523")
                .gender(Gender.MALE)
                .department(Department.IT)
                .username("cong")
                .password("$2a$12$jDTu8HxlvxbImzadO8..8OpIB9jGL2GoOOoKH8HzHujlebLCHnALO")
                .status(UserStatus.ACTIVATED)
                .role(role)
                .build();
        Position position = Position.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();

        Long[] skills = {skill1.getId(), skill2.getId()};
        CandidateDto candidateDto = CandidateDto.builder()
                .id(1L)
                .fullName("Anh Anh")
                .email("anh@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position.getName())
                .user(user.getUsername())
                .skills(skills)
                .build();

        given(positionRepository.findByName(anyString())).willReturn(Optional.of(position));
        given(userRepository.findByUsernameAndDeletedFalse(anyString())).willReturn(Optional.of(user));
        given(skillRepository.findById(anyLong())).willReturn(Optional.of(new Skill()));
        Candidate newCandidate = candidateService.create(candidateDto);
        Assertions.assertThat(newCandidate).isNotNull();
        Assertions.assertThat(newCandidate.getCandidateSkills()).hasSize(2);
    }

    @Test
    public void givenCandidateDto_whenUpdateCandidate_thenCandidateIsUpdated() {
        Role role = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        User user = User.builder()
                .id(1L)
                .fullName("Linh Linh")
                .dateOfBirth(LocalDate.of(2001, 05, 18))
                .email("linh@gmail.com")
                .address("Hưng Yên")
                .phoneNumber("0987676523")
                .gender(Gender.FEMALE)
                .department(Department.IT)
                .username("linh")
                .password("$2a$12$QzkRsW5HZIe4WXQNx01Jiu3pc2dJhL0OotihdD.3VHXaJ64H9lqMq")
                .status(UserStatus.ACTIVATED)
                .role(role)
                .build();
        Position position = Position.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();

        Long[] skills = {skill1.getId(), skill2.getId()};
        // given
        CandidateDto candidateDto = CandidateDto.builder()
                .id(1L)
                .fullName("Anh1 Anh1")
                .email("anh1@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position.getName())
                .user(user.getUsername())
                .skills(skills)
                .build();

        Candidate candidate = Candidate.builder()
                .id(1L)
                .fullName("Anh Anh")
                .email("anh@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position)
                .user(user)
                .build();
        given(positionRepository.findByName(anyString())).willReturn(Optional.of(position));
        given(userRepository.findByUsernameAndDeletedFalse(anyString())).willReturn(Optional.of(user));
        candidateService.update(candidateDto);
        assertThat(candidate.getId()).isEqualTo(candidateDto.getId());
        assertThat(candidate.getFullName()).isEqualTo(candidateDto.getFullName());
        assertThat(candidate.getEmail()).isEqualTo(candidateDto.getEmail());
        Assertions.assertThat(candidate.getCandidateSkills()).hasSize(2);
    }

    @Test
    public void givenCandidateId_whenDeleteCandidate_thenCandidateIsDeleted() {
        // given
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        Set<CandidateSkill> candidateSkills = new HashSet<>();
        candidateSkills.add(new CandidateSkill());
        candidate.setCandidateSkills(candidateSkills);
        // when
        candidateService.delete(candidateId);
        // then
        verify(candidateRepository, times(1)).save(candidate);
        candidateSkills.forEach(skill -> {
            verify(candidateSkillRepository, times(1)).save(skill);
            assertFalse(skill.getIsActive());
        });
        assertTrue(candidate.getDeleted());
    }

    @Test
    public void whenFindAllCandidateDeletedFalseAndNotExistInterview_thenReturnListCandidate() throws IOException {
        Role role = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        User user = User.builder()
                .id(1L)
                .fullName("Linh Linh")
                .dateOfBirth(LocalDate.of(2001, 05, 18))
                .email("linh@gmail.com")
                .address("Hưng Yên")
                .phoneNumber("0987676523")
                .gender(Gender.FEMALE)
                .department(Department.IT)
                .username("linh")
                .password("$2a$12$jDTu8HxlvxbImzadO8..8OpIB9jGL2GoOOoKH8HzHujlebLCHnALO")
                .status(UserStatus.ACTIVATED)
                .role(role)
                .build();
        Position position = Position.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();

        Long[] skills = {skill1.getId(), skill2.getId()};
        CandidateDto candidateDto = CandidateDto.builder()
                .id(1L)
                .fullName("Anh Anh")
                .email("anh@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position.getName())
                .user(user.getUsername())
                .skills(skills)
                .build();

        given(positionRepository.findByName(anyString())).willReturn(Optional.of(position));
        given(userRepository.findByUsernameAndDeletedFalse(anyString())).willReturn(Optional.of(user));
        given(skillRepository.findById(anyLong())).willReturn(Optional.of(new Skill()));
        Candidate candidate = candidateService.create(candidateDto);
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);
        List<Candidate> newCandidates = candidateService.findAllAndDeletedFalseAndNotExistInterview();
        assertEquals(1, newCandidates.size());
    }

    @Test
    public void givenCandidateEmail_whenExistsByEmail_thenReturnCandidateExistedByEmail() throws IOException {
        Role role = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        User user = User.builder()
                .id(1L)
                .fullName("Linh Linh")
                .dateOfBirth(LocalDate.of(2001, 05, 18))
                .email("linh@gmail.com")
                .address("Hưng Yên")
                .phoneNumber("0987676523")
                .gender(Gender.FEMALE)
                .department(Department.IT)
                .username("linh")
                .password("$2a$12$jDTu8HxlvxbImzadO8..8OpIB9jGL2GoOOoKH8HzHujlebLCHnALO")
                .status(UserStatus.ACTIVATED)
                .role(role)
                .build();
        Position position = Position.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();

        Long[] skills = {skill1.getId(), skill2.getId()};
        CandidateDto candidateDto = CandidateDto.builder()
                .id(1L)
                .fullName("Anh Anh")
                .email("anh@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position.getName())
                .user(user.getUsername())
                .skills(skills)
                .build();
        given(positionRepository.findByName(anyString())).willReturn(Optional.of(position));
        given(userRepository.findByUsernameAndDeletedFalse(anyString())).willReturn(Optional.of(user));
        given(skillRepository.findById(anyLong())).willReturn(Optional.of(new Skill()));
        Candidate candidate = candidateService.create(candidateDto);

        given(candidateRepository.existsByEmail(candidate.getEmail())).willReturn(true);
        boolean isExist = candidateService.existsByEmail(candidate.getEmail());
        Assertions.assertThat(isExist).isEqualTo(true);
    }

    @Test
    public void givenCandidateId_whenFindById_thenReturnCandidate() throws IOException {
        Role role = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        User user = User.builder()
                .id(1L)
                .fullName("Linh Linh")
                .dateOfBirth(LocalDate.of(2001, 05, 18))
                .email("linh@gmail.com")
                .address("Hưng Yên")
                .phoneNumber("0987676523")
                .gender(Gender.FEMALE)
                .department(Department.IT)
                .username("linh")
                .password("$2a$12$jDTu8HxlvxbImzadO8..8OpIB9jGL2GoOOoKH8HzHujlebLCHnALO")
                .status(UserStatus.ACTIVATED)
                .role(role)
                .build();
        Position position = Position.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill1 = Skill.builder()
                .id(1L)
                .name("Java")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .name("NodeJS")
                .build();

        Long[] skills = {skill1.getId(), skill2.getId()};
        CandidateDto candidateDto = CandidateDto.builder()
                .id(1L)
                .fullName("Anh Anh")
                .email("anh@gmail.com")
                .dateOfBirth(LocalDate.of(2002, 01, 11))
                .address("Hà Nội")
                .phoneNumber("0987654321")
                .gender(Gender.MALE)
                .yearOfExperience(5)
                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
                .position(position.getName())
                .user(user.getUsername())
                .skills(skills)
                .build();
        given(positionRepository.findByName(anyString())).willReturn(Optional.of(position));
        given(userRepository.findByUsernameAndDeletedFalse(anyString())).willReturn(Optional.of(user));
        given(skillRepository.findById(anyLong())).willReturn(Optional.of(new Skill()));
        Candidate candidate = candidateService.create(candidateDto);

        given(candidateRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));
        Optional<Candidate> result = candidateService.findById(candidate.getId());
        assertThat(result).isPresent().get().isEqualTo(candidate);
    }

}
