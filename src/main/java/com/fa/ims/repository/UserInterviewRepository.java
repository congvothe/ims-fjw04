package com.fa.ims.repository;

import com.fa.ims.entities.Interview;
import com.fa.ims.entities.UserInterview;
import com.fa.ims.entities.UserInterviewId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterviewRepository extends BaseRepository <UserInterview, UserInterviewId> {
    List<UserInterview> findAllByInterview_IdAndUser_Role_RoleAndIsActiveTrue(Long id, String role);
    List<UserInterview> findAllByInterviewAndIsActiveTrue(Interview interview);
}
