package com.example.ecom.controller;

import com.example.ecom.model.Product;
import com.example.ecom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //! Get all products
    @Operation(summary = "Get all products", description = "Fetches a list of all products available in the store.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved list of products",
                            content = @Content(schema = @Schema(implementation = Product.class))
                    )
            }
    )
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    //! Get product by ID
    @Operation(summary = "Get product by ID", description = "Fetches a product by its unique ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved product",
                            content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        Product product = productService.getProduct(id);

        if (product != null)
            return new ResponseEntity<>(product, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //! Create a new product
    @Operation(summary = "Create a new product", description = "Adds a new product to the store with an image.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Product created successfully",
                            content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/product/create")
    public ResponseEntity<?> addProduct(
            @RequestPart Product product,
            @RequestPart MultipartFile imageFile
    ) {
        try {
            Product newProduct = productService.addProduct(product, imageFile);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get product image by ID", description = "Fetches the image of a product by its unique ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved product image",
                            content = @Content(mediaType = "image/jpeg")
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    //! Get product image by ID
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable int productId) {
        Product product = productService.getProduct(productId);
        byte[] imageFile = product.getImageData();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType()))
                .body(imageFile);
    }

    //! Update product
    @Operation(summary = "Update product", description = "Updates an existing product's details.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    @PutMapping("/product/{id}/update")
    public ResponseEntity<String> updateProduct(
            @PathVariable int id,
            @RequestPart Product product,
            @RequestPart MultipartFile imageFile
    ) {
        Product updatedProduct = null;
        try {
            updatedProduct = productService.updateProduct(id, product, imageFile);
        } catch (IOException e) {
            return new ResponseEntity<>("Error updating product: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (updatedProduct != null) {
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    //! Delete product
}
