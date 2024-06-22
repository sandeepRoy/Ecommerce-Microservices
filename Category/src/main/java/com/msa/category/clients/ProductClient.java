package com.msa.category.clients;

import com.msa.category.responses.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product", url = "http://localhost:8086/admin/product")
public interface ProductClient {
    @GetMapping("/products/{category_id}")
    public List<ProductResponse> getProductsByCategoryId(@PathVariable Integer category_id);

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts();
}
