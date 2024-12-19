package com.checkout;

import com.checkout.model.BundlePromotion;
import com.checkout.model.Product;
import com.checkout.repository.BundlePromotionRepository;
import com.checkout.repository.ProductRepository;
import com.checkout.repository.ReceiptProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckoutComponentAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReceiptProductRepository productReceiptRepository;

    @Autowired
    private BundlePromotionRepository bundlePromotionRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productReceiptRepository.deleteAll();

        productRepository.deleteAll();
        productReceiptRepository.deleteAll();

        productRepository.save(Product.builder()
                .code("A")
                .normalPrice(BigDecimal.valueOf(40))
                .specialQuantity(3)
                .specialPrice(BigDecimal.valueOf(30))
                .description("Product A")
                .build());

        productRepository.save(Product.builder()
                .code("B")
                .normalPrice(BigDecimal.valueOf(20))
                .specialQuantity(2)
                .specialPrice(BigDecimal.valueOf(15))
                .description("Product B")
                .build());

        bundlePromotionRepository.save(BundlePromotion.builder()
                .firstProductCode("A")
                .secondProductCode("B")
                .discountAmount(BigDecimal.valueOf(5))
                .build());
    }

    @Test
    @DisplayName("Should handle complete checkout flow")
    void shouldHandleCompleteCheckoutFlow() throws Exception {

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.currentTotal").value(0));

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"A\", \"quantity\": 3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productCode").value("A"))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.currentTotal").value(30.0))
                .andExpect(jsonPath("$.appliedPromotions[0].promotionType").value("QUANTITY_DISCOUNT"));

        mockMvc.perform(post("/api/v1/checkout/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(30.0));

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.currentTotal").value(0));
    }

    @Test
    @DisplayName("Should handle product management")
    void shouldHandleProductManagement() throws Exception {

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("A"));


        mockMvc.perform(get("/api/v1/products/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("A"))
                .andExpect(jsonPath("$.normalPrice").value(40.0));


        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"C\", \"normalPrice\": 10, \"specialQuantity\": 2, \"specialPrice\": 7.5, \"description\": \"Product C\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("C"));


        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"A\", \"normalPrice\": 40, \"specialQuantity\": 3, \"specialPrice\": 30, \"description\": \"Product A\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Powinien nie zastosować promocji zestawowej, gdy jeden z produktów jest niedostępny")
    void shouldNotApplyBundlePromotionWhenOneProductMissing() throws Exception {

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"A\", \"quantity\": 2}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"C\", \"quantity\": 1}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedPromotions").isEmpty())
                .andExpect(jsonPath("$.currentTotal").value(80.0)); // 2 * 40 = 80

        mockMvc.perform(post("/api/v1/checkout/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(80.0));
    }

    @Test
    @DisplayName("Should handle error cases")
    void shouldHandleErrorCases() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"X\", \"quantity\": 1}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"A\", \"quantity\": 0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/products/X"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Powinien zastosować wiele promocji zestawowych poprawnie")
    void shouldApplyMultipleBundlePromotionsCorrectly() throws Exception {

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"A\", \"quantity\": 2}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"B\", \"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedPromotions.length()").value(1))
                .andExpect(jsonPath("$.appliedPromotions[0].promotionType").value("BUNDLE_DISCOUNT"))
                .andExpect(jsonPath("$.appliedPromotions[0].savedAmount").value(10.0)) // 2 * 5 PLN = 10 PLN
                .andExpect(jsonPath("$.currentTotal").value(110.0)); // 120 - 10 = 110


        mockMvc.perform(post("/api/v1/checkout/finalize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(110.0));

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.currentTotal").value(0));
    }

    @Test
    @DisplayName("Should handle basket operations")
    void shouldHandleBasketOperations() throws Exception {

        mockMvc.perform(post("/api/v1/checkout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productCode\": \"A\", \"quantity\": 2}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.currentTotal").value(80.0));

        mockMvc.perform(delete("/api/v1/checkout/basket"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/checkout/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.currentTotal").value(0));
    }
}