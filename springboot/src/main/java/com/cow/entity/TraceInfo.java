package com.cow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@TableName("trace_info")
@NoArgsConstructor
@AllArgsConstructor
public class TraceInfo {
    @TableId(value = "traceCode")
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
