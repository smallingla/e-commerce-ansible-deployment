package com.gridiron.ecommerce.product;

import com.gridiron.ecommerce.utility.GeneralEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends GeneralEntity {

    @Column(nullable = false)
    @NotBlank(message = "name cannot be blank")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "price cannot be blank")
    private BigDecimal price;

    @Column(nullable = false)
    @NotBlank(message = "description cannot be blank")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Quantity cannot be blank")
    private int availabilityQuantity;
}
