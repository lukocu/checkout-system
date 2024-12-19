package com.checkout.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;  
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;  

@Getter
@Value  
@Builder
@Schema(description = "Single item in basket")  
public class BasketItemResponse {  
    String productCode;
    Integer quantity;  
    BigDecimal unitPrice;  
    BigDecimal totalPrice;  
    Boolean hasSpecialPrice;

    public Boolean isHasSpecialPrice() {
        return hasSpecialPrice;
    }
}