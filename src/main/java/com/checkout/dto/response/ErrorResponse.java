package com.checkout.dto.response;  

import io.swagger.v3.oas.annotations.media.Schema;  
import lombok.Builder;  
import lombok.Value;  

import java.time.LocalDateTime;  

@Value  
@Builder  
@Schema(description = "Standard error response")  
public class ErrorResponse {  
    @Schema(description = "Error code", example = "PRODUCT_NOT_FOUND")  
    String code;  
    
    @Schema(description = "Error message", example = "Product with code 'A' not found")  
    String message;  
    
    @Schema(description = "Additional error details")  
    String details;  
    
    @Schema(description = "Timestamp of the error")  
    @Builder.Default  
    LocalDateTime timestamp = LocalDateTime.now();  
    
    @Schema(description = "Path where the error occurred", example = "/api/v1/products/A")  
    String path;  
}  