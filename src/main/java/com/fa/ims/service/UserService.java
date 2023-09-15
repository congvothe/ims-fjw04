package com.fa.ims.service;

import com.fa.ims.dto.UserDto;
import com.fa.ims.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService extends BaseService<User> {

    Page<UserDto> findAll(String search, String role, int page, int size);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAllUserByRole(String role);

    User create(UserDto userDto);

    Map<Long, String> mapUserInformation(List<Long> ids);
}
