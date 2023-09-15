package com.fa.ims.repository;

import com.fa.ims.entities.Candidate;
import com.fa.ims.entities.CandidateSkill;
import com.fa.ims.entities.CandidateSkillId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateSkillRepository extends BaseRepository<CandidateSkill, CandidateSkillId> {
    List<CandidateSkill> findByCandidateAndIsActiveTrue(Candidate candidate);
}
