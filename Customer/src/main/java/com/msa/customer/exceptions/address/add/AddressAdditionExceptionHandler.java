package com.msa.customer.exceptions.address.add;

import com.msa.customer.exceptions.ExceptionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AddressAdditionExceptionHandler {
    @ExceptionHandler(value = AddressAdditionException.class)
    public ResponseEntity<ExceptionInformation> handleAddressAdditionException(AddressAdditionException addressAdditionException) {
        ExceptionInformation exceptionInformation = new ExceptionInformation();
        exceptionInformation.setMessage(addressAdditionException.getMessage());
        exceptionInformation.setCode("CS-ADDRESS-INVALID-TOOMANY");

        return new ResponseEntity<>(exceptionInformation, HttpStatus.BAD_REQUEST);
    }
}
