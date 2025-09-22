package com.example.ecom.mapper;

import com.example.ecom.dto.ProductRequestDto;
import com.example.ecom.dto.ProductResponseDto;
import com.example.ecom.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toDto(Product product);

    Product toEntity(ProductRequestDto productRequestDto);
}
