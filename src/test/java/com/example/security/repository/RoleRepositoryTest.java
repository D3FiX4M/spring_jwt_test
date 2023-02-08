package com.example.security.repository;

import com.example.security.entity.ERole;
import com.example.security.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .name(ERole.USER)
                .build();
    }

    @Test
    @Transactional
    void findByName() {
        roleRepository.save(role);
        boolean isFind = roleRepository.findByName(role.getName()).isPresent();
        assertThat(isFind).isTrue();
    }
}