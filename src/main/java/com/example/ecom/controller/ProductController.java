package com.example.ecom.controller;

import com.example.ecom.dto.ProductRequestDto;
import com.example.ecom.dto.ProductResponseDto;
import com.example.ecom.mapper.ProductMapper;
import com.example.ecom.model.Product;
import com.example.ecom.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    //! Get all products
    @Operation(summary = "Get all products", description = "Fetches a list of all products available in the store.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved list of products",
                            content = @Content(
                                    mediaType =  MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    //! Get product by ID
    @Operation(summary = "Get product by ID", description = "Fetches a product by its unique ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved product",
                            content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable int productId) {
        ProductResponseDto product = productService.getProduct(productId);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //! Create a new product
    @Operation(summary = "Create a new product", description = "Adds a new product to the store with an image.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Product created successfully",
                            content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> addProduct(
            @Parameter(description = "Product JSON as string")
            @RequestPart("product") String productJson,
//            @Parameter(
//                    description = "Product JSON",
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            schema = @Schema(implementation = ProductRequestDto.class)
//                    )
//            )
//            @RequestPart("product") ProductRequestDto productRequestDto,

            @Parameter(description = "Image file")
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequestDto productRequestDto = objectMapper.readValue(productJson, ProductRequestDto.class);

            Product newProduct = productService.addProduct(productRequestDto, imageFile);
            return new ResponseEntity<>(productMapper.toDto(newProduct), HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing product JSON: " + e.getMessage());
        }
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
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable int id,

            @Parameter(
                    description = "Product JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductRequestDto.class)
                    )
            )
            @RequestPart("product") ProductRequestDto productRequestDto,

            @Parameter(description = "Image file (optional)")
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        Product updatedProduct = productService.updateProduct(id, productRequestDto, imageFile);
        return new ResponseEntity<>(productMapper.toDto(updatedProduct), HttpStatus.OK);
    }

    //! Delete product
    @Operation(summary = "Delete product", description = "Deletes a product by its unique ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    //! Search by keyword
    @Operation(summary = "Search products", description = "Searches for products based on a keyword.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved list of products",
                            content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "No products found")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDto> products = productService.searchProducts(keyword);
        return products.isEmpty()
            ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
            : new ResponseEntity<>(products, HttpStatus.OK);
    }
}
