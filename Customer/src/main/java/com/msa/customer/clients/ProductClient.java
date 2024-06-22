package com.msa.customer.clients;

import com.msa.customer.responses.ProductList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product", url = "http://localhost:8086/admin/product")
public interface ProductClient {
    @GetMapping("/byName")
    public ProductList getProductByName(@RequestParam(name = "product_name", required = true) String product_name);
}
