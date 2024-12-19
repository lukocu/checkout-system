package com.checkout.Config;

import com.checkout.mapper.BundlePromotionRepository;
import com.checkout.model.BundlePromotion;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final BundlePromotionRepository bundlePromotionRepository;

    @Override  
    public void run(String... args) {  

        bundlePromotionRepository.save(BundlePromotion.builder()
                .firstProductCode("D")  
                .secondProductCode("E")  
                .discountAmount(BigDecimal.valueOf(11.5))
                .build());  
    }  
}