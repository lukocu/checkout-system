package com.checkout.service;

import com.checkout.dto.AppliedPromotionDTO;
import com.checkout.model.BundlePromotion;
import com.checkout.model.Product;
import com.checkout.repository.BundlePromotionRepository;
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

        Map<String, BasketItem> itemMap = items.stream()
                .collect(Collectors.toMap(item -> item.getProduct().getCode(), item -> new BasketItem(item)));

        promotions.addAll(calculateBundlePromotionsWithTracking(itemMap));

        for (BasketItem item : itemMap.values()) {
            calculateQuantityPromotion(item).ifPresent(promotions::add);
        }

        return promotions;
    }

    private List<AppliedPromotionDTO> calculateBundlePromotionsWithTracking(Map<String, BasketItem> itemMap) {
        List<AppliedPromotionDTO> promotions = new ArrayList<>();
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
                            .description(String.format("Zestaw %s + %s", bundle.getFirstProductCode(), bundle.getSecondProductCode()))
                            .savedAmount(savedAmount)
                            .appliedProductCodes(List.of(bundle.getFirstProductCode(), bundle.getSecondProductCode()))
                            .build());

                    firstItem.decreaseQuantity(bundleCount);
                    secondItem.decreaseQuantity(bundleCount);
                }
            }
        }

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

        BigDecimal normalTotal = product.getNormalPrice()
                .multiply(BigDecimal.valueOf(product.getSpecialQuantity()))
                .multiply(BigDecimal.valueOf(specialSets));

        BigDecimal promotionalTotal = product.getSpecialPrice()
                .multiply(BigDecimal.valueOf(specialSets));

        BigDecimal savedAmount = normalTotal.subtract(promotionalTotal);

        return Optional.of(AppliedPromotionDTO.builder()
                .promotionType("QUANTITY_DISCOUNT")
                .description(String.format("Kup %d x %s za %s PLN", product.getSpecialQuantity(), product.getCode(), product.getSpecialPrice()))
                .savedAmount(savedAmount)
                .appliedProductCodes(List.of(product.getCode()))
                .build());
    }
}