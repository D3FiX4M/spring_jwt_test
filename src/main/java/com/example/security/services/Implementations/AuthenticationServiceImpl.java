package com.example.security.services.Implementations;

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
import com.example.security.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public MessageResponse registerNewUser(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ExistException("User with the same name already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ExistException("User with this email already exists");
        }

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        strRoles.removeIf(s -> !s.equals("admin") && !s.equals("user"));

        if (strRoles.isEmpty()) {
            throw  new NotFoundException("Roles not found");
        }

        if (strRoles.contains("admin")) {
            Role adminRole = roleRepository.findByName(ERole.ADMIN)
                    .orElseThrow(() -> new NotFoundException("Invalid role ADMIN"));
            roles.add(adminRole);
        } else {
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new NotFoundException("Invalid role USER"));
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
                .orElseThrow(()->new NotFoundException("User not found"));

        if (user.getActivationCode().equals("active")) {

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .username(user.getUsername())
                    .roles(user.getRoles())
                    .token(jwtToken)
                    .build();
        } else {
            throw new ActivateException("Please activate your account");
        }
    }

    public MessageResponse confirmAccount(String request) {
        User user = userRepository.findByActivationCode(request)
                .orElseThrow(() -> new NotFoundException("Activation code not found"));
            user.setActivationCode("active");
            userRepository.save(user);
            return new MessageResponse("Account activate successfully!");
        }
    }

