package com.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@Schema(description = "Applied promotion information")
public  class AppliedPromotionDTO {
    @Schema(description = "Type of promotion", example = "QUANTITY_DISCOUNT")
    @NotNull
    String promotionType;

    @Schema(description = "Promotion description")
    @NotNull
    String description;

    @Schema(description = "Amount saved due to promotion")
    @NotNull
    BigDecimal savedAmount;

    @Schema(description = "List of product codes affected by promotion")
    @Size(min = 1)
    List<String> appliedProductCodes;
}