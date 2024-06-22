package com.msa.customer.controllers;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.dtos.CreateCartDto;
import com.msa.customer.dtos.LoginCustomerDto;
import com.msa.customer.dtos.RegisterCustomerDto;
import com.msa.customer.responses.Root;
import com.msa.customer.services.CustomerService;
import com.msa.customer.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    public CustomerService customerService;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN;

    @GetMapping("/browse")
    public ResponseEntity<List<Root>> getAllCategoryWithProducts() {
        List<Root> allCategoryWithProducts = customerService.getAllCategoryWithProducts();
        return new ResponseEntity<>(allCategoryWithProducts, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        authenticationClient.registerUser(registerCustomerDto);
        return new ResponseEntity<>("Registration SuccessFull!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@RequestBody LoginCustomerDto loginCustomerDto) {
        TOKEN = authenticationClient.loginUser(loginCustomerDto);// TOKEN is in JSON
        customerService.setTOKEN(TOKEN);
        return new ResponseEntity<>("Log In SuccessFull!", HttpStatus.OK);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Cart> addToCart(@RequestBody CreateCartDto createCartDto) {
        if(TOKEN == null) {
            throw new RuntimeException("Customer Not Logged In or Registered!");
        }
        else {
            Cart cart = customerService.addToCart(createCartDto);
            return new ResponseEntity<>(cart, HttpStatus.OK);
            // test this
            // test 1 : using token as RequestParam: user_email populated
            // test 2 : generating token in background, logout not working (current logged in user stays logged in)
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutCustomer() {
        String response = customerService.logoutCustomer();
        TOKEN = "";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Change the project perspective as a customer - (Y)

    // Controllers
    // 1 : browse without login (Y)
    // 2:  add To Cart (user needs to be logged in, save data to cart table)
    // 3:  pay (no need to login again, save data to order table)
    // 4:  update profile (no need to login again, save data in user table)
    // 5:  logout

    // Conditions
    // 1: If logged in to update profile, no need to login again to add To Cart or pay
    // 2 : If not logged in, and done add to Cart, ask to login
    // 3: if logged in during add to cart, not required to login for payment
}
