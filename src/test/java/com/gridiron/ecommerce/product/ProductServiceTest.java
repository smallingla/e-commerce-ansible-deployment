package com.gridiron.ecommerce.product;

import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.orderItem.OrderItem;
import com.gridiron.ecommerce.product.request.CreateProductRequest;
import com.gridiron.ecommerce.product.request.EditProductRequest;
import com.gridiron.ecommerce.product.response.ProductResponse;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Any setup can be done here before each test
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenProductIsCreated() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest("Product A", BigDecimal.valueOf(100), "A test product", 10);
        Product product = new Product("Product A", BigDecimal.valueOf(100), "A test product", 10);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertNotNull(response);
        assertEquals("Product A", response.name());
        assertEquals("A test product", response.description());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void fetchProducts_ShouldReturnPaginatedData_WhenPageIsValid() {
        // Arrange
        Product product = new Product("Product A", BigDecimal.valueOf(100), "A test product", 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        // Act
        PaginatedData result = productService.fetchProducts(1, 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalPage());
        assertEquals(1, result.currentSize());
        assertEquals(1, result.totalSize());
        verify(productRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void fetchProducts_ShouldThrowInvalidInputException_WhenPageIsInvalid() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> productService.fetchProducts(0, 5));
    }

    @Test
    void editProduct_ShouldReturnUpdatedProductResponse_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        EditProductRequest request = new EditProductRequest("Updated Product", BigDecimal.valueOf(150), "Updated description", 15);
        Product existingProduct = new Product("Old Product", BigDecimal.valueOf(100), "Old description", 10);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        ProductResponse response = productService.editProduct(productId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Product", response.name());
        assertEquals("Updated description", response.description());
        assertEquals(BigDecimal.valueOf(150), response.price());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void editProduct_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        EditProductRequest request = new EditProductRequest("Updated Product", BigDecimal.valueOf(150), "Updated description", 15);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.editProduct(productId, request));
    }

    @Test
    void deleteProduct_ShouldDeleteProductAndCartItems_WhenProductExists() {
        // Arrange
        Long productId = 1L;

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
        verify(cartItemRepository, times(1)).deleteByProductId(productId);
    }

    @Test
    void deleteProduct_ShouldThrowResourceNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        doThrow(new ResourceNotFoundException("Product not found")).when(productRepository).deleteById(productId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void updateProductQuantityFromOrderItem_ShouldUpdateQuantities_WhenDeductIsTrue() {
        // Arrange
        OrderItem orderItem = mock(OrderItem.class);
        Product product = new Product("Product A", BigDecimal.valueOf(100), "A test product", 10);
        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getQuantity()).thenReturn(2);

        List<OrderItem> orderItems = List.of(orderItem);

        // Act
        productService.updateProductQuantityFromOrderItem(orderItems, true);

        // Assert
        assertEquals(8, product.getAvailabilityQuantity()); // 10 - 2
        verify(productRepository, times(1)).saveAll(any());
    }

    @Test
    void updateProductQuantityFromOrderItem_ShouldUpdateQuantities_WhenDeductIsFalse() {
        // Arrange
        OrderItem orderItem = mock(OrderItem.class);
        Product product = new Product("Product A", BigDecimal.valueOf(100), "A test product", 10);
        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getQuantity()).thenReturn(2);

        List<OrderItem> orderItems = List.of(orderItem);

        // Act
        productService.updateProductQuantityFromOrderItem(orderItems, false);

        // Assert
        assertEquals(12, product.getAvailabilityQuantity()); // 10 + 2
        verify(productRepository, times(1)).saveAll(any());
    }
}
