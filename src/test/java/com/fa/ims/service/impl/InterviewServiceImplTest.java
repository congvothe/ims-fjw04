package com.fa.ims.service.impl;//package com.fa.ims.service.impl;
//
//import com.fa.ims.entities.*;
//import com.fa.ims.enums.*;
//import com.fa.ims.repository.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//class InterviewServiceImplTest {
//
//    @Mock
//    private InterviewRepository interviewRepository;
//    @Mock
//    private CandidateRepository candidateRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private UserInterviewRepository userInterviewRepository;
//    @Mock
//    private CandidateSkillRepository candidateSkillRepository;
//    @Mock
//    private SkillRepository skillRepository;
//    @Mock
//    private PositionRepository positionRepository;
//
//    @InjectMocks
//    private UserInterviewServiceImpl userInterviewService;
//
//    @Test
//    void findAll() {
//    }
//
//    @Test
//    public void create() throws IOException {
//        Role role = Role.builder()
//                .id(1L)
//                .role("ADMIN")
//                .build();
//        //Add user
//        User user = User.builder()
//                .id(1L)
//                .fullName("Test Admin")
//                .dateOfBirth(LocalDate.of(1990, 01, 110))
//                .email("admin@gmail.com")
//                .address("Ha Noi")
//                .phoneNumber("012345678")
//                .gender(Gender.MALE)
//                .department(Department.A)
//                .username("linh")
//                .password("$2a$12$jDTu8HxlvxbImzadO8..8OpIB9jGL2GoOOoKH8HzHujlebLCHnALO")
//                .status(UserStatus.ACTIVATED)
//                .role(role)
//                .build();
//
//        //Add Skill
//        Position position = Position.builder()
//                .id(1L)
//                .name("Java")
//                .build();
//        Skill skill1 = Skill.builder()
//                .id(1L)
//                .name("Java")
//                .build();
//        Skill skill2 = Skill.builder()
//                .id(2L)
//                .name("NodeJS")
//                .build();
//
//        //Add Candidate to InterView
//        Long[] skills = {skill1.getId(), skill2.getId()};
//        Candidate candidate = Candidate.builder()
//                .id(1L)
//                .fullName("Vo The Cong")
//                .email("cong@mail.com")
//                .dateOfBirth(LocalDate.of(1997, 02, 27))
//                .address("Ha Noi")
//                .phoneNumber("0888002279")
//                .gender(Gender.MALE)
//                .status(CandidateStatus.OPEN)
//                .yearOfExperience(1)
//                .highestLevel(HighestLevel.BACHELOR_S_DEGREE)
//                .position(positionRepository.findByName("HR").orElseThrow()
//                )
//                .user(user)
//                .build();
//        Interview interview = Interview.builder()
//                .id(1L)
//                .title("Test 1_Interview Fresher Java")
//                .scheduleDate(LocalDate.of(2023, 04, 28))
//                .scheduleFrom(LocalTime.of(9,30))
//                .scheduleTo(LocalTime.of(10,30))
//                .location("17 Duy Tan")
//                .meetingId("HN_JWEB")
//
//                .build();
//    }
//
//    @Test
//    void getInformationInterview() {
//    }
//
//    @Test
//    void update() {
//    }
//
//    @Test
//    void delete() {
//    }
//}