package com.checkout.service;

import com.checkout.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final Map<String, BasketItem> items = new ConcurrentHashMap<>();

    public void addProduct(Product product, int quantity) {
        items.compute(product.getCode(), (key, existingItem) -> {
            if (existingItem == null) {
                return new BasketItem(product, quantity);
            }
            existingItem.addQuantity(quantity);
            return existingItem;
        });
    }

    public BigDecimal calculateTotal() {
        return items.values().stream()
                .map(item -> item.getProduct().getNormalPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<BasketItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}