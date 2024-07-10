package com.msa.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cart_id;
    private Double total_amount;
    private String modeOfPayment;

    @OneToOne
    private Customer customer;

    @OneToMany(
            mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Wishlist> wishlistList;
}
