package com.checkout.exception;  

public class ProductAlreadyExistsException extends RuntimeException {  
    public ProductAlreadyExistsException(String code) {  
        super(String.format("Produkt o kodzie '%s' już istnieje w systemie", code));  
    }  
}