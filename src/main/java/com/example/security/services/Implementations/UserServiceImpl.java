package com.example.security.services.Implementations;

import com.example.security.dto.request.IdRequest;
import com.example.security.dto.response.MessageResponse;
import com.example.security.dto.response.UserResponse;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    public List<User> getUserList(){

        return userRepository.findAll();
    }

    public UserResponse getUserById(Integer id){

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .roles(user.getRoles())
                .activationCode(user.getActivationCode())
                .build();
    }

    public MessageResponse deleteUserById(IdRequest request){

        userRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Invalid id"));
        userRepository.deleteById(request.getId());
        return new MessageResponse("User "+ request.getId() +" deleted");
    }


}
