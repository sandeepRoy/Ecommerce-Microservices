package com.msa.customer.exceptions.customer.secondLogin;

public class CustomerPreviouslyLoggedInException extends Exception {
    private String message;

    public CustomerPreviouslyLoggedInException(String message) {
        super(message);
    }
}
