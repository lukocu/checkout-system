package com.checkout.service;

import com.checkout.dto.ProductDTO;
import com.checkout.dto.request.CreateProductRequest;
import com.checkout.exception.ProductAlreadyExistsException;
import com.checkout.exception.ProductNotFoundException;
import com.checkout.mapper.ProductMapper;
import com.checkout.model.Product;
import com.checkout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {

        if (productRepository.existsByCode(request.getCode())) {
            throw new ProductAlreadyExistsException(request.getCode());
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductByCodeDTO(String code) {
        return productRepository.findByCode(code)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(code));
    }

    @Transactional
    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Product getProductEntityByCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
    }

}