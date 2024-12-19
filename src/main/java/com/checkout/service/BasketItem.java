package com.checkout.service;

import com.checkout.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BasketItem {  
    private final Product product;
    private int quantity;  
    private BigDecimal totalPrice;  
    private boolean hasSpecialPrice;  

    public BasketItem(Product product, int quantity) {  
        this.product = product;  
        this.quantity = quantity;  
        recalculatePrice();  
    }  

    public void addQuantity(int additionalQuantity) {  
        if (additionalQuantity <= 0) {  
            throw new IllegalArgumentException("Quantity must be positive");  
        }  
        this.quantity += additionalQuantity;  
        recalculatePrice();  
    }  

    public void setQuantity(int newQuantity) {  
        if (newQuantity < 0) {  
            throw new IllegalArgumentException("Quantity cannot be negative");  
        }  
        this.quantity = newQuantity;  
        recalculatePrice();  
    }  

    private void recalculatePrice() {  

        if (product.getSpecialQuantity() != null && product.getSpecialPrice() != null) {  
            calculatePriceWithPromotion();  
        } else {  
            calculateRegularPrice();  
        }  
    }  

    private void calculatePriceWithPromotion() {  
        int specialSets = quantity / product.getSpecialQuantity();  
        int remainingItems = quantity % product.getSpecialQuantity();  

        BigDecimal promotionalPrice = product.getSpecialPrice()  
                .multiply(BigDecimal.valueOf(specialSets));  
        
        BigDecimal regularPrice = product.getNormalPrice()  
                .multiply(BigDecimal.valueOf(remainingItems));  

        this.totalPrice = promotionalPrice.add(regularPrice);  
        this.hasSpecialPrice = specialSets > 0;  
    }  

    private void calculateRegularPrice() {  
        this.totalPrice = product.getNormalPrice()  
                .multiply(BigDecimal.valueOf(quantity));  
        this.hasSpecialPrice = false;  
    }  

    public BigDecimal getUnitPrice() {  
        return product.getNormalPrice();  
    }  

    @Override  
    public String toString() {  
        return String.format("BasketItem{product=%s, quantity=%d, totalPrice=%s, hasSpecialPrice=%s}",  
                product.getCode(), quantity, totalPrice, hasSpecialPrice);  
    }  
}