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
@Table(name = "trace_info")
@NoArgsConstructor
@AllArgsConstructor
public class TraceInfo {
    @Id
    @TableId
private String TraceCode;
    @TableField("productName")
    private String productName;
    @TableField("productImage")
    private String productImage;
    @TableField("productDate")
    private String productDate;
    @TableField("specification")
    private String specification;
    @TableField("productionAddress")
    private String productionAddress;
    @TableField("manager")
    private Integer managerId;

}
