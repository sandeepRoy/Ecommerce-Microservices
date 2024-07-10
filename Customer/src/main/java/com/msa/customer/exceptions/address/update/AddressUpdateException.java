package com.msa.customer.exceptions.address.update;

public class AddressUpdateException extends Exception {
    private String message;

    public AddressUpdateException(String message) {
        super(message);
    }
}
