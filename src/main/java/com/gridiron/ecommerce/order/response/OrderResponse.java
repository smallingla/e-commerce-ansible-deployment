package com.gridiron.ecommerce.order.response;

import com.gridiron.ecommerce.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderResponse(
        Long orderId,
        Long userId,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,
        int numberOfItems
) {
}
