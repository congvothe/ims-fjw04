package com.fa.ims.service;

import com.fa.ims.dto.InterviewDto;
import com.fa.ims.entities.Interview;
import com.fa.ims.enums.InterviewResult;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface InterviewService extends BaseService<Interview> {
    Page<InterviewDto> findAll(String search, LocalDate date, InterviewResult result,
                               String status, int page, int size);

    Interview create(InterviewDto interviewDto);

    InterviewDto getInformationInterview(Long id);

    void update(InterviewDto interviewDto);

    void updateResultInterview(InterviewDto interviewDto);

    List<InterviewDto> candidateResultPass();
}
