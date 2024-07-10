package com.msa.customer.services;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.clients.CategoryWithProductsClient;
import com.msa.customer.clients.ProductClient;
import com.msa.customer.dtos.*;
import com.msa.customer.exceptions.address.add.AddressAdditionException;
import com.msa.customer.exceptions.address.update.AddressUpdateException;
import com.msa.customer.exceptions.customer.firstLogin.CustomerLoginException;
import com.msa.customer.exceptions.customer.secondLogin.CustomerPreviouslyLoggedInException;
import com.msa.customer.model.Address;
import com.msa.customer.model.AddressType;
import com.msa.customer.model.Wishlist;
import com.msa.customer.model.Customer;
import com.msa.customer.repositories.AddressRepository;
import com.msa.customer.repositories.WishlistRepository;
import com.msa.customer.repositories.CustomerRepository;
import com.msa.customer.responses.ProductList;
import com.msa.customer.responses.Root;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

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
    public WishlistRepository wishlistRepository;

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

    // GET - Logged In Customer's Profile and Address
    public Customer getCustomerProfile() throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In!");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).get();

        return found_customer;
    }

    // POST - After Login, make an entry in Customer table
    // Condition - Check Existing entry before inserting new!!!!
    public Customer addCustomer(LoginCustomerDto loginCustomerDto) throws CustomerPreviouslyLoggedInException {
        userEmail = getUserEmail(TOKEN);

        List<Customer> all_customers = customerRepository.findAll();

        for(Customer customer : all_customers) {
            if(customer.getCustomer_email().equals(userEmail)){
                throw new CustomerPreviouslyLoggedInException("Login SuccessFull!");
            }
        }
        Customer new_customer = new Customer();
        new_customer.setCustomer_email(userEmail);
        Customer saved_customer = customerRepository.save(new_customer);
        return saved_customer;
    }

    // PUT - Update Logged In Customer's Profile
    public Customer updateCustomerProfile(UpdateCustomerProfileDto updateCustomerProfileDto) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In!");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).get();

        found_customer.setCustomer_name(updateCustomerProfileDto.getCustomer_name());
        found_customer.setCustomer_mobile(updateCustomerProfileDto.getCustomer_mobile());
        found_customer.setGender(updateCustomerProfileDto.getGender());

        Customer updated_customer = customerRepository.save(found_customer);
        return updated_customer;
    }

    // POST - Add Address to Customer's profile by logged-in user's email
    // Condition - Limit Address by 2
    public Customer addAddressToCustomer(AddressAddDto addressAddDto) throws CustomerLoginException, AddressAdditionException {

        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found!"));

        List<Address> addressList = found_customer.getAddressList();

        if(addressList.size() <= 1) {
            Address new_address = new Address();
            new_address.setCustomer(found_customer);
            new_address.setAddressType(addressAddDto.getAddressType());
            new_address.setAddress(addressAddDto.getAddress());
            new_address.setCity(addressAddDto.getCity());
            new_address.setState(addressAddDto.getState());
            new_address.setPincode(addressAddDto.getPincode());
            addressRepository.save(new_address);
            Customer updated_customer = customerRepository.save(found_customer);
            return updated_customer;
        }
        else{
            throw new AddressAdditionException("More than 2 Addresses are not allowed!");
        }
    }

    public Customer updateAddressOfCustomer(String addressType, UpdateAddressDto updateAddressDto) throws CustomerLoginException, AddressUpdateException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        if(addressType != "HOME" && addressType != "WORK") {
            throw new AddressUpdateException("Incorrect Address Type Provided in Path");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);
        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found!"));

        List<Address> addressList = found_customer.getAddressList();

        for(Address address : addressList) {
            if(address.getAddressType() == AddressType.valueOf(addressType)) {
                address.setAddress(updateAddressDto.getAddress());
                address.setCity(updateAddressDto.getCity());
                address.setState(updateAddressDto.getState());
                address.setPincode(updateAddressDto.getPincode());

                addressRepository.save(address);
            }
        }

        return found_customer;
    }

    // DELETE - Remove an address of Logged-in Customer
    public Customer deleteAddressOfCustomer(String addressType) throws CustomerLoginException {

        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found!"));

        List<Address> addressList = found_customer.getAddressList();
        for(Address address : addressList){
            if(address.getAddressType() == AddressType.valueOf(addressType)) {
                addressList.remove(address);
                addressRepository.delete(address);
            }
        }

        return found_customer;
    }

    public String deleteCustomer() throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer found_customer = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found!"));

        customerRepository.delete(found_customer);

        String response = authenticationClient.removeUser(TOKEN);

        return response;
    }

    // POST - Add Product to Cart with Logged-In User's email
    public Wishlist addToWishList(CreateWishlistDto createWishlistDto) throws CustomerLoginException {

        ProductList productByName = productClient.getProductByName(createWishlistDto.getProduct_name());

        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setProduct_name(createWishlistDto.getProduct_name());
        wishlist.setProduct_manufacturer(createWishlistDto.getProduct_manufacturer());
        wishlist.setProduct_quantity(createWishlistDto.getProduct_quantity());
        wishlist.setPayable_amount(productByName.getProduct_price() * createWishlistDto.getProduct_quantity());
        wishlist.setCustomer(customer_found);

        Wishlist wishlist_user = wishlistRepository.save(wishlist);
        return wishlist_user;
    }



    public String logoutCustomer() {
        TOKEN = "";
        return "Customer Logged Out!";
    }

    public String isValidRequest(CreateWishlistDto createWishlistDto, ProductList productByName) {
        if(productByName.getProduct_inStock() < createWishlistDto.getProduct_quantity()) {
            return "Requested quantity greater than available stock";
        }
        else{
            return "Valid";
        }
    }
}
