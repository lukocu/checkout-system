package com.checkout.exception;  

public class PromotionValidationException extends BusinessException {  
    public PromotionValidationException(String message) {  
        super(message, "PROMOTION_VALIDATION_ERROR");  
    }  
}