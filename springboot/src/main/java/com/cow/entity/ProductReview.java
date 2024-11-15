package com.cow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Data
@Entity
@Table(name = "product_review")
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    @Id
    private Integer reviewId;
    private String accountNumber;
    private String productNo;
    private String productSpecs;
    private String orderNo;
    private Date reviewTime;
    private Double starLevel;
    private String productReview;
}
