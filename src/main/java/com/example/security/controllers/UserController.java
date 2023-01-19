package com.example.security.controllers;


import com.example.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/userList")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> userList() {
        return ResponseEntity.ok(userService.getUserList());
    }

    @PostMapping("/userList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUserById(@RequestBody Integer request){
        return ResponseEntity.ok(userService.deleteUserById(request));
    }

    @GetMapping("/userList/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> userById(@PathVariable Integer id ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

}
