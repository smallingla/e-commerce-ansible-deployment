package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.AbstractIntegrationTest;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // Create a product for testing
        Product product = new Product("Test Product", new BigDecimal("100.0"), "A test product", 10);
        productRepository.save(product);
        productId = product.getId();

        // Define a test user ID
        userId = 1L;

        // Create a cart for the user
        Cart cart = new Cart();
        cart.setUserId(userId);
        cartRepository.save(cart);

        // Add a CartItem to the cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);  // Ensure this is greater than 0
        cartItemRepository.save(cartItem);

        // Save the cart with the item
        cart.getCartItems().add(cartItem);
        cartRepository.save(cart);
    }

    @Test
    void createOrderForUser_ShouldCreateOrder_WhenCartIsNotEmpty() {
        // Act
        orderService.createOrderForUser(userId);

        // Assert
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());

        Order order = orders.get(0);
        assertEquals(userId, order.getUserId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertEquals(new BigDecimal("200.0"), order.getTotalPrice());
    }

    @Test
    void createOrderForUser_ShouldThrowException_WhenCartIsEmpty() {
        // Arrange: Clear the cart items for the user
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                orderService.createOrderForUser(userId));
        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void updateOrderStatusByOrderId_ShouldUpdateStatus_WhenOrderExists() {
        // Arrange: Create an order for the user
        orderService.createOrderForUser(userId);
        Order order = orderRepository.findAll().get(0);

        // Act: Update the order status to SHIPPED
        orderService.updateOrderStatusByOrderId(order.getId(), OrderStatus.SHIPPED);

        // Assert
        Optional<Order> updatedOrder = orderRepository.findById(order.getId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.SHIPPED, updatedOrder.get().getStatus());
        assertNotNull(updatedOrder.get().getOrderShippedAt());
    }

    @Test
    void updateOrderStatusByOrderId_ShouldThrowException_WhenOrderNotFound() {
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                orderService.updateOrderStatusByOrderId(999L, OrderStatus.SHIPPED));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void updateOrderStatusByOrderId_ShouldThrowException_WhenStatusIsSame() {
        // Arrange: Create an order for the user
        orderService.createOrderForUser(userId);
        Order order = orderRepository.findAll().get(0);

        // Act & Assert: Try to update to the same status
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                orderService.updateOrderStatusByOrderId(order.getId(), OrderStatus.PENDING));
        assertEquals("Order is already pending", exception.getMessage());
    }
}
