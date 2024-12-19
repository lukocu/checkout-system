package com.checkout.repository;

import com.checkout.model.ReceiptProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;  

@Repository  
public interface ReceiptProductRepository extends JpaRepository<ReceiptProduct, Long> {
    void deleteByProductId(Long productId);  
}