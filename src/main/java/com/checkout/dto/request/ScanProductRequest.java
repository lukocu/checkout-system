package com.checkout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;  
import jakarta.validation.constraints.Min;  
import jakarta.validation.constraints.NotBlank;  
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value  
@Builder  
@Schema(description = "Request for scanning product")  
public class ScanProductRequest {  
    @NotBlank  
    @Schema(description = "Product code to scan", example = "A")  
    String productCode;  
    
    @Min(1)  
    @Schema(description = "Quantity to scan", example = "1")  
    Integer quantity;  
}  