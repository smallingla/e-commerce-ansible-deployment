package com.gridiron.ecommerce.orderItem;

import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.product.response.ProductResponse;
import com.gridiron.ecommerce.utility.PaginatedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderItemService orderItemService;

    private Long orderId = 1L;

    @BeforeEach
    void setUp() {
        // Any setup can be done here before each test
    }

    @Test
    void fetchAllOrderItemsByOrderId_ShouldReturnPaginatedData_WhenOrderItemsExist() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(2);
        Product product = new Product("Test Product", BigDecimal.valueOf(100), "A test product", 10);
        orderItem.setProduct(product); // Assume Product class exists

        List<OrderItem> orderItemList = List.of(orderItem);
        Page<OrderItem> orderItemPage = new PageImpl<>(orderItemList);

        when(orderItemRepository.findByOrderId(any(Long.class), any(PageRequest.class))).thenReturn(orderItemPage);
        when(productService.formatProductsToProductResponse(any(Set.class))).thenReturn(List.of(new ProductResponse(product.getId(),product.getName(),product.getDescription(), product.getAvailabilityQuantity(), product.getPrice())));

        // Act
        PaginatedData result = orderItemService.fetchAllOrderItemsByOrderId(orderId, 1, 10);

        // Assert
        assertEquals(1, result.currentSize());
        assertEquals(1, result.totalSize());
        assertEquals(1, result.totalPage());
    }
}