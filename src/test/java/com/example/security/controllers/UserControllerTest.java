package com.example.security.controllers;

import com.example.security.Exceptions.NotFoundException;
import com.example.security.dto.request.IdRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.dto.response.UserResponse;
import com.example.security.entity.User;
import com.example.security.services.Implementations.UserDetailsServiceImpl;
import com.example.security.services.Implementations.JwtService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    // GET USER LIST METHOD

    @Test
    void validReturnUserList() throws Exception {
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
    void validReturnUserById() throws Exception {
        int id = 1;
        userResponse.setId(1);
        when(userServiceImpl.getUserById(id)).thenReturn(userResponse);

        ResultActions response = mockMvc.perform(get("/api/user/userList/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponse)));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        String responseAsString = response.andReturn().getResponse().getContentAsString();

        UserResponse ResponseAsUserResponse = objectMapper.readValue(responseAsString, UserResponse.class);

        assertThat(ResponseAsUserResponse).isEqualTo(userResponse);

    }

    @Test
    void notValidReturnUserById_() throws Exception {
        int id = 1;
        userResponse.setId(1);
        when(userServiceImpl.getUserById(id)).thenThrow(new NotFoundException("User with this id was not found"));

        ResultActions response = mockMvc.perform(get("/api/user/userList/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponse)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("User with this id was not found")));

    }

    // DELETE USER BY ID METHOD

    @Test
    void validDeleteUserById() throws Exception {

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

    @Test
    void notValidDeleteUserById() throws Exception {

        IdRequest idRequest = IdRequest.builder()
                .id(1)
                .build();

        when(userServiceImpl.getUserById(idRequest.getId())).thenThrow(new NotFoundException("User with this id was not found"));

        ResultActions response = mockMvc.perform(get("/api/user/userList/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponse)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("User with this id was not found")));

    }




}