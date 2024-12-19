package com.checkout.controller;

import com.checkout.dto.ReceiptDTO;
import com.checkout.dto.request.ScanProductRequest;
import com.checkout.dto.response.BasketStateResponse;
import com.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;  

@RestController  
@RequestMapping("/api/v1/checkout")
@Tag(name = "Checkout Operations", description = "API endpoints for checkout operations")  
public class CheckoutController {  

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/scan")  
    @Operation(summary = "Scan product", description = "Scan product and add to current basket")  
    @ApiResponse(responseCode = "200", description = "Product successfully scanned")  
    @ApiResponse(responseCode = "404", description = "Product not found")  
    @ApiResponse(responseCode = "400", description = "Invalid request")  
    public ResponseEntity<BasketStateResponse> scanProduct(  
            @Valid @RequestBody ScanProductRequest request) {  
        return ResponseEntity.ok(checkoutService.scanProduct(request));  
    }  

    @GetMapping("/basket")  
    @Operation(summary = "Get basket state", description = "Returns current state of the basket")  
    @ApiResponse(responseCode = "200", description = "Basket state retrieved successfully")  
    public ResponseEntity<BasketStateResponse> getBasketState() {  
        return ResponseEntity.ok(checkoutService.getBasketState());  
    }  

    @PostMapping("/finalize")  
    @Operation(summary = "Finalize purchase", description = "Complete the purchase and generate receipt")  
    @ApiResponse(responseCode = "200", description = "Purchase completed successfully")  
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<ReceiptDTO> finalizePurchase() {
        return ResponseEntity.ok(checkoutService.finalizePurchase());
    }  

    @DeleteMapping("/basket")  
    @Operation(summary = "Clear basket", description = "Remove all items from the basket")  
    @ApiResponse(responseCode = "204", description = "Basket cleared successfully")  
    public ResponseEntity<Void> clearBasket() {  
        checkoutService.clearBasket();  
        return ResponseEntity.noContent().build();  
    }  
}