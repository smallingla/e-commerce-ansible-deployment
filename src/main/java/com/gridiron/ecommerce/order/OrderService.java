package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cart.CartService;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.orderItem.OrderItem;
import com.gridiron.ecommerce.orderItem.OrderItemRepository;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.user.User;
import com.gridiron.ecommerce.user.UserRepository;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;


    /**
     * This method updates an existing order.
     * @param existingOrder The older to be updated
     */
    void updateOrder(Order existingOrder) {
        existingOrder.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(existingOrder);
    }

    /**
     * This method creates a new order for a user
     * @param userId The userId of the user that owns the order
     */
    @Transactional
    public void createOrderForUser(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        //  Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        //  Create Order Items from Cart Items
        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> createOrderItemFromCartItem(cartItem, order))
                .collect(Collectors.toSet());

        order.setOrderItems(orderItems);

        //  Calculate Total Price
        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        // Save the Order
        orderRepository.save(order);


        // Clear the Cart
       cartService.clearCart(cart);

    }

    /**
     * This method creates an order item from the cart item
     * @param cartItem
     * @param order
     * @return
     */
    private OrderItem createOrderItemFromCartItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return orderItem;
    }

    /**
     * This method changes the status of an order
     * @param orderId the id of the order to be updated
     * @param orderStatus the status to be updated to, PENDING,
     */
    public void updateOrderStatusByOrderId(Long orderId, OrderStatus orderStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if(order.getStatus().equals(orderStatus)) {
            throw new InvalidInputException("Order is already " + orderStatus.name().toLowerCase());
        }

        order.setStatus(orderStatus);

        switch (orderStatus){
            case SHIPPED: order.setOrderShippedAt(LocalDateTime.now());
            case DELIVERED: order.setOrderDeliveredAt(LocalDateTime.now());
            case CANCELED: order.setOrderCanceledAt(LocalDateTime.now());
        }
        updateOrder(order);
    }

}
