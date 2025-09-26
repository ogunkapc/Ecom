package com.example.ecom.mapper;

import com.example.ecom.dto.ProductRequestDto;
import com.example.ecom.dto.ProductResponseDto;
import com.example.ecom.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toDto(Product product);

    Product toEntity(ProductRequestDto productRequestDto);

    // update existing product
    void updateProductFromDto(ProductRequestDto dto, @MappingTarget Product product);
}
