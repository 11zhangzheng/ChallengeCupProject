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
@Table(name = "user_role")
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements Serializable {
    @Id
    private Integer userId;
    private Integer roleId;
}
