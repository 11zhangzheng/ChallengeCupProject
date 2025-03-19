package com.cow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "shopping_cart")
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    @Id
    private Integer cartId;
    private String accountNumber;
    private Integer productId;
    private Integer payAmount;
    private String productSpecs;
}
