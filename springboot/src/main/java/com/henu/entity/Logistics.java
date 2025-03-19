package com.henu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "logistics")
@NoArgsConstructor
@AllArgsConstructor
public class Logistics {
    @Id
    private Integer logisticId;
    private String orderNo;
    private String sender;
    private String senderTel;
    private String senderAdd;
    private String receiver;
    private String receiverTel;
    private String receiverAdd;
    private String parcelName;
}
