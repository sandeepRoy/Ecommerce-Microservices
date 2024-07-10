package com.msa.customer.clients;

import com.msa.customer.dtos.LoginCustomerDto;
import com.msa.customer.dtos.RegisterCustomerDto;
import com.msa.customer.responses.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "authentication", url = "http://localhost:8084/auth")
public interface AuthenticationClient {
    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterCustomerDto registerCustomerDto);

    @PostMapping("/login")
    public String loginUser(@RequestBody LoginCustomerDto loginCustomerDto);

    @DeleteMapping("/remove")
    public String removeUser(@RequestParam String token);
}
