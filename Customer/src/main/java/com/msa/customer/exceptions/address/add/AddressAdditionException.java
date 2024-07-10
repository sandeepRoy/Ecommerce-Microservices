package com.msa.customer.exceptions.address.add;

public class AddressAdditionException extends Exception {
    private String message;

    public AddressAdditionException(String message) {
        super(message);
    }
}
