package com.cow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cow.dao.ManagerMapper;
import com.cow.entity.Manager;
import com.cow.service.ManagerService;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, Manager> implements ManagerService {


}
