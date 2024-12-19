package com.checkout.exception;  

public class ProductNotFoundException extends BusinessException {  
    public ProductNotFoundException(String code) {  
        super(String.format("Product with code '%s' not found", code), "PRODUCT_NOT_FOUND");  
    }  
}