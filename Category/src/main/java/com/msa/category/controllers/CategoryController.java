package com.msa.category.controllers;

import com.msa.category.dtos.CategoryDto;
import com.msa.category.entities.Category;
import com.msa.category.responses.AllCategoryProduct;
import com.msa.category.responses.CategoryWithProductsResponse;
import com.msa.category.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategory() {
        List<Category> allCategory = categoryService.getAllCategory();

        return new ResponseEntity<>(allCategory, HttpStatus.OK);
    }

    @GetMapping("/{category_id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer category_id) {
        ResponseEntity<Category> categoryById = categoryService.getCategoryById(category_id);
        return categoryById;
    }

    @GetMapping("/{category_id}/products")
    public ResponseEntity<CategoryWithProductsResponse> getProductsByCategoryId(@PathVariable Integer category_id) {
        CategoryWithProductsResponse productsByCategoryId = categoryService.getProductsByCategoryId(category_id);
        return new ResponseEntity<>(productsByCategoryId, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping("all/categoryWithProducts")
    public ResponseEntity<List<AllCategoryProduct>> getAllCategoryWithProducts() {
        List<AllCategoryProduct> allCategoryWithProducts = categoryService.getAllCategoryWithProducts();
        return new ResponseEntity<>(allCategoryWithProducts, HttpStatus.OK);
    }
}
