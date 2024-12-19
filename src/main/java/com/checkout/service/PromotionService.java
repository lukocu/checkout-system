package com.checkout.service;  

import com.checkout.mapper.BundlePromotionRepository;
import com.checkout.model.BundlePromotion;
import com.checkout.model.Product;
import com.checkout.dto.AppliedPromotionDTO;  
import lombok.RequiredArgsConstructor;  
import org.springframework.stereotype.Service;  

import java.math.BigDecimal;  
import java.util.ArrayList;  
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  
@RequiredArgsConstructor  
public class PromotionService {

    private final BundlePromotionRepository bundlePromotionRepository;

    public List<AppliedPromotionDTO> calculatePromotions(List<BasketItem> items) {  
        List<AppliedPromotionDTO> promotions = new ArrayList<>();  

        for (BasketItem item : items) {  
            calculateQuantityPromotion(item).ifPresent(promotions::add);  
        }

        promotions.addAll(calculateBundlePromotions(items));
        
        return promotions;  
    }  

    private Optional<AppliedPromotionDTO> calculateQuantityPromotion(BasketItem item) {
        Product product = item.getProduct();  
        if (product.getSpecialQuantity() == null || product.getSpecialPrice() == null) {  
            return Optional.empty();  
        }  

        int specialSets = item.getQuantity() / product.getSpecialQuantity();  
        if (specialSets == 0) {  
            return Optional.empty();  
        }  

        BigDecimal normalPrice = product.getNormalPrice()  
                .multiply(BigDecimal.valueOf((long) specialSets * product.getSpecialQuantity()));
        BigDecimal promotionalPrice = product.getSpecialPrice()  
                .multiply(BigDecimal.valueOf(specialSets));  
        BigDecimal savedAmount = normalPrice.subtract(promotionalPrice);  

        return Optional.of(AppliedPromotionDTO.builder()  
                .promotionType("QUANTITY_DISCOUNT")  
                .description(String.format("%d x %s za %s",   
                    product.getSpecialQuantity(),   
                    product.getCode(),   
                    product.getSpecialPrice()))  
                .savedAmount(savedAmount)  
                .appliedProductCodes(List.of(product.getCode()))  
                .build());  
    }

    private List<AppliedPromotionDTO> calculateBundlePromotions(List<BasketItem> items) {
        List<AppliedPromotionDTO> promotions = new ArrayList<>();
        Map<String, BasketItem> itemMap = items.stream()
                .collect(Collectors.toMap(item -> item.getProduct().getCode(), item -> item));

        List<BundlePromotion> bundlePromotions = bundlePromotionRepository.findAll();

        for (BundlePromotion bundle : bundlePromotions) {
            BasketItem firstItem = itemMap.get(bundle.getFirstProductCode());
            BasketItem secondItem = itemMap.get(bundle.getSecondProductCode());

            if (firstItem != null && secondItem != null) {
                int bundleCount = Math.min(firstItem.getQuantity(), secondItem.getQuantity());
                if (bundleCount > 0) {
                    BigDecimal savedAmount = bundle.getDiscountAmount()
                            .multiply(BigDecimal.valueOf(bundleCount));

                    promotions.add(AppliedPromotionDTO.builder()
                            .promotionType("BUNDLE_DISCOUNT")
                            .description(String.format("Zestaw %s + %s",
                                    bundle.getFirstProductCode(),
                                    bundle.getSecondProductCode()))
                            .savedAmount(savedAmount)
                            .appliedProductCodes(List.of(
                                    bundle.getFirstProductCode(),
                                    bundle.getSecondProductCode()))
                            .build());
                }
            }
        }

        return promotions;
    }
}
