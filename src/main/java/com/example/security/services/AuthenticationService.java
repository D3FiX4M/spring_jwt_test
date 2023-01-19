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
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public MessageResponse registerNewUser(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse("Error: Email is already taken!");
        }

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        strRoles.removeIf(s -> !s.equals("admin") && !s.equals("user"));

        if (strRoles.isEmpty()) {
            return new MessageResponse("Error: Roles is invalid");
        }

        if (strRoles.contains("admin")) {
            Role adminRole = roleRepository.findByName(ERole.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Invalid role ADMIN"));
            roles.add(adminRole);
        } else {
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Invalid role USER"));
            roles.add(userRole);
        }


        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .activationCode(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("simba162000@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/api/auth/confirm-account?code=" + user.getActivationCode());

        emailSenderService.sendEmail(mailMessage);

        return new MessageResponse("User registered successfully, now to confirm your email account!");
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                ));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(NoSuchElementException::new);

        if (user.getActivationCode().equals("active")) {

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .username(user.getUsername())
                    .roles(user.getRoles())
                    .token(jwtToken)
                    .build();
        } else {
            return null;
        }
    }

    public MessageResponse confirmAccount(String request) {
        User user = userRepository.findByActivationCode(request)
                .orElseThrow(() -> new RuntimeException("Invalid activation code"));
            user.setActivationCode("active");
            userRepository.save(user);
            return new MessageResponse("Account activate successfully!");
        }
    }

