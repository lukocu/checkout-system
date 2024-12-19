package com.checkout.service;

import com.checkout.dto.AppliedPromotionDTO;
import com.checkout.dto.ReceiptDTO;
import com.checkout.dto.request.ScanProductRequest;
import com.checkout.dto.response.BasketItemResponse;
import com.checkout.dto.response.BasketStateResponse;
import com.checkout.exception.ProductNotFoundException;
import com.checkout.mapper.ReceiptMapper;
import com.checkout.model.AppliedPromotion;
import com.checkout.model.Product;
import com.checkout.model.Receipt;
import com.checkout.model.ReceiptProduct;
import com.checkout.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service  
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final ProductService productService;  
    private final BasketService basketService;  
    private final PromotionService promotionService;  
    private final ReceiptRepository receiptRepository;
    private final PriceCalculator priceCalculator;
    private final ReceiptMapper receiptMapper;
    private final ReceiptNumberGenerator receiptNumberGenerator;

    @Transactional  
    public BasketStateResponse scanProduct(ScanProductRequest request) {  
        Product product = productService.getProductByCode(request.getProductCode())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductCode()));  
        
        basketService.addProduct(product, request.getQuantity());  
        
        return calculateCurrentBasketState();  
    }  

    @Transactional(readOnly = true)  
    public BasketStateResponse getBasketState() {  
        return calculateCurrentBasketState();  
    }  

    @Transactional  
    public ReceiptDTO finalizePurchase() {
        if (basketService.isEmpty()) {
            throw new RuntimeException("Nie można sfinalizować zakupu - koszyk jest pusty");
        }

        try {
            var basketState = calculateCurrentBasketState();
            log.debug("Pobrano stan koszyka: {}", basketState);

            Receipt receipt = createReceipt(basketState);
            log.debug("Utworzono paragon: {}", receipt);

            Receipt savedReceipt = receiptRepository.save(receipt);
            log.debug("Zapisano paragon: {}", savedReceipt);

            basketService.clear();
            log.debug("Wyczyszczono koszyk");

            return receiptMapper.toDto(savedReceipt);

        } catch (Exception e) {
            log.error("Błąd podczas finalizacji zakupu. Szczegóły: ", e);
            throw new RuntimeException("Nie udało się sfinalizować zakupu. Koszyk pozostał niezmieniony.");
        }
    }  

    @Transactional  
    public void clearBasket() {  
        basketService.clear();  
    }  

    private BasketStateResponse calculateCurrentBasketState() {  
        var basketItems = basketService.getItems();

        var appliedPromotions = promotionService.calculatePromotions(basketItems);

        List<BasketItemResponse> itemResponses = basketItems.stream()
                .map(this::mapToBasketItemResponse)
                .collect(Collectors.toList());

        return BasketStateResponse.builder()  
                .items(itemResponses)
                .appliedPromotions(appliedPromotions)  
                .currentTotal(calculateTotal(appliedPromotions))
                .totalSaved(calculateSavings(appliedPromotions))  
                .build();  
    }

    private BigDecimal calculateTotal(List<AppliedPromotionDTO> appliedPromotions) {
        BigDecimal regularTotal = basketService.calculateTotal();
        BigDecimal promotionsDiscount = calculateSavings(appliedPromotions);
        return regularTotal.subtract(promotionsDiscount);
    }

    private BigDecimal calculateSavings(List<AppliedPromotionDTO> appliedPromotions) {
        return appliedPromotions.stream()
                .map(AppliedPromotionDTO::getSavedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Receipt createReceipt(BasketStateResponse basketState) {
        Receipt receipt = Receipt.builder()
                .receiptNumber(receiptNumberGenerator.generateReceiptNumber())
                .creationDate(LocalDateTime.now())
                .totalAmount(basketState.getCurrentTotal())
                .totalSaved(basketState.getTotalSaved())
                .build();

        basketState.getItems().forEach(item -> {
            ReceiptProduct product = createReceiptProduct(item);
            receipt.addReceiptProduct(product);
        });

        basketState.getAppliedPromotions().forEach(promotionDTO -> {
            AppliedPromotion promotion = mapPromotion(promotionDTO);
            receipt.addAppliedPromotion(promotion);
        });

        return receipt;
    }


    private ReceiptProduct createReceiptProduct(BasketItemResponse item) {
        Product product = productService.getProductEntityByCode(item.getProductCode());
        if (product == null) {
            throw new ProductNotFoundException("Nie znaleziono produktu o kodzie: " + item.getProductCode());
        }

        return ReceiptProduct.builder()
                .product(product)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .isSpecialPrice(item.isHasSpecialPrice())
                .build();
    }

    private List<AppliedPromotion> mapPromotions(List<AppliedPromotionDTO> promotions) {
        if (promotions == null) {
            return new ArrayList<>();
        }

        return promotions.stream()
                .map(this::mapPromotion)
                .collect(Collectors.toList());
    }

    private AppliedPromotion mapPromotion(AppliedPromotionDTO promotion) {
        return AppliedPromotion.builder()
                .promotionType(promotion.getPromotionType())
                .savedAmount(promotion.getSavedAmount())
                .description(promotion.getDescription())
                .build();
    }


    private BasketItemResponse mapToBasketItemResponse(BasketItem item) {
        return BasketItemResponse.builder()
                .productCode(item.getProduct().getCode())
                .quantity(item.getQuantity())
                .unitPrice(priceCalculator.calculateEffectiveUnitPrice(item))
                .totalPrice(priceCalculator.calculateTotalPrice(item))
                .hasSpecialPrice(priceCalculator.isSpecialPriceApplied(item))
                .build();
    }

}