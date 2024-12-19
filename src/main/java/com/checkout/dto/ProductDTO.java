package com.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a product")
public class ProductDTO {

    @Schema(description = "Unique product code", example = "A")
    @NotBlank
    @Size(min = 1, max = 10)
    String code;

    @Schema(description = "Normal product price", example = "40.00")
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal normalPrice;

    @Schema(description = "Number of products required for promotion", example = "3")
    @Min(value = 2)
    Integer specialQuantity;

    @Schema(description = "Promotional price for products in special quantities", example = "30.00")
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal specialPrice;

    @Schema(description = "Additional product description")
    @Size(max = 1000)
    String description;
}