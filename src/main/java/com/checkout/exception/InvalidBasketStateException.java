package com.checkout.exception;  

public class InvalidBasketStateException extends BusinessException {  
    public InvalidBasketStateException(String message) {  
        super(message, "INVALID_BASKET_STATE");  
    }  
}