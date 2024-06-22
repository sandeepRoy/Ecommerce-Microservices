package com.msa.customer.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductList {
    public String product_name;
    public String product_manufacturer;
    public Double product_price;
    public Integer product_inStock;
}
