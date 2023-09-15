package com.fa.ims.repository;

import com.fa.ims.entities.Position;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends BaseRepository<Position, Long> {
    Optional<Position> findByName(String name);
}
