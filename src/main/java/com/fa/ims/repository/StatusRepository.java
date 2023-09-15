package com.fa.ims.repository;

import com.fa.ims.entities.Status;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends BaseRepository<Status, Long> {
    Optional<Status> findByNameAndStage(String name, String stage);
    List<Status> findAllByStage(String stage);

}
