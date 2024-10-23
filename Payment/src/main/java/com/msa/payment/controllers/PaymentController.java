package com.msa.payment.controllers;

import com.msa.payment.dtos.PaymentOrderRequest;
import com.msa.payment.entities.PaymentOrder;
import com.msa.payment.services.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    public PaymentService paymentService;

    @GetMapping("/")
    public String init() {
        return "index";
    }

    @PostMapping(value = "/create-order", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PaymentOrder> createPaymentOrder(@RequestBody PaymentOrderRequest paymentOrderRequest) throws RazorpayException {
        PaymentOrder paymentOrder = paymentService.createPaymentOrder(paymentOrderRequest);
        return new ResponseEntity<>(paymentOrder, HttpStatus.CREATED);
    }

    @PostMapping("/handle-payment-callback")
    public String handlePaymentCallback(@RequestParam Map<String, String> responsePayload) {
        paymentService.updateOrder(responsePayload);
        return "success";
    }
}
