package com.gridiron.ecommerce.product;

import com.gridiron.ecommerce.product.request.CreateProductRequest;
import com.gridiron.ecommerce.product.request.EditProductRequest;
import com.gridiron.ecommerce.utility.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * This endpoint creates a new product
     * It is secured and only accessible by authenticated users with role ADMIN
     * @param createProductRequest json object to create a new product
     */
    @PostMapping("/private")
    private ResponseEntity<ApiResponse> createNewProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {

        return new ResponseEntity<>(new ApiResponse(true, "Product Created Successfully",
                productService.createProduct(createProductRequest)), HttpStatus.CREATED);
    }

    /**
     * This endpoint creates a new product
     * It is not secured and accessible to all users
     */
    @GetMapping("/public")
    private ResponseEntity<ApiResponse> fetchProducts(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") int size
                                                      ) {

        return ResponseEntity.ok(new ApiResponse(true, "Products Fetched Successfully",
                productService.fetchProducts(page,size)));
    }

    /**
     * This endpoint edits a product by the productId
     * It is secured and only accessible by authenticated users with role ADMIN
     * @param editProductRequest json object to edit a new product
     */
    @PutMapping("/private/{product-id}")
    private ResponseEntity<ApiResponse> editProduct(@RequestBody @Valid EditProductRequest editProductRequest,
                                                    @PathVariable("product-id") Long productId) {

        return ResponseEntity.ok(new ApiResponse(true, "Product Edited Successfully",
                productService.editProduct(productId,editProductRequest)));
    }

    /**
     * This endpoint deletes a product by the productId
     * It is secured and only accessible by authenticated users with role ADMIN
     */
    @DeleteMapping("/private/{product-id}")
    private ResponseEntity<ApiResponse> deleteProduct(@PathVariable("product-id") Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.ok(new ApiResponse(true, "Product Deleted Successfully",null));
    }
}
