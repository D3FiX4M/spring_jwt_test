package com.example.security.controllers;

import com.example.security.dto.request.IdRequest;
import com.example.security.dto.response.MessageResponse;
import com.example.security.dto.response.UserResponse;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.services.Implementations.UserDetailsServiceImpl;
import com.example.security.services.JwtService;
import com.example.security.services.Implementations.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    User user;
    UserResponse userResponse;

    @BeforeEach
    public void init() {
        user = User.builder()
                .username("test1")
                .password("1")
                .roles(null)
                .email("something@gmail.com")
                .activationCode("active")
                .build();

        userResponse = UserResponse.builder()
                .username("test1")
                .password("1")
                .roles(null)
                .email("something@gmail.com")
                .activationCode("active")
                .build();

    }


    @Test
    void shouldReturnUserList() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userServiceImpl.getUserList()).thenReturn(userList);

        ResultActions response = mockMvc.perform(get("/api/user/userList")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()", CoreMatchers.is(userList.size())));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        int id = 1;
        userResponse.setId(1);
        when(userServiceImpl.getUserById(id)).thenReturn(userResponse);

        ResultActions response = mockMvc.perform(get("/api/user/userList/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponse)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(userResponse.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(userResponse.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", CoreMatchers.is(userResponse.getPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", CoreMatchers.is(userResponse.getRoles())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userResponse.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.activationCode", CoreMatchers.is(userResponse.getActivationCode())));

    }


    @Test
    void shouldDeleteUserById() throws Exception {

        IdRequest idRequest = IdRequest.builder()
                .id(1)
                .build();

        when(userServiceImpl.deleteUserById(idRequest)).thenReturn(new MessageResponse("User " + idRequest.getId() + " deleted"));

        ResultActions response = mockMvc.perform(post("/api/user/userList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(1)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("User " + idRequest.getId() + " deleted")));

    }


}