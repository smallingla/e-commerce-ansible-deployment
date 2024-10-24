package com.gridiron.ecommerce.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gridiron.ecommerce.orderItem.OrderItem;
import com.gridiron.ecommerce.utility.GeneralEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends GeneralEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;    // The corresponding Id of the user placing the order

    @Column(nullable = false)
    private BigDecimal totalPrice;  // Total price of the order

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // Status of the order

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<OrderItem> orderItems = new HashSet<>();  // Items in the order

    @Column(name = "shipped_at")
    private LocalDateTime orderShippedAt;   //  The date and time the order was shipped, if it was

    @Column(name = "delivered_at")
    private LocalDateTime orderDeliveredAt; //  The date and time the order was delivered, if it was

    @Column(name = "canceled_at")
    private LocalDateTime orderCanceledAt;  //  The date and time the order was canceled, if it was

}
