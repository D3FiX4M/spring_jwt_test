package com.example.security.repository;

import com.example.security.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();
    }

    @Test
    @Transactional
    void findByUsername() {

        userRepository.save(user);

        boolean isFind = userRepository.findByUsername(user.getUsername()).isPresent();

        assertThat(isFind).isTrue();

    }

    @Test
    @Transactional
    void findByActivationCode() {

        userRepository.save(user);
        boolean isFind = userRepository.findByActivationCode(user.getActivationCode()).isPresent();

        assertThat(isFind).isTrue();
    }

    @Test
    @Transactional
    void existsByUsername() {
        userRepository.save(user);
        boolean isFind = userRepository.existsByUsername(user.getUsername());

        assertThat(isFind).isTrue();
    }

    @Test
    @Transactional
    void existsByEmail() {
        userRepository.save(user);
        boolean isFind = userRepository.existsByEmail(user.getEmail());

        assertThat(isFind).isTrue();
    }
}