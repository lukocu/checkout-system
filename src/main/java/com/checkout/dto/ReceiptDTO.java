package com.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Receipt information")
public class ReceiptDTO {
    @Schema(description = "Unique receipt number")
    @NotNull
    String receiptNumber;

    @Schema(description = "Receipt creation date and time")
    @NotNull
    LocalDateTime creationDate;

    @Schema(description = "List of items on the receipt")
    @NotNull
    @Size(min = 1)
    List<ReceiptProductDTO> receiptProducts;

    @Schema(description = "List of applied promotions")
    List<AppliedPromotionDTO> appliedPromotions;

    @Schema(description = "Total amount of the receipt")
    @NotNull
    BigDecimal totalAmount;
}