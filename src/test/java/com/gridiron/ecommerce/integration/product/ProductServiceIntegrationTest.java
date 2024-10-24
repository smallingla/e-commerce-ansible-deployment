package com.gridiron.ecommerce.integration.product;

import com.gridiron.ecommerce.integration.AbstractIntegrationTest;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.product.request.CreateProductRequest;
import com.gridiron.ecommerce.product.request.EditProductRequest;
import com.gridiron.ecommerce.product.response.ProductResponse;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class ProductServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidInput() {
        // Arrange
        CreateProductRequest createRequest = new CreateProductRequest("Test Product", new BigDecimal("100.00"), "A test product", 10);

        // Act
        ProductResponse response = productService.createProduct(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Test Product");
        assertThat(response.price()).isEqualByComparingTo("100.00");
    }

    @Test
    void fetchProducts_ShouldReturnPaginatedData_WhenProductsExist() {
        // Arrange
        CreateProductRequest createRequest1 = new CreateProductRequest("Product 1", new BigDecimal("50.00"), "Description 1", 5);
        CreateProductRequest createRequest2 = new CreateProductRequest("Product 2", new BigDecimal("75.00"), "Description 2", 8);
        productService.createProduct(createRequest1);
        productService.createProduct(createRequest2);

        // Act
        PaginatedData paginatedData = productService.fetchProducts(1, 2);

        // Assert
        assertThat(paginatedData).isNotNull();
        assertThat(paginatedData.totalSize()).isGreaterThan(0);
        assertThat(paginatedData.currentSize()).isEqualTo(2);
    }

    @Test
    void editProduct_ShouldUpdateProduct_WhenProductExists() {
        // Arrange
        CreateProductRequest createRequest = new CreateProductRequest("Old Product", new BigDecimal("30.00"), "Old Description", 5);
        ProductResponse createdProduct = productService.createProduct(createRequest);
        EditProductRequest editRequest = new EditProductRequest("New Product", new BigDecimal("60.00"), "New Description", 15);

        // Act
        ProductResponse updatedProduct = productService.editProduct(createdProduct.productId(), editRequest);

        // Assert
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.name()).isEqualTo("New Product");
        assertThat(updatedProduct.price()).isEqualByComparingTo("60.00");
    }

    @Test
    void deleteProduct_ShouldRemoveProduct_WhenProductExists() {
        // Arrange
        CreateProductRequest createRequest = new CreateProductRequest("Product to Delete", new BigDecimal("20.00"), "Delete Me", 3);
        ProductResponse productResponse = productService.createProduct(createRequest);
        Long productId = productResponse.productId();

        // Act
        productService.deleteProduct(productId);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.editProduct(productId, new EditProductRequest("Test", new BigDecimal("10.00"), "Desc", 1)));
    }
}
