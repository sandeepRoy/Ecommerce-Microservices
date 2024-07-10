package com.msa.customer.exceptions.customer.secondLogin;

import com.msa.customer.exceptions.ExceptionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerPreviouslyLoggedInExceptionHandler {
    @ExceptionHandler(value = CustomerPreviouslyLoggedInException.class)
    public ResponseEntity<ExceptionInformation> handleCustomerPreviouslyLoggedInException(CustomerPreviouslyLoggedInException customerPreviouslyLoggedInException) {
        ExceptionInformation exceptionInformation = new ExceptionInformation();
        exceptionInformation.setMessage(customerPreviouslyLoggedInException.getMessage());
        exceptionInformation.setCode("CS-REPEAT-TRUE");

        return new ResponseEntity<>(exceptionInformation, HttpStatus.OK);
    }
}
