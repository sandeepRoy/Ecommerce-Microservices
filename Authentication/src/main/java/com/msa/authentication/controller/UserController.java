package com.msa.authentication.controller;

import com.msa.authentication.entities.User;
import com.msa.authentication.responses.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {
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
        userProfileResponse.setEmail(user.getEmail());

        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }
}
