package com.msa.authentication.controller;

import com.msa.authentication.entities.User;
import com.msa.authentication.responses.AuthResponse;
import com.msa.authentication.requests.AuthenticateRequest;
import com.msa.authentication.responses.UserProfileResponse;
import com.msa.authentication.services.AuthenticationService;
import com.msa.authentication.requests.RegisterRequest;
import com.msa.authentication.services.JwtService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    public AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthenticateRequest authenticateRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetails> getUserName() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<UserProfileResponse> getPublicDataForUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setFirstName(user.getFirstname());
        userProfileResponse.setLasName(user.getLastname());

        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged Out!";
    }
}
