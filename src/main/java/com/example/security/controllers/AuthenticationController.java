package com.example.security.controllers;

import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.services.Implementations.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationServiceImpl.registerNewUser(request));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {

        return ResponseEntity.ok(authenticationServiceImpl.authenticate(request));
    }


    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmAccount(
            @RequestParam("code") String request
    ) {
        return ResponseEntity.ok(authenticationServiceImpl.confirmAccount(request));
    }
}
