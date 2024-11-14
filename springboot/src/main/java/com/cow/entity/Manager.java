package com.cow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "manager")
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    @Id
    @TableId
    private Integer managerId;
    @TableField("name")
    private String managerName;
    @TableField("phone")
    private String phone;

}
