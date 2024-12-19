package com.checkout.mapper;

import com.checkout.dto.ReceiptDTO;
import com.checkout.model.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    ReceiptDTO toDto(Receipt receipt);


}  