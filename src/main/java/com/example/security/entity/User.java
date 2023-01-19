package com.example.security.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User{

  @Id
  @GeneratedValue
  private Integer id;
  private String username;
  private String email;
  private String password;
  private String activationCode;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(	name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

}
