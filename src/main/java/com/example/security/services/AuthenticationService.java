package com.example.security.services;

import com.example.security.dto.request.AuthenticationRequest;
import com.example.security.dto.request.RegisterRequest;
import com.example.security.dto.response.AuthenticationResponse;
import com.example.security.dto.response.MessageResponse;

public interface AuthenticationService {

    public MessageResponse registerNewUser(RegisterRequest request);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    public MessageResponse confirmAccount(String request);


}
