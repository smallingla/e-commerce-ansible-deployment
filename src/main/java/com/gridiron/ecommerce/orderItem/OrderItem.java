package com.gridiron.ecommerce.orderItem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gridiron.ecommerce.order.Order;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.utility.GeneralEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends GeneralEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;    //Reference to the order this item

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;    // Reference to be product being purchase

    @Column(nullable = false)
    private int quantity;   //Quantity of product being ordered

    @Column(nullable = false)
    private BigDecimal price;  // Price of the product at the time of ordering

}
