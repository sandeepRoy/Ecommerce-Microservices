package com.msa.customer.clients;

import com.msa.customer.responses.Root;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "category", url = "http://localhost:8085/admin/category")
public interface CategoryWithProductsClient {
    @GetMapping("all/categoryWithProducts")
    public List<Root> getAllCategoryWithProducts();
 }
