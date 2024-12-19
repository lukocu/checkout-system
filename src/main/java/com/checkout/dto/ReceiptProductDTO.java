package com.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Receipt product information")
public  class ReceiptProductDTO {
    @Schema(description = "Product code", example = "A")
    @NotNull
    String productCode;

    @Schema(description = "Quantity of products")
    @NotNull
    @Min(1)
    Integer quantity;

    @Schema(description = "Price per unit")
    @NotNull
    BigDecimal unitPrice;

    @Schema(description = "Total price for this product")
    @NotNull
    BigDecimal totalPrice;

    @Schema(description = "Indicates if special price was applied")
    @NotNull
    Boolean isSpecialPrice;
}