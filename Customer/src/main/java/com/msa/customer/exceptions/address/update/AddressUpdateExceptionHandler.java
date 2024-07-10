package com.msa.customer.exceptions.address.update;

import com.msa.customer.exceptions.ExceptionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AddressUpdateExceptionHandler {
    @ExceptionHandler(value = AddressUpdateException.class)
    public ResponseEntity<ExceptionInformation> handleAddressUpdateException(AddressUpdateException addressUpdateException) {
        ExceptionInformation exceptionInformation = new ExceptionInformation();
        exceptionInformation.setMessage(addressUpdateException.getMessage());
        exceptionInformation.setCode("INVALID - ADDRESS TYPE");

        return new ResponseEntity<>(exceptionInformation, HttpStatus.BAD_REQUEST);
    }
}
