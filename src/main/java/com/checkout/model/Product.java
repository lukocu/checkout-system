package com.checkout.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A model representing the product in the system")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, unique = true)
    @Schema(description = "Unique product code", example = "A")
    private String code;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    @Schema(description = "Normal product price", example = "40.00")
    private BigDecimal normalPrice;

    @Min(value = 2)
    @Schema(description = "Number of products required for promotion", example = "3")
    private Integer specialQuantity;

    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Promotional price for products in special quantities", example = "30.00")
    private BigDecimal specialPrice;

    @Column(length = 1000)
    @Schema(description = "Additional product description")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptProduct> receiptProducts = new ArrayList<>();
}