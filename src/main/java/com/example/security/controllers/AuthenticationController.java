package com.example.security.controllers;

import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;
import com.example.security.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.registerNewUser(request));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse isAuthenticate = authenticationService.authenticate(request);

        if (isAuthenticate == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid activation"));
        }
        return ResponseEntity.ok(isAuthenticate);
    }


    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmAccount(
            @RequestParam("code") String request
    ) {
        return ResponseEntity.ok(authenticationService.confirmAccount(request));
    }
}
