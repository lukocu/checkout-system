package com.checkout.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class ReceiptNumberGenerator {  
    
    private static final String RECEIPT_PREFIX = "PAR";  
    private final AtomicLong counter = new AtomicLong(1);
    
    public String generateReceiptNumber() {  
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long number = counter.getAndIncrement();  
        
        String receiptNumber = String.format("%s-%s-%06d", RECEIPT_PREFIX, dateStr, number);  
        log.debug("Wygenerowano numer paragonu: {}", receiptNumber);  
        
        return receiptNumber;  
    }  
}