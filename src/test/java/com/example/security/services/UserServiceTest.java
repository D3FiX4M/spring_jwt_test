package com.example.security.services;

import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserList() {
        User user = User.builder()
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();

        User user1 = User.builder()
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

        List<User> gettingUserList = userService.getUserList();

        assertThat(gettingUserList).isNotNull();
        assertThat(gettingUserList.size()).isEqualTo(userList.size());
    }

    @Test
    void getUserById() {
        int id = 1;
        User user = User.builder()
                .id(id)
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));

        User gettingUser = userService.getUserById(id);

        assertThat(gettingUser).isNotNull();
        assertThat(gettingUser).isEqualTo(user);
    }

    @Test
    void deleteUserById() {
        int id = 1;
        User user = User.builder()
                .id(id)
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        doNothing().when(userRepository).deleteById(id);

        MessageResponse messageResponse = userService.deleteUserById(id);
        assertThat(messageResponse.getMessage()).isEqualTo("User "+ id +" deleted");
    }
}