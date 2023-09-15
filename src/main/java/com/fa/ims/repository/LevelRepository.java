package com.fa.ims.repository;

import com.fa.ims.entities.Level;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends BaseRepository<Level, Long> {
    @Query(value = "select * from level l" +
            " where l.id  in (select jl.level_id  from job_level jl" +
            " inner join job j on jl.job_id = j.id" +
            " where j.id =:id and jl.is_active=true)", nativeQuery = true)
    List<Level> findLevelByJobId(@Param("id") Long id);
}
