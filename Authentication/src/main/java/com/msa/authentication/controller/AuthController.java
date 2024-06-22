package com.msa.authentication.controller;

import com.msa.authentication.entities.User;
import com.msa.authentication.responses.AuthResponse;
import com.msa.authentication.requests.AuthenticateRequest;
import com.msa.authentication.services.AuthenticationService;
import com.msa.authentication.requests.RegisterRequest;
import com.msa.authentication.services.JwtService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    public AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("HITTTT!!");
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthenticateRequest authenticateRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }

    /* Need a logout controller as well, because session is staying on for current user and can't log out from Customer
    * Intention is -
    * 1. Attempt 1 : when ever we hit this endpoint, token associated with this user should be eliminated, so that other user can log in
    * 2. Attempt 2 : Can we expire the user?
     */

    @PostMapping("/logout")
    public String logout() {
        return "Logged Out!";
    }
}
