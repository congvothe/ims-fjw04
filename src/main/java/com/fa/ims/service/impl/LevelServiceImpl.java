package com.fa.ims.service.impl;

import com.fa.ims.entities.Level;
import com.fa.ims.repository.LevelRepository;
import com.fa.ims.service.LevelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LevelServiceImpl implements LevelService {
    private final LevelRepository levelRepository;

    @Override
    public List<Level> findAll() {
        return levelRepository.findAll();
    }
}
