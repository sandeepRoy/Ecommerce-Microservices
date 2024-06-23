package com.msa.customer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wishlist_id;

    private String user_name; // should be fetched from customer table

    private String user_email; // should be fetched from customer table

    private String product_name;

    private String product_manufacturer;

    private Double product_price;

    private Integer product_quantity;

    private Double payable_amount;
}
