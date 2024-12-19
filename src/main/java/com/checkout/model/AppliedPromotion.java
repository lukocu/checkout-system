package com.checkout.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "applied_promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedPromotion {  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String promotionType; // np. "QUANTITY_DISCOUNT", "BUNDLE_DISCOUNT"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    @ToString.Exclude
    private Receipt receipt;

    @Column
    private String description;  

    @Column(nullable = false)  
    private BigDecimal savedAmount;

    @ElementCollection  
    @CollectionTable(name = "promotion_products",   
        joinColumns = @JoinColumn(name = "applied_promotion_id"))  
    private List<String> appliedProductCodes;
}  