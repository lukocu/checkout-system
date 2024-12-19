package com.checkout.dto.response;

import com.checkout.dto.AppliedPromotionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;  
import lombok.Value;  

import java.math.BigDecimal;  
import java.util.List;  

@Value  
@Builder  
@Schema(description = "Current basket state response")  
public class BasketStateResponse {  
    List<BasketItemResponse> items;  
    List<AppliedPromotionDTO> appliedPromotions;
    BigDecimal currentTotal;  
    BigDecimal totalSaved;  
}