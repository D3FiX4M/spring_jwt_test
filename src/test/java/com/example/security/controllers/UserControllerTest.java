package com.example.security.controllers;

import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.User;
import com.example.security.services.Implementations.UserDetailsServiceImpl;
import com.example.security.services.JwtService;
import com.example.security.services.UserService;
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
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    User user;

    @BeforeEach
    public void init() {
        user = User.builder()
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

        when(userService.getUserList()).thenReturn(userList);

        ResultActions response = mockMvc.perform(get("/api/user/userList")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()", CoreMatchers.is(userList.size())));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        int id = 1;
        when(userService.getUserById(id)).thenReturn(user);

        ResultActions response = mockMvc.perform(get("/api/user/userList/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(user.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", CoreMatchers.is(user.getPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", CoreMatchers.is(user.getRoles())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.activationCode", CoreMatchers.is(user.getActivationCode())));
    }


    @Test
    void shouldDeleteUserById() throws Exception {
        int id = 1;
        when(userService.deleteUserById(id)).thenReturn(new MessageResponse("User "+ id +" deleted"));

        ResultActions response = mockMvc.perform(post("/api/user/userList")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(1)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("User "+ id +" deleted")));

    }


}