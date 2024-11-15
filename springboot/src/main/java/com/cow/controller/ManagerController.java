package com.cow.controller;

import com.cow.dao.ManagerDao;
import com.cow.entity.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class ManagerController {
    @Autowired
    private ManagerDao ManagerDao;

    public Manager getManagerById(Integer id) {
        Manager manager = ManagerDao.selectById(id);
        if (manager != null) {
            return manager;
        } else {
            System.out.println("manager 为空！");
        }
        return null;
    }
}
