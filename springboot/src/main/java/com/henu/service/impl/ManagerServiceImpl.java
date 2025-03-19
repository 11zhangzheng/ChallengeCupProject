package com.henu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.dao.ManagerDao;
import com.henu.entity.Manager;
import com.henu.service.ManagerService;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerDao, Manager> implements ManagerService {


}
