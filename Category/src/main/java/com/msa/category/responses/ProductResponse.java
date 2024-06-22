package com.msa.category.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer product_id;

    private String product_name;

    private String product_manufacturer;

    private Double product_price;

    private Integer product_inStock;
}
