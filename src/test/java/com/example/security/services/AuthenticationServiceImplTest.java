package com.example.security.services;

import com.example.security.Exceptions.ActivateException;
import com.example.security.Exceptions.ExistException;
import com.example.security.Exceptions.NotFoundException;
import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.entity.ERole;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import com.example.security.services.Implementations.AuthenticationServiceImpl;
import com.example.security.services.Implementations.EmailSenderService;
import com.example.security.services.Implementations.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;
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
                .roles(new HashSet<>())
                .build();
    }


    // REGISTER NEW USER METHOD

    @Test
    void ValidRegisterNewUser(){
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
                authenticationServiceImpl.registerNewUser(registerRequest);

        assertThat(messageResponse.getMessage())
                .isEqualTo("User registered successfully, now to confirm your email account!");
    }


    @Test
    void notValidRegisterNewUser_BecauseSameUsername() {

        when(userRepository.existsByUsername(registerRequest.getUsername()))
                .thenReturn(true);

        ExistException exception = assertThrows(ExistException.class,
                () -> {authenticationServiceImpl.registerNewUser(registerRequest);},
                "User with the same name already exists");

        assertEquals("User with the same name already exists", exception.getMessage());
    }

    @Test
    void notValidRegisterNewUser_BecauseSameEmail() {

        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);

        ExistException exception = assertThrows(ExistException.class,
                () -> {authenticationServiceImpl.registerNewUser(registerRequest);},
                "User with this email already exists");

        assertEquals("User with this email already exists", exception.getMessage());

    }


    @Test
    void notValidRegisterNewUser_BecauseRoleIsEmpty(){

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> {authenticationServiceImpl.registerNewUser(registerRequest);},
                "Roles not found");

        assertEquals("Roles not found", exception.getMessage());

    }


    // AUTHENTICATE METHOD

    @Test
    void ValidAuthenticate() {

        user.setActivationCode("active");

        when(userRepository.findByUsername(authenticationRequest.getUsername())).
                thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(user)).thenReturn("testToken");

        AuthenticationResponse gettingAuthenticationResponse =
                authenticationServiceImpl.authenticate(authenticationRequest);

        assertThat(gettingAuthenticationResponse.getToken()).isEqualTo("testToken");
        assertThat(gettingAuthenticationResponse.getUsername()).isEqualTo(authenticationResponse.getUsername());
        assertThat(gettingAuthenticationResponse.getRoles()).isEqualTo(authenticationResponse.getRoles());

    }


    @Test
    void notValidAuthenticate_BecauseInvalidUsername() {

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                ))).
                thenThrow(new BadCredentialsException("Invalid username"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> {authenticationServiceImpl.authenticate(authenticationRequest);},
                "Invalid username");

        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void notValidAuthenticate_BecauseInvalidPassword() {

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                ))).
                thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> {authenticationServiceImpl.authenticate(authenticationRequest);},
                "Bad credentials");

        assertEquals("Bad credentials", exception.getMessage());
    }


    @Test
    void notValidAuthenticate_BecauseNoActivationEmail() {

        when(userRepository.findByUsername(authenticationRequest.getUsername())).
                thenReturn(Optional.ofNullable(user));

        ActivateException exception = assertThrows(ActivateException.class,
                () -> {authenticationServiceImpl.authenticate(authenticationRequest);},
                "Please activate your account");

        assertEquals("Please activate your account", exception.getMessage());
    }


    /// CONFIRM ACCOUNT METHOD

    @Test
    void ValidConfirmAccount() {
        String code = "testCode";

        when(userRepository.findByActivationCode(code))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        MessageResponse messageResponse = authenticationServiceImpl.confirmAccount(code);

        assertThat(user.getActivationCode()).isEqualTo("active");
        assertThat(messageResponse.getMessage())
                .isEqualTo("Account activate successfully!");
    }

    @Test
    void NotValidConfirmAccount() {
        String code = "testCode";

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> {authenticationServiceImpl.confirmAccount(code);},
                "Activation code not found");

        assertEquals("Activation code not found", exception.getMessage());

    }
}