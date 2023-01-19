package com.example.security.services;

import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.ERole;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private User user;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testName")
                .password("1")
                .email("something@Email.com")
                .roles(null)
                .activationCode("testCode")
                .build();

        authenticationRequest = AuthenticationRequest.builder()
                .username("testName")
                .password("1")
                .build();

        authenticationResponse = AuthenticationResponse.builder()
                .username("testName")
                .roles(null)
                .token(null)
                .build();

        registerRequest = RegisterRequest.builder()
                .username("validName")
                .password("1")
                .email("validEmail@Email.com")
                .roles(null)
                .build();
    }

    @Test
    void notValidRegisterNewUser_BecauseSameUsername() {

        registerRequest.setUsername("testName");

        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(true);

        MessageResponse messageResponse =
                authenticationService.registerNewUser(registerRequest);

        assertThat(messageResponse.getMessage())
                .isEqualTo("Error: Username is already taken!");

    }

    @Test
    void notValidRegisterNewUser_BecauseSameEmail() {

        registerRequest.setEmail("something@Email.com");

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);

        MessageResponse messageResponse =
                authenticationService.registerNewUser(registerRequest);

        assertThat(messageResponse.getMessage())
                .isEqualTo("Error: Email is already taken!");

    }


    @Test
    void checkRegisterNewUser_withSameRole(){
        Set<String> registerRoles = new HashSet<>();
        //need to comment method for update user role
        registerRoles.add("user");
        registerRoles.add("admin");
        registerRequest.setRoles(registerRoles);

        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.ofNullable(Role.builder().name(ERole.ADMIN).build()));
        //when(roleRepository.findByName(ERole.USER)).thenReturn(Optional.ofNullable(Role.builder().name(ERole.USER).build()));
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(emailSenderService).sendEmail(any());

        MessageResponse messageResponse =
                authenticationService.registerNewUser(registerRequest);

        assertThat(messageResponse.getMessage())
                .isEqualTo("User registered successfully, now to confirm your email account!");
    }



    @Test
    void notValidAuthenticate_BecauseNoActivationEmail() {

        when(userRepository.findByUsername(authenticationRequest.getUsername())).
                thenReturn(Optional.ofNullable(user));

        AuthenticationResponse gettingAuthenticationResponse =
                authenticationService.authenticate(authenticationRequest);

        assertThat(gettingAuthenticationResponse).isEqualTo(null);
    }


    @Test
    void ValidAuthenticate_BecauseActivationEmail_AndGiveToken() {

        user.setActivationCode("active");

        when(userRepository.findByUsername(authenticationRequest.getUsername())).
                thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(user)).thenReturn("testToken");

        AuthenticationResponse gettingAuthenticationResponse =
                authenticationService.authenticate(authenticationRequest);

        assertThat(gettingAuthenticationResponse.getToken()).isEqualTo("testToken");
        assertThat(gettingAuthenticationResponse.getUsername()).isEqualTo(authenticationResponse.getUsername());
        assertThat(gettingAuthenticationResponse.getRoles()).isEqualTo(authenticationResponse.getRoles());

    }

    @Test
    void confirmAccount() {
        String code = "testCode";

        when(userRepository.findByActivationCode(code))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        MessageResponse messageResponse = authenticationService.confirmAccount(code);

        assertThat(user.getActivationCode()).isEqualTo("active");
        assertThat(messageResponse.getMessage())
                .isEqualTo("Account activate successfully!");
    }
}