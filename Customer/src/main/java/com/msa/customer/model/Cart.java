package com.msa.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cart_id;
    private Double total_amount;
    private String modeOfPayment;
    private String customer_name;
    private String customer_email;
    private String customer_mobile;
    @Enumerated(EnumType.STRING)
    private Gender customer_gender;

    // make appear cart with multiple wishlists
    @OneToMany(
            mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Wishlist> wishlist = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "deliveryAddress_id")
    private Address delivery_address;

    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    @JsonIgnore
    private Customer customer;

    // Helper method to add wishlist items to the cart
    public void addWishlistItem(Wishlist item) {
        wishlist.add(item);
        item.setCart(this);
    }

    // Helper method to remove wishlist items from the cart
    public void removeWishlistItem(Wishlist item) {
        wishlist.remove(item);
        item.setCart(null);
    }
}
