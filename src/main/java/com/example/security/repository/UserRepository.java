package com.example.security.repository;

import java.util.Optional;

import com.example.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByUsername(String username);
  Optional<User> findByActivationCode(String activationCode);
  Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);

}
