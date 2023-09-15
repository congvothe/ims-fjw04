package com.fa.ims.service.impl;

import com.fa.ims.entities.Position;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.PositionRepository;
import com.fa.ims.service.PositionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    @Override
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public Position findById(Long id) {
        return positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

}
