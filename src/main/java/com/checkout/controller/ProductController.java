package com.checkout.controller;

import com.checkout.dto.ProductDTO;
import com.checkout.dto.request.CreateProductRequest;
import com.checkout.dto.response.ErrorResponse;
import com.checkout.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;  

@RestController  
@RequestMapping("/api/v1/products")  
@RequiredArgsConstructor  
@Tag(name = "Product Management", description = "API endpoints for product management")  
public class ProductController {  

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Create product",
            description = "Add new product to the system"
    )
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(
            responseCode = "409",
            description = "Product with given code already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {  
        return ResponseEntity.status(201).body(productService.createProduct(request));  
    }  

    @GetMapping  
    @Operation(summary = "Get all products", description = "Retrieve all products in the system")  
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")  
    public ResponseEntity<List<ProductDTO>> getAllProducts() {  
        return ResponseEntity.ok(productService.getAllProducts());  
    }  

    @GetMapping("/{code}")  
    @Operation(summary = "Get product by code", description = "Retrieve specific product by its code")  
    @ApiResponse(responseCode = "200", description = "Product found")  
    @ApiResponse(responseCode = "404", description = "Product not found")  
    public ResponseEntity<ProductDTO> getProductByCode(  
            @PathVariable String code) {  
        return ResponseEntity.ok(productService.getProductByCodeDTO(code));
    }  
}