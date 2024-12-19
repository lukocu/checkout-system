package com.checkout.mapper;  

import com.checkout.dto.ProductDTO;
import com.checkout.dto.request.CreateProductRequest;
import com.checkout.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;  
import org.mapstruct.MappingTarget;  

@Mapper(componentModel = "spring")  
public interface ProductMapper {

    @Mapping(source = "code", target = "code")
    @Mapping(source = "normalPrice", target = "normalPrice")
    @Mapping(source = "specialQuantity", target = "specialQuantity")
    @Mapping(source = "specialPrice", target = "specialPrice")
    @Mapping(source = "description", target = "description")
    ProductDTO toDto(Product entity);

    Product toEntity(ProductDTO dto);

    default Product toEntity(CreateProductRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setCode(request.getCode());
        product.setNormalPrice(request.getNormalPrice());
        product.setSpecialQuantity(request.getSpecialQuantity());
        product.setSpecialPrice(request.getSpecialPrice());
        product.setDescription(request.getDescription());

        return product;
    }
}