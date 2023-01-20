package com.example.security.controllers;


import com.example.security.dto.request.IdRequest;
import com.example.security.services.Implementations.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;


    @GetMapping("/userList")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> userList() {
        return ResponseEntity.ok(userServiceImpl.getUserList());
    }

    @PostMapping("/userList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUserById(@RequestBody IdRequest request){
        return ResponseEntity.ok(userServiceImpl.deleteUserById(request));
    }

    @GetMapping("/userList/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> userById(@PathVariable Integer id ) {
        return ResponseEntity.ok(userServiceImpl.getUserById(id));
    }

}
