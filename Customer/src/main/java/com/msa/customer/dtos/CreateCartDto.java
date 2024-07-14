package com.msa.customer.dtos;

import com.msa.customer.model.AddressType;
import com.msa.customer.model.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartDto {
    private String modeOfPayment;
    private String addressType;
}
