package com.msa.category.services;

import com.msa.category.clients.ProductClient;
import com.msa.category.dtos.CategoryDto;
import com.msa.category.entities.Category;
import com.msa.category.repositories.CategoryRepository;
import com.msa.category.responses.AllCategoryProduct;
import com.msa.category.responses.CategoryWithProductsResponse;
import com.msa.category.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public ProductClient productClient;

    // POST - Create Category
    public Category createCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .category_name(categoryDto.getCategory_name())
                .build();
        Category newCategory = categoryRepository.save(category);
        return newCategory;
    }

    // GET - List<Category>
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    // GET - Category by ID
    public ResponseEntity<Category> getCategoryById(Integer category_id) {
        return new ResponseEntity<>(categoryRepository.findById(category_id).get(), HttpStatus.OK);
    }

    // GET - CategoryWithProductsResponse, Category with List<Products>
    public CategoryWithProductsResponse getProductsByCategoryId(Integer category_id) {
        CategoryWithProductsResponse categoryWithProductsResponse = new CategoryWithProductsResponse();

        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("Category Not Found!"));
        categoryWithProductsResponse.setCategory_id(category.getCategory_id());
        categoryWithProductsResponse.setCategory_name(category.getCategory_name());

        List<ProductResponse> productsByCategoryId = productClient.getProductsByCategoryId(category_id);
        categoryWithProductsResponse.setProductList(productsByCategoryId);

        return categoryWithProductsResponse;
    }

    // GET - List<AllCategoryProduct>, All Categories with their Products
    public List<AllCategoryProduct> getAllCategoryWithProducts() {
        List<AllCategoryProduct> list_allProductsWithCategory = new ArrayList<>();
        List<Category> all_categories = categoryRepository.findAll();

        for(Category c : all_categories){
            AllCategoryProduct allCategoryProduct = new AllCategoryProduct();
            allCategoryProduct.setCategory_id(c.getCategory_id());
            allCategoryProduct.setCategory_name(c.getCategory_name());
            List<ProductResponse> productsByCategoryId = productClient.getProductsByCategoryId(c.getCategory_id());
            allCategoryProduct.setProductList(productsByCategoryId);
            list_allProductsWithCategory.add(allCategoryProduct);
        }
        return list_allProductsWithCategory;
    }
}
