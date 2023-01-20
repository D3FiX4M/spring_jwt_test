package com.example.security.dto.response;


import com.example.security.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private int id;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
    private String activationCode;
}
