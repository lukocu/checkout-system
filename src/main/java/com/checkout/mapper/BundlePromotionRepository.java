package com.checkout.mapper;

import com.checkout.model.BundlePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundlePromotionRepository extends JpaRepository<BundlePromotion, Long> {
    List<BundlePromotion> findAll();
}