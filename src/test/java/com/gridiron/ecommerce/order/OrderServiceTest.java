package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cart.CartService;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService; // Added mock for ProductService

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Initialize any required state or configurations if necessary
    }

    @Test
    void createOrderForUser_ShouldCreateOrder_WhenCartIsNotEmpty() {
        // Arrange
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setUserId(userId);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));
        product.setAvailabilityQuantity(10); // Ensure product is available

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.createOrderForUser(userId);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCart(cart);
        verify(productService, times(1)).updateProductQuantityFromOrderItem(any(), eq(true)); // Verify product quantity update call
    }

    @Test
    void createOrderForUser_ShouldThrowException_WhenCartIsEmpty() {
        // Arrange
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setUserId(userId);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrderForUser(userId));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartService, never()).clearCart(cart);
    }

    @Test
    void updateOrderStatusByOrderId_ShouldUpdateStatus_WhenOrderExists() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.updateOrderStatusByOrderId(orderId, OrderStatus.SHIPPED);

        // Assert
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertNotNull(order.getOrderShippedAt());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatusByOrderId_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatusByOrderId(orderId, OrderStatus.SHIPPED));
    }

    @Test
    void updateOrderStatusByOrderId_ShouldThrowException_WhenStatusIsSame() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> orderService.updateOrderStatusByOrderId(orderId, OrderStatus.SHIPPED));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
