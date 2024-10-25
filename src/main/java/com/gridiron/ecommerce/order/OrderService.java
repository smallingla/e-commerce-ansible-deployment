package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cart.CartService;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.order.response.OrderResponse;
import com.gridiron.ecommerce.orderItem.OrderItem;
import com.gridiron.ecommerce.orderItem.OrderItemRepository;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.user.User;
import com.gridiron.ecommerce.user.UserRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductService productService;


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
       //update product quantity
        productService.updateProductQuantityFromOrderItem(orderItems,true);

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
     * This method changes the status of an order.
     * The functionality here can be extended comprehensively to meet specific business needs
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
            case SHIPPED-> order.setOrderShippedAt(LocalDateTime.now());
            case DELIVERED-> order.setOrderDeliveredAt(LocalDateTime.now());
            case CANCELED->{
                order.setOrderCanceledAt(LocalDateTime.now());
                productService.updateProductQuantityFromOrderItem(order.getOrderItems(), false);
            }
        }
        updateOrder(order);
    }

    /**
     * Fetches all the orders in the system with pagination
     * @param page The page number to be fetched for
     * @param size The number of orders to be fetched on the page
     */
    public PaginatedData fetchAllOrders(int page, int size){
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page-1, size, Sort.by(Sort.Order.desc("createdAt"))));

        return PaginatedData.builder()
                .totalPage(orders.getTotalPages())
                .totalSize(orders.getTotalElements())
                .currentSize(orders.getNumberOfElements())
                .data(formatOrders(orders.getContent()))
                .build();
    }

    /**
     * Formats a list of orders to OrderResponse object
     * @param orders List of orders
     * @return List of order response
     */
    private List<OrderResponse> formatOrders(Collection<Order> orders) {

        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {

            orderResponses.add(OrderResponse.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .numberOfItems(order.getOrderItems().size())
                    .status(order.getStatus())
                    .totalPrice(order.getTotalPrice())
                    .shippedAt(order.getOrderShippedAt())
                    .createdAt(order.getCreatedAt())
                    .cancelledAt(order.getOrderCanceledAt())
                    .deliveredAt(order.getOrderDeliveredAt())
                    .build()
            );
        }

        return orderResponses;
    }

}
