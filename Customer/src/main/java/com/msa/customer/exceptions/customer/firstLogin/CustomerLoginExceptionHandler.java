package com.msa.customer.exceptions.customer.firstLogin;

import com.msa.customer.exceptions.ExceptionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerLoginExceptionHandler {
    @ExceptionHandler(value = CustomerLoginException.class)
    public ResponseEntity<ExceptionInformation> handleCustomerLoginException(CustomerLoginException customerLoginException) {
        ExceptionInformation exceptionInformation = new ExceptionInformation();
        exceptionInformation.setMessage(customerLoginException.getMessage());
        exceptionInformation.setCode("CS-LOGGED_IN-FALSE");

        return new ResponseEntity<>(exceptionInformation, HttpStatus.BAD_REQUEST);
    }
}
