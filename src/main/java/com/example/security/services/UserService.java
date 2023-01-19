package com.example.security.services;

import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public List<User> getUserList(){

        return userRepository.findAll();
    }

    public User getUserById(Integer id){

        return  userRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id"));
    }

    public MessageResponse deleteUserById(Integer request){

        userRepository.findById(request).orElseThrow(() -> new RuntimeException("Invalid id"));
        userRepository.deleteById(request);
        return new MessageResponse("User "+ request +" deleted");
    }


}
