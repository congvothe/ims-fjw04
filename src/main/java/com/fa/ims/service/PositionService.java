package com.fa.ims.service;

import com.fa.ims.entities.Position;

import java.util.List;
import java.util.Optional;

public interface PositionService {
    List<Position> findAll();

    Position findById(Long id);
}
