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
@TableName("manager")
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    @TableId
    private Integer managerId;
    @TableField("name")
    private String managerName;
    @TableField("phone")
    private String phone;

}
