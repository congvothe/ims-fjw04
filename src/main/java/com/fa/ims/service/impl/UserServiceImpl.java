package com.fa.ims.service.impl;

import com.fa.ims.dto.UserDto;
import com.fa.ims.entities.Role;
import com.fa.ims.entities.User;
import com.fa.ims.entities.UserOffer;
import com.fa.ims.enums.UserStatus;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.RoleRepository;
import com.fa.ims.repository.UserOfferRepository;
import com.fa.ims.repository.UserRepository;
import com.fa.ims.service.UserService;
import com.fa.ims.util.EnumUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserOfferRepository userOfferRepository;

    @Override
    public User create(UserDto userDto) {
        User user = new User();
        user.setDeleted(false);
        BeanUtils.copyProperties(userDto, user);
        Role role = roleRepository.findByRole(userDto.getRole()).orElseThrow(ResourceNotFoundException::new);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Map<Long, String> mapUserInformation(List<Long> ids) {
        Map<Long, String> users = new HashMap<>();
        ids.forEach(id -> {
            List<UserOffer> userOffers = userOfferRepository.findAllByOffer_IdAndIsActiveTrue(id);
            String userWithRoleManager = userOffers.stream()
                    .map(UserOffer::getUser)
                    .filter(user -> user.getRole().getRole().equals("Manager"))
                    .map(User::getFullName)
                    .collect(Collectors.joining());
            users.put(id, userWithRoleManager);
        });
        return users;
    }

    @Override
    public Page<UserDto> findAll(String search, String role, int page, int size) {
        Specification<User> specification = undeletedSpec();
        if (StringUtils.hasText(search)) {
            Specification<User> searchByKeyword = (root, query, builder) -> builder.or(
                    builder.like(builder.upper(root.get("fullName")), "%" + search.toUpperCase() + "%"),
                    builder.like(builder.upper(root.get("username")), "%" + search.toUpperCase() + "%"),
                    builder.like(builder.upper(root.get("email")), "%" + search.toUpperCase() + "%"),
                    builder.like(builder.upper(root.get("phoneNumber")), "%" + search.toUpperCase() + "%"),
                    builder.like(builder.upper(root.join("role").get("role")), "%" + search.toUpperCase() + "%"));
            if (EnumUtil.isValidName(UserStatus.class, search)) {
                Specification<User> findByStatus = ((root, query, builder) ->
                        builder.equal(root.get("status"), UserStatus.valueOf(search.toUpperCase())));
                searchByKeyword = searchByKeyword.or(findByStatus);
            }
            specification = specification.and(searchByKeyword);
        }
        if (StringUtils.hasText(role)) {
            Specification<User> searchByRole = (root, query, builder) ->
                    builder.equal(root.join("role").get("role"), role);
            specification = specification.and(searchByRole);
        }
        Page<User> userPage = userRepository
                .findAll(specification, PageRequest.of(page, size, Sort.by("lastModifiedDate").descending()));
        return userPage.map(UserDto::new);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeletedFalse(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }

    @Override
    public List<User> findAllUserByRole(String role) {
        return userRepository.findAllByRole_Role(role);
    }
}
