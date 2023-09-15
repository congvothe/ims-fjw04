package com.fa.ims.service.impl;

import com.fa.ims.entities.Skill;
import com.fa.ims.repository.SkillRepository;
import com.fa.ims.service.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    public List<Skill> fillAll() {
        return skillRepository.findAll();
    }
}
