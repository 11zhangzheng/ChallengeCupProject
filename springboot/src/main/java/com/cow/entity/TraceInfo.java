package com.cow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("trace_info")
public class TraceInfo {
    @TableId("traceCode")
    private String traceCode;

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
    private Integer manager;
}
