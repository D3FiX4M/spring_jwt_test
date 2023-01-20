package com.example.security.controllers;

import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.User;
import com.example.security.services.Implementations.AuthenticationServiceImpl;
import com.example.security.services.Implementations.UserDetailsServiceImpl;
import com.example.security.services.JwtService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationServiceImpl authenticationServiceImpl;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    User user;
    AuthenticationRequest authenticationRequest;

    AuthenticationResponse authenticationResponse;
    RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {

        authenticationRequest = AuthenticationRequest.builder()
                .username("test2")
                .password("1")
                .build();

         authenticationResponse = AuthenticationResponse.builder()
                .username(authenticationRequest.getUsername())
                .roles(null)
                .token("testToken")
                .build();

        registerRequest = RegisterRequest.builder()
                .username("test3")
                .email("something1@gmail.com")
                .password("1")
                .roles(null)
                .build();

    }

    @Test
    void should_Register() throws Exception {

        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenReturn(new MessageResponse("User registered successfully, now to confirm your email account!"));

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("User registered successfully, now to confirm your email account!")));

    }


    @Test
    void should_Register_give_error_Username_already_taken() throws Exception {

        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenReturn(new MessageResponse("Error: Username is already taken!"));

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("Error: Username is already taken!")));

    }

    @Test
    void should_Register_give_error_Email_already_taken() throws Exception {

        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenReturn(new MessageResponse("Error: Email is already taken!"));

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("Error: Email is already taken!")));

    }

    @Test
    void should_Register_give_error_Role_is_invalid() throws Exception {

        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenReturn(new MessageResponse("Error: Roles is invalid"));

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("Error: Roles is invalid")));

    }

    @Test
    void should_Authenticate_Give_400() throws Exception {
        String message = "Invalid activation";
        when(authenticationServiceImpl.authenticate(authenticationRequest)).thenReturn(null);

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(message)));

    }

    @Test
    void should_Authenticate() throws Exception {


        when(authenticationServiceImpl.authenticate(authenticationRequest)).
                thenReturn(authenticationResponse);

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(authenticationResponse.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", CoreMatchers.is(authenticationResponse.getRoles())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", CoreMatchers.is("testToken")));

    }

    @Test
    void should_ConfirmAccount() throws Exception {
        String activationCode = "testCode";
        when(authenticationServiceImpl.confirmAccount(activationCode))
                .thenReturn(new MessageResponse("Account activate successfully!"));

        ResultActions response = mockMvc.perform(get("/api/auth/confirm-account?code=" + activationCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Account activate successfully!")));

    }

}