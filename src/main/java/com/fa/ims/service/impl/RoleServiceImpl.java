package com.fa.ims.service.impl;

import com.fa.ims.entities.Role;
import com.fa.ims.repository.RoleRepository;
import com.fa.ims.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
