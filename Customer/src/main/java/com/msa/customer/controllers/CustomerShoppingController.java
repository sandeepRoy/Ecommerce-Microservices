package com.msa.customer.controllers;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.dtos.CreateWishlistDto;
import com.msa.customer.exceptions.customer.firstLogin.CustomerLoginException;
import com.msa.customer.model.Cart;
import com.msa.customer.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
public class CustomerShoppingController {
    @Autowired
    public CustomerService customerService;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN = CustomerAuthenticationController.TOKEN;

    @GetMapping("/get-cart")
    public ResponseEntity<Object> getCart() throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        } else {
            Cart cart = customerService.getCart();
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }

    @PutMapping("/update-cart/add-product")
    public ResponseEntity<Object> updateCart_addProduct(@RequestBody CreateWishlistDto createWishlistDto) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            Cart cart = customerService.updateCart_addProduct(createWishlistDto);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }

    @PutMapping("/update-cart/{product_name}/quantity")
    public ResponseEntity<Object> updateCartWishlistQuantity(@PathVariable String product_name, @RequestParam(required = false) Integer quantity) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        } else {
            Cart cart = customerService.updateCart_changeQuantity(product_name, quantity);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }

    @PutMapping("/update-cart/delivery-address/{address_type}")
    public ResponseEntity<Object> updateCartDeliveryAddress(@PathVariable String address_type) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        } else {
            Cart cart = customerService.updateCart_changeDeliveryAddress(address_type);
            return new ResponseEntity<Object>(cart, HttpStatus.OK);
        }
    }

    @PutMapping("/update-cart/mode-of-payment/{payment_type}")
    public ResponseEntity<Object> updateCartModeOfPayment(@PathVariable String payment_type) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            Cart cart = customerService.updateCart_modeOfPayment(payment_type);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }

    @DeleteMapping("/update-cart/remove/{product_name}")
    public ResponseEntity<Object> updateCart_removeProduct(@PathVariable String product_name) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else{
            Cart cart = customerService.updateCart_removeProduct(product_name);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }
}
