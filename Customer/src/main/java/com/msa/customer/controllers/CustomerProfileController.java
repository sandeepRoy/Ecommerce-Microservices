package com.msa.customer.controllers;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.dtos.*;
import com.msa.customer.exceptions.address.add.AddressAdditionException;
import com.msa.customer.exceptions.address.update.AddressUpdateException;
import com.msa.customer.exceptions.customer.firstLogin.CustomerLoginException;
import com.msa.customer.model.BuyLater;
import com.msa.customer.model.Cart;
import com.msa.customer.model.Customer;
import com.msa.customer.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/profile")
public class CustomerProfileController {
    @Autowired
    public CustomerService customerService;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN = CustomerAuthenticationController.TOKEN;


    @GetMapping("/details")
    public ResponseEntity<Object> getCustomerProfile() throws CustomerLoginException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            Customer customerProfile = customerService.getCustomerProfile();
            return new ResponseEntity<>(customerProfile, HttpStatus.OK);
        }
    }

    @PutMapping("/update/personal-details")
    public ResponseEntity<Object> updateCustomerProfile(@RequestBody UpdateCustomerProfileDto updateCustomerProfileDto) throws CustomerLoginException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else{
            Customer customer = customerService.updateCustomerProfile(updateCustomerProfileDto);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
    }

    @PostMapping("/add-address")
    public ResponseEntity<Object> addAddressForCustomer(@RequestBody AddressAddDto addressAddDto) throws CustomerLoginException, AddressAdditionException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else{
            Customer customer_with_address = customerService.addAddressToCustomer(addressAddDto);
            return new ResponseEntity<>(customer_with_address, HttpStatus.OK);
        }
    }

    @PutMapping("/update-address/{address_type}")
    public ResponseEntity<Object> updateAddressOfCustomer(@PathVariable String address_type, @RequestBody UpdateAddressDto updateAddressDto) throws CustomerLoginException, AddressUpdateException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else{
            Customer customer = customerService.updateAddressOfCustomer(address_type, updateAddressDto);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
    }

    @DeleteMapping("/remove-address/{address_type}")
    public ResponseEntity<Customer> deleteAddressOfCustomer(@PathVariable String address_type) throws CustomerLoginException {
        if(TOKEN == "") {
            return new ResponseEntity<>(new Customer(), HttpStatus.UNAUTHORIZED);
        }
        else {
            Customer customer = customerService.deleteAddressOfCustomer(address_type);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
    }

    @PostMapping("/add-to-buylater")
    public ResponseEntity<Object> addBuyLater_newProduct(@RequestBody CreateWishlistDto createWishlistDto) throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            BuyLater buyLater = customerService.addBuyLater_newProduct(createWishlistDto);
            return new ResponseEntity<>(buyLater, HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCustomer() throws CustomerLoginException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else{
            String response = customerService.deleteCustomer();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // Development Phase - Adding BuyLater items to Cart for purchase

    // Testing 1 : Existing Cart & WishList : Pass
    // Testing 2 : Non Existing Cart & Wishlist : In Progress
    @PutMapping("/add-buylater-to-cart")
    public ResponseEntity<Object> addToCart_buyLater() throws CustomerLoginException {
        if (TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            Cart cart = customerService.updateCart_addBuyLater();
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
    }
}
