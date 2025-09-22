package com.example.ecom.service;

import com.example.ecom.dto.ProductRequestDto;
import com.example.ecom.dto.ProductResponseDto;
import com.example.ecom.mapper.ProductMapper;
import com.example.ecom.model.Product;
import com.example.ecom.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private  final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    //! Get all products
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper :: toDto)
                .toList();
    }

    //! Get a product by ID
    public ProductResponseDto getProduct(int id) {
        var product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return productMapper.toDto(product);
        } else {
            return null;
        }
    }

    //! Add product
    public Product addProduct(ProductRequestDto productRequestDto, MultipartFile imageFile) throws IOException {
        Product product = productMapper.toEntity(productRequestDto);
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());

        return productRepository.save(product);
    }

    //! Update a product
    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        product.setImageData(imageFile.getBytes());
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        return productRepository.save(product);
    }

    //! Delete a product
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    //! Search by a keyword
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

}
