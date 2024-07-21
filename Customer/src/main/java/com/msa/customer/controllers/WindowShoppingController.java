package com.msa.customer.controllers;

import com.msa.customer.responses.Root;
import com.msa.customer.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/window-shopping")
public class WindowShoppingController {
    @Autowired
    public CustomerService customerService;

    @GetMapping("/browse")
    public ResponseEntity<List<Root>> getAllCategoryWithProducts() {
        List<Root> allCategoryWithProducts = customerService.getAllCategoryWithProducts();
        return new ResponseEntity<>(allCategoryWithProducts, HttpStatus.OK);
    }
}
