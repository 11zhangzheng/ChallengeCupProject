package com.cow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "product_specs")
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecs {
    @Id
    private Integer productId;
    private Integer specsId;
}
