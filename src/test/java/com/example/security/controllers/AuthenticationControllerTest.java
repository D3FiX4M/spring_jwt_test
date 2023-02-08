package com.example.security.controllers;

import com.example.security.Exceptions.ActivateException;
import com.example.security.Exceptions.ExistException;
import com.example.security.Exceptions.NotFoundException;
import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.User;
import com.example.security.services.Implementations.AuthenticationServiceImpl;
import com.example.security.services.Implementations.JwtService;
import com.example.security.services.Implementations.UserDetailsServiceImpl;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // REGISTER METHOD

    @Test
    void validRegister() throws Exception {

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
    void notValidRegister_BecauseUsernameAlreadyExists() throws Exception {


        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenThrow(new ExistException("User with the same name already exists"));


        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("User with the same name already exists")));

    }

    @Test
    void notValidRegister_BecauseEmailAlreadyExists() throws Exception {


        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenThrow(new ExistException("User with this email already exists"));


        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("User with this email already exists")));

    }

    @Test
    void notValidRegister_BecauseRoleIsEmpty() throws Exception {


        when(authenticationServiceImpl.registerNewUser(registerRequest)).
                thenThrow(new ExistException("Roles not found"));


        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.message", CoreMatchers.is("Roles not found")));

    }

    // AUTHENTICATE METHOD

    @Test
    void validAuthenticate() throws Exception {


        when(authenticationServiceImpl.authenticate(authenticationRequest)).
                thenReturn(authenticationResponse);

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        String responseAsString = response.andReturn().getResponse().getContentAsString();

        AuthenticationResponse ResponseAsAuthenticationResponse = objectMapper.readValue(responseAsString, AuthenticationResponse.class);

        assertThat(ResponseAsAuthenticationResponse).isEqualTo(authenticationResponse);


    }

    @Test
    void notValidAuthenticate_BecauseInvalidUsername() throws Exception {


        when(authenticationServiceImpl.authenticate(authenticationRequest))
                .thenThrow(new BadCredentialsException("Invalid username"));

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Invalid username")));

    }

    @Test
    void notValidAuthenticate_BecauseInvalidPassword() throws Exception {


        when(authenticationServiceImpl.authenticate(authenticationRequest))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Bad credentials")));

    }

    @Test
    void notValidAuthenticate_BecauseNoActivationEmail() throws Exception {


        when(authenticationServiceImpl.authenticate(authenticationRequest))
                .thenThrow(new ActivateException("Please activate your account"));

        ResultActions response = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Please activate your account")));

    }


    // CONFIRM ACCOUNT METHOD



    @Test
    void validConfirmAccount() throws Exception {

        String activationCode = "testCode";

        when(authenticationServiceImpl.confirmAccount(activationCode))
                .thenReturn(new MessageResponse("Account activate successfully!"));

        ResultActions response = mockMvc.perform(get("/api/auth/confirm-account?code=" + activationCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Account activate successfully!")));

    }

    @Test
    void notValidConfirmAccount() throws Exception {

        String activationCode = "testCode";

        when(authenticationServiceImpl.confirmAccount(activationCode))
                .thenThrow(new NotFoundException("Activation code not found!"));

        ResultActions response = mockMvc.perform(get("/api/auth/confirm-account?code=" + activationCode)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Activation code not found!")));

    }



}