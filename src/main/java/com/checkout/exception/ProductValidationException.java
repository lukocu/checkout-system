package com.checkout.exception;

public class ProductValidationException extends BusinessException {
    public ProductValidationException(String message) {
        super(message, "PRODUCT_VALIDATION_ERROR");
    }
}