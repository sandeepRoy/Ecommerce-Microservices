package com.msa.customer.controllers;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.dtos.LoginCustomerDto;
import com.msa.customer.dtos.RegisterCustomerDto;
import com.msa.customer.exceptions.customer.secondLogin.CustomerPreviouslyLoggedInException;
import com.msa.customer.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/auth")
public class CustomerAuthenticationController {
    @Autowired
    public CustomerService customerService;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN;

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        authenticationClient.registerUser(registerCustomerDto);
        return new ResponseEntity<>("Registration SuccessFull!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@RequestBody LoginCustomerDto loginCustomerDto) throws CustomerPreviouslyLoggedInException {
        TOKEN = authenticationClient.loginUser(loginCustomerDto);
        customerService.setTOKEN(TOKEN);
        customerService.addCustomer(loginCustomerDto);
        return new ResponseEntity<>("Log In SuccessFull!", HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutCustomer() {
        String response = customerService.logoutCustomer();
        TOKEN = "";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
