package com.example.ecom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ecom.dto.ProductRequestDto;
import com.example.ecom.dto.ProductResponseDto;
import com.example.ecom.mapper.ProductMapper;
import com.example.ecom.model.Product;
import com.example.ecom.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Cloudinary cloudinary;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            Cloudinary cloudinary
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.cloudinary = cloudinary;
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
        return productRepository.findById(id)
                .map(productMapper :: toDto)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    //! Add product
    @Transactional
    public Product addProduct(ProductRequestDto productRequestDto, MultipartFile imageFile){
        Product product = productMapper.toEntity(productRequestDto);

        // Upload image to Cloudinary
        try {
            var uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    ObjectUtils.asMap("folder", "products"));
            product.setImageUrl((String) uploadResult.get("secure_url"));
            product.setImagePublicId((String) uploadResult.get("public_id"));
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }

        return productRepository.save(product);
    }

    //! Update a product
    @Transactional
    public Product updateProduct(int id, ProductRequestDto productRequestDto, MultipartFile imageFile) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Update product fields from DTO
        productMapper.updateProductFromDto(productRequestDto, product);

        // If a new image is provided, upload and replace the old one
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image from Cloudinary
                if (product.getImagePublicId() != null) {
                    try {
                        cloudinary.uploader().destroy(product.getImagePublicId(), ObjectUtils.emptyMap());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Upload image to Cloudinary
                var uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "products"));
                product.setImageUrl((String) uploadResult.get("secure_url"));
                product.setImagePublicId((String) uploadResult.get("public_id"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload new image: " + e.getMessage());
        }
        return productRepository.save(product);
    }

    //! Delete a product
    @Transactional
    public void deleteProduct(int id){
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Delete image from Cloudinary
        if (product.getImagePublicId() != null) {
            try {
                cloudinary.uploader().destroy(product.getImagePublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image: " + e.getMessage());
            }
        }

        productRepository.deleteById(id);
    }

    //! Search by a keyword
    public List<ProductResponseDto> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword)
                .stream()
                .map(productMapper :: toDto)
                .toList();
    }
}
