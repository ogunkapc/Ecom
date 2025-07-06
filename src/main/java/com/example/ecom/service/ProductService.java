package com.example.ecom.service;

import com.example.ecom.model.Product;
import com.example.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //! Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    //! Get a product by ID
    public Product getProduct(int id) {
        return productRepository.findById(id).orElse(null);
    }

    //! Add product
    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
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
