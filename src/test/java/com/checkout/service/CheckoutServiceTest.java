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
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.extension.ExtendWith;  
import org.mockito.ArgumentCaptor;  
import org.mockito.InjectMocks;  
import org.mockito.Mock;  
import org.mockito.junit.jupiter.MockitoExtension;  
import org.springframework.util.StringUtils;  

import java.math.BigDecimal;  
import java.time.LocalDateTime;  
import java.util.List;  
import java.util.Optional;  

import static org.junit.jupiter.api.Assertions.*;  
import static org.mockito.Mockito.*;  

@ExtendWith(MockitoExtension.class)  
class CheckoutServiceTest {  

    @Mock  
    private ProductService productService;  

    @Mock  
    private BasketService basketService;  

    @Mock  
    private PromotionService promotionService;  

    @Mock  
    private ReceiptRepository receiptRepository;  

    @Mock  
    private PriceCalculator priceCalculator;  

    @Mock  
    private ReceiptMapper receiptMapper;  

    @Mock  
    private ReceiptNumberGenerator receiptNumberGenerator;  

    @InjectMocks  
    private CheckoutService checkoutService;  

    private Product productA;  
    private BasketItemResponse basketItemResponseA;  
    private AppliedPromotionDTO quantityPromotion;  

    @BeforeEach  
    void setUp() {  

        productA = Product.builder()  
                .code("A")  
                .normalPrice(BigDecimal.valueOf(40.00))  
                .specialQuantity(3)  
                .specialPrice(BigDecimal.valueOf(30.00))
                .build();  

        basketItemResponseA = BasketItemResponse.builder()  
                .productCode("A")  
                .quantity(3)  
                .unitPrice(BigDecimal.valueOf(30.00))  
                .totalPrice(BigDecimal.valueOf(90.00))  
                .hasSpecialPrice(true)  
                .build();  

        quantityPromotion = AppliedPromotionDTO.builder()  
                .promotionType("QUANTITY_DISCOUNT")  
                .description("3 x A za 30.00")  
                .savedAmount(BigDecimal.valueOf(30.00))  
                .appliedProductCodes(List.of("A"))  
                .build();  
    }


    @Test
    void testCalculateCurrentBasketState_viaScanProduct() {

        ScanProductRequest request = ScanProductRequest.builder()
                .productCode("A")
                .quantity(3)
                .build();

        when(productService.getProductByCode("A")).thenReturn(Optional.of(productA));
        doNothing().when(basketService).addProduct(productA, 3);

        List<BasketItem> basketItems = List.of(new BasketItem(productA, 3));
        when(basketService.getItems()).thenReturn(basketItems);
        when(promotionService.calculatePromotions(basketItems)).thenReturn(List.of(quantityPromotion));
        when(basketService.calculateTotal()).thenReturn(BigDecimal.valueOf(120.00));

        when(priceCalculator.calculateEffectiveUnitPrice(any())).thenReturn(BigDecimal.valueOf(30.00));
        when(priceCalculator.calculateTotalPrice(any())).thenReturn(BigDecimal.valueOf(90.00));
        when(priceCalculator.isSpecialPriceApplied(any())).thenReturn(true);

        BasketStateResponse basketState = checkoutService.scanProduct(request);

        assertEquals(BigDecimal.valueOf(90.00), basketState.getCurrentTotal());
        assertEquals(BigDecimal.valueOf(30.00), basketState.getTotalSaved());
        assertEquals(1, basketState.getAppliedPromotions().size());
        assertEquals("QUANTITY_DISCOUNT", basketState.getAppliedPromotions().get(0).getPromotionType());

        assertEquals(1, basketState.getItems().size());
        BasketItemResponse itemResponse = basketState.getItems().get(0);
        assertEquals("A", itemResponse.getProductCode());
        assertEquals(3, itemResponse.getQuantity());
        assertEquals(BigDecimal.valueOf(30.00), itemResponse.getUnitPrice());
        assertEquals(BigDecimal.valueOf(90.00), itemResponse.getTotalPrice());
        assertTrue(itemResponse.isHasSpecialPrice());

        verify(basketService, times(1)).addProduct(productA, 3);
    }


    @Test
    void testScanProduct_Success() {

        ScanProductRequest request = new ScanProductRequest("A", 2);

        when(productService.getProductByCode("A")).thenReturn(Optional.of(productA));

        List<BasketItem> updatedBasketItems = List.of(new BasketItem(productA, 2));
        when(basketService.getItems()).thenReturn(updatedBasketItems);
        when(promotionService.calculatePromotions(updatedBasketItems)).thenReturn(List.of());
        when(basketService.calculateTotal()).thenReturn(BigDecimal.valueOf(80.00));

        when(priceCalculator.calculateEffectiveUnitPrice(any())).thenReturn(BigDecimal.valueOf(40.00));
        when(priceCalculator.calculateTotalPrice(any())).thenReturn(BigDecimal.valueOf(80.00));
        when(priceCalculator.isSpecialPriceApplied(any())).thenReturn(false);

        BasketStateResponse response = checkoutService.scanProduct(request);

        assertEquals(BigDecimal.valueOf(80.00), response.getCurrentTotal());
        assertEquals(BigDecimal.ZERO, response.getTotalSaved());
        assertTrue(response.getAppliedPromotions().isEmpty());

        verify(basketService, times(1)).addProduct(productA, 2);
    }

    @Test  
    void testFinalizePurchase_EmptyBasket() {  
        when(basketService.isEmpty()).thenReturn(true);  

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {  
            checkoutService.finalizePurchase();  
        });  

        assertEquals("Nie można sfinalizować zakupu - koszyk jest pusty", exception.getMessage());  
        verify(receiptRepository, never()).save(any(Receipt.class));  
        verify(basketService, never()).clear();  
    }  

    @Test  
    void testScanProduct_InvalidQuantity() {  
        ScanProductRequest request = new ScanProductRequest("A", 0);  

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {  
            checkoutService.scanProduct(request);  
        });  

        assertEquals("Ilość musi być większa od zera", exception.getMessage());  
        verify(basketService, never()).addProduct(any(Product.class), anyInt());  
    }  


    @Test  
    void testScanProduct_ProductNotFound() {  
        ScanProductRequest request = new ScanProductRequest("B", 1);  

        when(productService.getProductByCode("B")).thenReturn(Optional.empty());  

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {  
            checkoutService.scanProduct(request);  
        });  

        assertNotEquals("B", exception.getCode());
        verify(basketService, never()).addProduct(any(Product.class), anyInt());  
    }  
}