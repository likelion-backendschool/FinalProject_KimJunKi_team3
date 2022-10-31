package com.ll.finalproject.app.product.mapper;

import com.ll.finalproject.app.product.dto.ProductDto;
import com.ll.finalproject.app.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto entityToProductDto(Product product);

    List<ProductDto> entitiesToProductDtos(List<Product> products);
}
