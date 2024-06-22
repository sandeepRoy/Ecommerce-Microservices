package com.msa.category.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithProductsResponse {
    private Integer category_id;
    private String category_name;
    private List<ProductResponse> productList;
}
