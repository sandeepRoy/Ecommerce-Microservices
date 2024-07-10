package com.msa.customer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWishlistDto {
    private String product_name;
    private String product_manufacturer;
    private Integer product_quantity;
}
