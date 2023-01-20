package com.example.security.services;

import com.example.security.dto.request.IdRequest;
import com.example.security.dto.response.MessageResponse;
import com.example.security.dto.response.UserResponse;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.services.Implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();

    }

    @Test
    void getUserList() {

        User user1 = User.builder()
                .id(2)
                .username("testName1")
                .password("1")
                .email("something1@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> gettingUserList = userServiceImpl.getUserList();

        assertThat(gettingUserList).isNotNull();
        assertThat(gettingUserList.size()).isEqualTo(userList.size());
    }

    @Test
    void getUserById() {
        int id = 1;
        UserResponse userResponse = UserResponse.builder()
                .id(id)
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));

        UserResponse gettingUser = userServiceImpl.getUserById(id);

        assertThat(gettingUser).isNotNull();
        assertThat(gettingUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(gettingUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void deleteUserById() {
        IdRequest idRequest = IdRequest.builder()
                .id(1)
                .build();

        when(userRepository.findById(idRequest.getId())).thenReturn(Optional.ofNullable(user));
        doNothing().when(userRepository).deleteById(idRequest.getId());

        MessageResponse messageResponse = userServiceImpl.deleteUserById(idRequest);
        assertThat(messageResponse.getMessage()).isEqualTo("User " + idRequest.getId() + " deleted");
    }
}