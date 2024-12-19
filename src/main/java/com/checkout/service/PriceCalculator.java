package com.checkout.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PriceCalculator {  
    
    public BigDecimal calculateEffectiveUnitPrice(BasketItem item) {
        if (isSpecialPriceApplied(item)) {  
            return item.getProduct().getSpecialPrice();  
        }  
        return item.getProduct().getNormalPrice();  
    }  
    
    public BigDecimal calculateTotalPrice(BasketItem item) {  
        return calculateEffectiveUnitPrice(item)  
                .multiply(BigDecimal.valueOf(item.getQuantity()));  
    }  
    
    public boolean isSpecialPriceApplied(BasketItem item) {  
        return item.getProduct().getSpecialPrice() != null   
               && item.getProduct().getSpecialQuantity() != null   
               && item.getQuantity() >= item.getProduct().getSpecialQuantity();  
    }  
}