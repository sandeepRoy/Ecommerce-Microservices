package com.msa.customer.services;

import com.msa.customer.clients.AuthenticationClient;
import com.msa.customer.clients.CategoryWithProductsClient;
import com.msa.customer.clients.ProductClient;
import com.msa.customer.dtos.*;
import com.msa.customer.exceptions.address.add.AddressAdditionException;
import com.msa.customer.exceptions.address.update.AddressUpdateException;
import com.msa.customer.exceptions.customer.firstLogin.CustomerLoginException;
import com.msa.customer.exceptions.customer.secondLogin.CustomerPreviouslyLoggedInException;
import com.msa.customer.model.*;
import com.msa.customer.repositories.AddressRepository;
import com.msa.customer.repositories.CartRepository;
import com.msa.customer.repositories.WishlistRepository;
import com.msa.customer.repositories.CustomerRepository;
import com.msa.customer.repositories.BuyLaterRepository;
import com.msa.customer.responses.ProductList;
import com.msa.customer.responses.Root;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import javax.management.RuntimeMBeanException;

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
    public CartRepository cartRepository;

    @Autowired
    public BuyLaterRepository buyLaterRepository;

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

    // GET - Cart of a customer
    public Cart getCart() throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        Cart cart = customer_found.getCart();
        return cart;
    }

    // POST - Add Wishlist items, Delivery Address and Customer to Cart
    public Cart addToCart_wishlist(CreateCartDto createCartDto) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Cart cart = new Cart();

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        List<Address> foundCustomer_addressList = customer_found.getAddressList();
        List<Wishlist> foundCustomer_wishlist = customer_found.getWishlist();

        Address delivery_address = new Address();

        for(Address address : foundCustomer_addressList) {
            if(address.getAddressType().equals(AddressType.valueOf(createCartDto.getAddressType()))) {
                delivery_address = address;
            }
        }

        Double totalPayableAmount = getTotalPayableAmount(foundCustomer_wishlist);

        cart.setCustomer_name(customer_found.getCustomer_name());
        cart.setCustomer_mobile(customer_found.getCustomer_mobile());
        cart.setCustomer_email(customer_found.getCustomer_email());
        cart.setCustomer_gender(customer_found.getGender());
        cart.setCustomer(customer_found);
        cart.setTotal_amount(totalPayableAmount);
        cart.setModeOfPayment(createCartDto.getModeOfPayment());
        cart.setDelivery_address(delivery_address);

        for(Wishlist wishlist : foundCustomer_wishlist) {
            cart.addWishlistItem(wishlist);
        }

        Cart save = cartRepository.save(cart);

        return save;
    }

    // PUT - Update cart with new wishlist item
    public Cart updateCart_addProduct(CreateWishlistDto createWishlistDto) throws CustomerLoginException {
        ProductList productByName = productClient.getProductByName(createWishlistDto.getProduct_name());

        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        List<Wishlist> customer_wishlist = customer_found.getWishlist();

        Wishlist new_wish = new Wishlist();

        Cart cart = customer_found.getCart();

        // if no cart created earlier
        if(cart == null) {
            Cart new_cart = createNewCart();

            List<Wishlist> cartWishlist = new_cart.getWishlist();

            // assign data to wish
            new_wish.setProduct_name(productByName.getProduct_name());
            new_wish.setProduct_manufacturer(productByName.getProduct_manufacturer());
            new_wish.setProduct_quantity(createWishlistDto.getProduct_quantity());
            new_wish.setPayable_amount(productByName.getProduct_price() * createWishlistDto.getProduct_quantity());
            new_wish.setCustomer(customer_found);
            new_wish.setCart(new_cart);
            wishlistRepository.save(new_wish);

            // add newly created wish to the list holdable
            cartWishlist.add(new_wish);

            // total payable amount
            Double totalPayableAmount = getTotalPayableAmount(cartWishlist);

            // since a wish is being created assign it to customer
            customer_wishlist.add(new_wish);
            customer_found.setWishlist(customer_wishlist);
            customerRepository.save(customer_found);

            // assign data to cart
            new_cart.setCustomer(customer_found);
            new_cart.setCustomer_name(customer_found.getCustomer_name());
            new_cart.setCustomer_gender(customer_found.getGender());
            new_cart.setCustomer_email(customer_found.getCustomer_email());
            new_cart.setCustomer_mobile(customer_found.getCustomer_mobile());
            new_cart.setTotal_amount(totalPayableAmount);
            new_cart.setWishlist(cartWishlist);

            Cart cart_saved = cartRepository.save(new_cart);
            return cart_saved;
        }

        // if cart contains data
        else {
            List<Wishlist> cart_wishlist = cart.getWishlist();

            new_wish.setProduct_name(productByName.getProduct_name());
            new_wish.setProduct_manufacturer(productByName.getProduct_manufacturer());
            new_wish.setProduct_quantity(createWishlistDto.getProduct_quantity());
            new_wish.setPayable_amount(productByName.getProduct_price() * createWishlistDto.getProduct_quantity());
            new_wish.setCart(cart);
            new_wish.setCustomer(customer_found);
            wishlistRepository.save(new_wish);

            customer_wishlist.add(new_wish);
            cart_wishlist.add(new_wish);

            customer_found.setWishlist(customer_wishlist);
            customerRepository.save(customer_found);

            Double totalPayableAmount = getTotalPayableAmount(cart_wishlist);
            cart.setTotal_amount(totalPayableAmount);
            cart.setWishlist(cart_wishlist);
            Cart updated_cart = cartRepository.save(cart);
            return updated_cart;
        }
    }

    // PUT - Update Cart as per given product's quantity
    public Cart updateCart_changeQuantity(String product_name, Integer quantity) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        Cart cart = customer_found.getCart();

        if(quantity == null) {
            return cart;
        }

        List<Wishlist> customer_wishlist = cart.getWishlist();

        for(Wishlist wishlist : customer_wishlist) {
            if(wishlist.getProduct_name().equals(product_name)) {
                ProductList productByName = productClient.getProductByName(wishlist.getProduct_name());

                if(productByName.getProduct_inStock() > quantity) {
                    wishlist.setProduct_quantity(quantity);
                    wishlist.setPayable_amount(productByName.getProduct_price() * quantity);
                    wishlistRepository.save(wishlist);
                }
                else {
                    throw new RuntimeException("Quantity required isn't available in stock");
                }
            }
        }
        Double totalPayableAmount = getTotalPayableAmount(customer_wishlist);
        cart.setTotal_amount(totalPayableAmount);
        Cart updated_cart = cartRepository.save(cart);
        return updated_cart;
    }

    // PUT - Update cart's delivery address as per given address type
    public Cart updateCart_changeDeliveryAddress(String address_type) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        List<Address> addressList = customer_found.getAddressList();

        Cart cart = customer_found.getCart();

        Address deliveryAddress = cart.getDelivery_address();

        for(Address address : addressList) {
            if(address.getAddressType() == AddressType.valueOf(address_type)) {
                deliveryAddress = address;
            }
        }

        cart.setDelivery_address(deliveryAddress);
        Cart updated_cart = cartRepository.save(cart);
        return updated_cart;
    }

    // PUT - Cart, Update Mode of Payment as provided
    public Cart updateCart_modeOfPayment(String payment_type) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        Cart cart = customer_found.getCart();
        cart.setModeOfPayment(payment_type);

        Cart updated_cart = cartRepository.save(cart);
        return updated_cart;
    }

    // PUT - Cart, DELETE : Wishlist, Remove a product from cart, recalculate total amount
    public Cart updateCart_removeProduct(String product_name) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        Wishlist wish = new Wishlist();
        wish.setProduct_name(product_name);
        Example<Wishlist> wishlistExample = Example.of(wish);
        Wishlist wish_to_remove = wishlistRepository.findOne(wishlistExample).orElseThrow(() -> new RuntimeException("Product Not Found!"));
        wishlistRepository.delete(wish_to_remove);

        Cart cart = customer_found.getCart();
        List<Wishlist> cartWishlist = cart.getWishlist();
        cartWishlist.remove(wish_to_remove);

        Double totalPayableAmount = getTotalPayableAmount(cartWishlist);
        cart.setTotal_amount(totalPayableAmount);
        cart.setWishlist(cartWishlist);
        Cart cart_updated = cartRepository.save(cart);
        return cart_updated;
    }

    // POST - Create new entry for BuyLater as per given payload
    public BuyLater addBuyLater_newProduct(CreateWishlistDto createWishlistDto) throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        ProductList productByName = productClient.getProductByName(createWishlistDto.getProduct_name());

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        List<BuyLater> customer_buyLaterList = customer_found.getBuyLaterList();

        BuyLater buyLater = new BuyLater();
        buyLater.setBuylater_product_name(createWishlistDto.getProduct_name());
        buyLater.setBuylater_product_manufacturer(createWishlistDto.getProduct_manufacturer());
        buyLater.setBuylater_product_quantity(createWishlistDto.getProduct_quantity());
        buyLater.setBuylater_payable_amount(productByName.getProduct_price() * createWishlistDto.getProduct_quantity());
        buyLater.setCustomer(customer_found);
        BuyLater new_buyLater = buyLaterRepository.save(buyLater);

        // if customer's buylater list isn't created earlier
        if(customer_buyLaterList == null) {
            List<BuyLater> new_buyLaterList = new ArrayList<>();
            new_buyLaterList.add(buyLater);

            customer_found.setBuyLaterList(new_buyLaterList);
            customerRepository.save(customer_found);
        }
        // if customer's buylater list already created and have values
        else {
            customer_buyLaterList.add(buyLater);
            customer_found.setBuyLaterList(customer_buyLaterList);
            customerRepository.save(customer_found);
        }
        return new_buyLater;
    }

    private Double getTotalPayableAmount(List<Wishlist> foundCustomerWishlist) {
        Double totalPaybleAmount = 0.0;

        for(Wishlist wishlist : foundCustomerWishlist) {
            totalPaybleAmount += wishlist.getPayable_amount();
            System.out.println("468: Customer Service:: " + totalPaybleAmount);
        }


        return totalPaybleAmount;
    }

    private Cart createNewCart() {
        Cart cart = new Cart();
        List<Wishlist> wishlist = new ArrayList<>();
        cart.setWishlist(wishlist);

        Cart new_cart = cartRepository.save(cart);
        return new_cart;
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

    // Development Phase - Add BuyLater items to Cart for Purchase, recalculate amount
    // PUT - Cart, ADD : BuyLater, Add Buylater items to cart, recalculate total amount
    public Cart updateCart_addBuyLater() throws CustomerLoginException {
        if(userEmail == null) {
            throw new CustomerLoginException("Customer Not Logged In");
        }

        Customer customer = new Customer();
        customer.setCustomer_email(userEmail);

        Example<Customer> customerExample = Example.of(customer);
        Customer customer_found = customerRepository.findOne(customerExample).orElseThrow(() -> new RuntimeException("Customer Not Found"));

        List<BuyLater> buyLaterList = customer_found.getBuyLaterList();
        Cart cart = customer_found.getCart();

        // check if cart exists by earlier transactions, like adding wishlist / buylater items
        if(cart == null) {
            log.info("Cart is null, resuming from here!");

            // a new cart is required to hold all the buylater items
            Cart newCart = createNewCart();

            List<Wishlist> wishlist = newCart.getWishlist();

            for(BuyLater buyLater : buyLaterList) {
                Wishlist buyLaterToWish = new Wishlist();
                buyLaterToWish.setProduct_name(buyLater.getBuylater_product_name());
                buyLaterToWish.setProduct_quantity(buyLater.getBuylater_product_quantity());
                buyLaterToWish.setProduct_manufacturer(buyLater.getBuylater_product_manufacturer());
                buyLaterToWish.setPayable_amount(buyLater.getBuylater_payable_amount() * buyLater.getBuylater_product_quantity());
                buyLaterToWish.setCart(newCart);
                buyLaterToWish.setCustomer(customer_found);

                wishlist.add(buyLaterToWish);

                wishlistRepository.save(buyLaterToWish);

                buyLaterToWish = null;
            }

            Double totalPayableAmount = getTotalPayableAmount(wishlist);

            newCart.setTotal_amount(totalPayableAmount);
            newCart.setCustomer(customer_found);
            newCart.setCustomer_name(customer_found.getCustomer_name());
            newCart.setCustomer_email(customer_found.getCustomer_email());
            newCart.setCustomer_gender(customer_found.getGender());
            newCart.setCustomer_mobile(customer_found.getCustomer_mobile());
            newCart.setWishlist(wishlist);

            Cart createdCart = cartRepository.save(newCart);
            return createdCart;
        }

        // since cart exists, just add buylayter items as wishes to it.
        else {

            log.info("Cart is isn't null, resuming from here!");

            // on existing cart's wishlist, buylater items are added as wishes
            List<Wishlist> wishlist = cart.getWishlist();

            for (BuyLater buyLater : buyLaterList) {
                Wishlist buyLaterToWishlistItem = new Wishlist();
                buyLaterToWishlistItem.setProduct_name(buyLater.getBuylater_product_name());
                buyLaterToWishlistItem.setProduct_quantity(buyLater.getBuylater_product_quantity());
                buyLaterToWishlistItem.setProduct_manufacturer(buyLater.getBuylater_product_manufacturer());
                buyLaterToWishlistItem.setPayable_amount(buyLater.getBuylater_payable_amount());
                buyLaterToWishlistItem.setCart(cart);
                buyLaterToWishlistItem.setCustomer(customer_found);

                wishlist.add(buyLaterToWishlistItem);

                wishlistRepository.save(buyLaterToWishlistItem);
                buyLaterToWishlistItem = null;
            }

            Double totalPayableAmount = getTotalPayableAmount(wishlist);
            cart.setTotal_amount(totalPayableAmount);

            Cart updated = cartRepository.save(cart);

            return updated;
        }
    }
}
