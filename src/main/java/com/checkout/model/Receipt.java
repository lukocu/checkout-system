package com.checkout.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String receiptNumber;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReceiptProduct> receiptProducts = new ArrayList<>();

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AppliedPromotion> appliedPromotions = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal totalSaved;

    public void addReceiptProduct(ReceiptProduct product) {
        receiptProducts.add(product);
        product.setReceipt(this);
    }

    public void removeReceiptProduct(ReceiptProduct product) {
        receiptProducts.remove(product);
        product.setReceipt(null);
    }

    public void addAppliedPromotion(AppliedPromotion promotion) {
        appliedPromotions.add(promotion);
        promotion.setReceipt(this);
    }

    public void removeAppliedPromotion(AppliedPromotion promotion) {
        appliedPromotions.remove(promotion);
        promotion.setReceipt(null);
    }

}
