package com.checkout.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipt_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    @ToString.Exclude
    private Receipt receipt;

    @Column(nullable = false)  
    private Integer quantity;  

    @Column(nullable = false)  
    private BigDecimal unitPrice;

    @Column(nullable = false)  
    private BigDecimal totalPrice;  

    @Column(nullable = false)  
    private Boolean isSpecialPrice;  
}