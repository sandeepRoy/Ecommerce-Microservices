package com.msa.customer.exceptions.customer.firstLogin;

public class CustomerLoginException extends Exception {
    private String message;

    public CustomerLoginException(String message) {
        super(message);
    }
}
