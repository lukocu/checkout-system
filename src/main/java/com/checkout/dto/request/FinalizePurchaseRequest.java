package com.checkout.dto.request;  

import io.swagger.v3.oas.annotations.media.Schema;  
import jakarta.validation.constraints.NotNull;  
import lombok.Builder;  
import lombok.Value;  

@Value  
@Builder  
@Schema(description = "Request for finalizing purchase")  
public class FinalizePurchaseRequest {  
    @NotNull  
    @Schema(description = "Payment method", example = "CARD")  
    PaymentMethod paymentMethod;  
    
    @Schema(description = "Additional notes")  
    String notes;  
}  

