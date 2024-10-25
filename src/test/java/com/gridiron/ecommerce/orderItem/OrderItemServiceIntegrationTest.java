package com.gridiron.ecommerce.orderItem;


import com.gridiron.ecommerce.AbstractIntegrationTest;
import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.order.Order;
import com.gridiron.ecommerce.order.OrderRepository;
import com.gridiron.ecommerce.order.OrderStatus;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class OrderItemServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long cartId;
    private Long orderId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // Create a product for testing
        Product product = new Product("Test Product", new BigDecimal("100.0"), "A test product", 10);
        productRepository.save(product);
        productId = product.getId();

        // Create a cart for testing
        Cart cart = new Cart();
        cart.setUserId(1L);
        cartRepository.save(cart);
        cartId = cart.getId();

        // Add a CartItem to the cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItemRepository.save(cartItem);

        // Create an Order for testing
        Order order = new Order();
        order.setUserId(1L);
        order.setTotalPrice(new BigDecimal("200.0")); // Total price should match the CartItem quantity * price
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        order.getOrderItems().add(new OrderItem(order,product,1,product.getPrice())); // Associate OrderItem with Order
        orderRepository.save(order);
        orderId = order.getId(); // Set orderId based on saved order
    }

    @Test
    void fetchAllOrderItemsByOrderId_ShouldReturnPaginatedData_WhenItemsExist() {
        // Act
        PaginatedData paginatedData = orderItemService.fetchAllOrderItemsByOrderId(orderId, 1, 10);

        // Assert
        assertThat(paginatedData).isNotNull();
        assertThat(paginatedData.totalSize()).isGreaterThan(0);
        assertThat(paginatedData.currentSize()).isEqualTo(1);
    }
}
