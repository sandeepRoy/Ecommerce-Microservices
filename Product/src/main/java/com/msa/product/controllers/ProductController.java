package com.msa.product.controllers;

import com.msa.product.dtos.CreateProductDto;
import com.msa.product.entities.Product;
import com.msa.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = productService.getAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer product_id) {
        Product productById = productService.getProductById(product_id);
        return new ResponseEntity<>(productById, HttpStatus.OK);
    }

    @GetMapping("/products/{category_id}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Integer category_id) {
        List<Product> productsByCategoryId = productService.getProductsByCategoryId(category_id);
        return new ResponseEntity<>(productsByCategoryId, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductDto createProductDto) {
        Product product = productService.createProduct(createProductDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/byName")
    public ResponseEntity<Product> getProductByName(@RequestParam(name = "product_name", required = true) String product_name) {
        Product productByName = productService.getProductByName(product_name);
        return new ResponseEntity<>(productByName, HttpStatus.OK);
    }
}
