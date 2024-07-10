package com.msa.customer.dtos;

import com.msa.customer.model.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerProfileDto {
    private String customer_name;
    private String customer_mobile;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
