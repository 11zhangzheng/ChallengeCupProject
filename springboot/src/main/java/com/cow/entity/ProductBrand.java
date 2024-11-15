package com.cow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@Table(name = "product_brand")
@NoArgsConstructor
@AllArgsConstructor
public class ProductBrand implements Serializable {
    @Id
    private Integer brandId;
    private String brandName;
    private String brandDescribe;
}
