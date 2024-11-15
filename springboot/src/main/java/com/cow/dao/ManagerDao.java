package com.cow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cow.entity.Manager;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ManagerDao extends BaseMapper<Manager> {

}
