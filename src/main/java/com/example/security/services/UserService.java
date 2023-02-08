package com.example.security.services;

import com.example.security.dto.request.IdRequest;
import com.example.security.dto.response.MessageResponse;
import com.example.security.dto.response.UserResponse;
import com.example.security.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    public List<User> getUserList();

    public UserResponse getUserById(Integer request);

    public MessageResponse deleteUserById(IdRequest request);
}
