package com.msa.customer.controllers;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.dtos.CreateWishlistDto;
import com.msa.customer.exceptions.customer.firstLogin.CustomerLoginException;
import com.msa.customer.model.Wishlist;
import com.msa.customer.responses.Root;
import com.msa.customer.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/shop")
public class CustomerShoppingController {
    @Autowired
    public CustomerService customerService;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN = CustomerAuthenticationController.TOKEN;

    @GetMapping("/browse")
    public ResponseEntity<List<Root>> getAllCategoryWithProducts() {
        List<Root> allCategoryWithProducts = customerService.getAllCategoryWithProducts();
        return new ResponseEntity<>(allCategoryWithProducts, HttpStatus.OK);
    }

    @PostMapping("/wishlist")
    public ResponseEntity<Object> addToWishlist(@RequestBody CreateWishlistDto createWishlistDto) throws CustomerLoginException {
        if(TOKEN == "") {
            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
        }
        else {
            Wishlist wishlist = customerService.addToWishList(createWishlistDto);
            return new ResponseEntity<>(wishlist, HttpStatus.OK);
        }
    }

//    @PostMapping("/cart")
//    public ResponseEntity<Object> createCart() {
//        if(TOKEN == "") {
//            return new ResponseEntity<>("Customer Not Logged In!", HttpStatus.UNAUTHORIZED);
//        }
//        else{
//
//        }
//    }
}
