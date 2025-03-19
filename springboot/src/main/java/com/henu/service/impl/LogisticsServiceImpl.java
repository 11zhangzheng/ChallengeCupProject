package com.henu.service.impl;

import com.henu.dao.LogisticsDao;
import com.henu.entity.Logistics;
import com.henu.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class LogisticsServiceImpl implements LogisticsService {
    @Autowired
    private LogisticsDao logisticsDao;

    @Override
    public Logistics selectById(Integer logisticId) {
        return logisticsDao.selectById(logisticId);
    }

    @Override
    public List<Logistics> selectAll() {
        return logisticsDao.selectAll();
    }

    @Override
    public Logistics selectOrderNo(String orderNo) {
        return logisticsDao.selectOrderNo(orderNo);
    }

    @Override
    public Boolean insertData(Logistics logistics) {
        return logisticsDao.insertData(logistics);
    }

    @Override
    public Boolean deleteById(Integer logisticId) {
        return logisticsDao.deleteById(logisticId);
    }
}
