package com.msa.customer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCustomerDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
