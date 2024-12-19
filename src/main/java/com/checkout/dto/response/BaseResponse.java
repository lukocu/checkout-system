package com.checkout.dto.response;  

import com.fasterxml.jackson.annotation.JsonInclude;  
import lombok.Getter;  
import lombok.experimental.SuperBuilder;  

import java.time.LocalDateTime;  

@Getter  
@SuperBuilder  
@JsonInclude(JsonInclude.Include.NON_NULL)  
public abstract class BaseResponse {  
    private final String requestId;  
    private final LocalDateTime timestamp;  
    private final String message;  
}