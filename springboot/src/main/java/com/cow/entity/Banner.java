package com.cow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "banner")
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    private Integer bannerId;
    private String productName;
    private String productUrl;
    private String bannerUrl;
}
