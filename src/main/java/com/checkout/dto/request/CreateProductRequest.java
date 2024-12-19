package com.checkout.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;  
import jakarta.validation.constraints.DecimalMin;  
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;  

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  
@Schema(description = "Request for creating new product")  
public class CreateProductRequest {  
    @NotBlank  
    @Schema(description = "Product code", example = "A")  
    String code;
    
    @DecimalMin("0.01")  
    @Schema(description = "Normal price", example = "40.00")  
    BigDecimal normalPrice;  
    
    @Schema(description = "Quantity required for special price", example = "3")  
    Integer specialQuantity;  
    
    @Schema(description = "Special price", example = "30.00")  
    BigDecimal specialPrice;  
    
    @Schema(description = "Product description")  
    String description;  
}  