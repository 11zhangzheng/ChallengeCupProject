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
@Table(name = "specs")
@NoArgsConstructor
@AllArgsConstructor
public class Specs implements Serializable {
    @Id
    private Integer specsId;
    private String specsName;
    private String productType;
}
