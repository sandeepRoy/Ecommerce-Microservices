package com.msa.customer.services;


import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.clients.CategoryWithProductsClient;
import com.msa.customer.clients.ProductClient;
import com.msa.customer.dtos.CreateCartDto;
import com.msa.customer.dtos.LoginCustomerDto;
import com.msa.customer.dtos.UpdateAddressDto;
import com.msa.customer.dtos.UpdateCustomerDto;
import com.msa.customer.model.Address;
import com.msa.customer.model.Cart;
import com.msa.customer.model.Customer;
import com.msa.customer.repositories.AddressRepository;
import com.msa.customer.repositories.CartRepository;
import com.msa.customer.repositories.CustomerRepository;
import com.msa.customer.responses.ProductList;
import com.msa.customer.responses.Root;
import com.msa.customer.responses.UserProfileResponse;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {

    public static final String SECRET_KEY = "cKNpYuq49z28DN+sH1FpDVLWX4vMd12QGWHx62oj3BGrQ4uNCr4Yxm5St3/P5dMUgVt3aK/0BK+zuqnQHMYZ1xAUMTpV09YCXimbAP2SYkUlZqI1XbIT5Idxsdu41xZ+VZAH3h6EZ+2WdrzezxJJ30URiyHu7bgGMPwoQjxidd5HR0uv7BVhD9xxEkI4jgWaZl0i9uKAZSqFfTaCTKCUzbK/COBQbj1SUQ7qT30XBTSdla+lK04wLAaJeiyGoXxnNFfMlS20uzmBJba8AdHxpmMmajptR8BdAUf+2HaX2MSHCzZRHXNwuW7mxFbDrMl0JpCAABBSMd7E51GaDnA1Vjcao7rzFuLVCXzkNt8P4F4";
    public static final String TOKEN_PREFIX = "Bearer ";
    private static String userEmail;
    private static String userName;

    @Autowired
    public CategoryWithProductsClient categoryWithProductsClient;

    @Autowired
    public ProductClient productClient;

    @Autowired
    public CartRepository cartRepository;

    @Autowired
    public CustomerRepository customerRepository;

    @Autowired
    public AddressRepository addressRepository;

    @Autowired
    public AuthenticationClient authenticationClient;

    public static String TOKEN;

    public void setTOKEN(String TOKEN) {
        String processed_token = "";
        for(int i = 10; i < TOKEN.length() - 2; i++){
            processed_token += TOKEN.charAt(i);
        }
        CustomerService.TOKEN = processed_token;
    }

    public String getUserEmail(String TOKEN) {
        String user_email = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(TOKEN.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        System.out.println("86: Customer Service -- " + user_email);
        return user_email;
    }

    // GET - List<Root>, return all Categories with Products associated with them
    public List<Root> getAllCategoryWithProducts() {
        List<Root> allCategoryWithProducts = categoryWithProductsClient.getAllCategoryWithProducts();
        return allCategoryWithProducts;
    }

    // POST - After Login, make an entry in Customer table
    public Customer addCustomer(LoginCustomerDto loginCustomerDto) {
        userEmail = getUserEmail(TOKEN);

        Customer customer = new Customer();
        customer.setCustomer_email(loginCustomerDto.getEmail());

        Customer new_customer = customerRepository.save(customer);
        return new_customer;
    }

    // PUT - Update Customer's profile by logged-in user's email
    // Condition - Get Logged-In User's firstname and lastName from Authentication Server - ??
    public Customer updateCustomerProfile(UpdateCustomerDto updateCustomerDto) {

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found!"));

        found_customer.setCustomer_name(updateCustomerDto.getCustomer_name()); userName = updateCustomerDto.getCustomer_name();
        found_customer.setCustomer_mobile(updateCustomerDto.getCustomer_mobile());
        found_customer.setGender(updateCustomerDto.getGender());

        UpdateAddressDto updateAddressDto = updateCustomerDto.getUpdateAddressDto();
        Address new_address = new Address();
        new_address.setCustomer(found_customer);
        new_address.setAddressType(updateAddressDto.getAddressType());
        new_address.setAddress(updateAddressDto.getAddress());
        new_address.setCity(updateAddressDto.getCity());
        new_address.setState(updateAddressDto.getState());
        new_address.setPincode(updateAddressDto.getPincode());
        addressRepository.save(new_address);

        Customer updated_customer = customerRepository.save(found_customer);
        return updated_customer;
    }


    // POST - Add Product to Cart with Logged-In User's email
    public Cart addToCart(CreateCartDto createCartDto) {

        ProductList productByName = productClient.getProductByName(createCartDto.getProduct_name());

        Cart cart = new Cart();
        cart.setUser_email(userEmail);
        cart.setUser_name(userName);
        cart.setProduct_name(createCartDto.getProduct_name());
        cart.setProduct_price(productByName.getProduct_price());
        cart.setProduct_manufacturer(createCartDto.getProduct_manufacturer());
        cart.setProduct_quantity(createCartDto.getProduct_quantity());
        cart.setPayable_amount(productByName.getProduct_price() * createCartDto.getProduct_quantity());

        Cart cart_user = cartRepository.save(cart);
        return cart_user;
    }

    public String logoutCustomer() {
        TOKEN = "";
        return "Customer Logged Out!";
    }

    public String isValidRequest(CreateCartDto createCartDto, ProductList productByName) {
        if(productByName.getProduct_inStock() < createCartDto.getProduct_quantity()) {
            return "Requested quantity greater than available stock";
        }
        else{
            return "Valid";
        }
    }
}
