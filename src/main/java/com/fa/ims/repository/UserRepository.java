package com.fa.ims.repository;

import com.fa.ims.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    @Query("FROM User u INNER JOIN FETCH u.role WHERE u.username = :username AND u.deleted = false ")
    Optional<User> findByUsernameAndDeletedFalse(String username);
    boolean existsByUsernameAndDeletedFalse(String username);
    boolean existsByEmailAndDeletedFalse(String email);
    List<User> findAllByRole_Role(String role);
}
