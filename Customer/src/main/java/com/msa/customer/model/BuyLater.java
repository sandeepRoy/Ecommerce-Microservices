package com.msa.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "buylater")
public class BuyLater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer buylater_id;
    private String buylater_product_name;
    private String buylater_product_manufacturer;
    private Integer buylater_product_quantity;
    private Double buylater_payable_amount;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;
}
