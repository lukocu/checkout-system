package com.checkout.exception;  

import lombok.Getter;  

@Getter  
public abstract class BusinessException extends RuntimeException {  
    private final String code;  
    
    protected BusinessException(String message, String code) {  
        super(message);  
        this.code = code;  
    }  
    
    protected BusinessException(String message, String code, Throwable cause) {  
        super(message, cause);  
        this.code = code;  
    }  
}