package com.henu.service.impl;

import com.henu.dao.ReturnReasonDao;
import com.henu.entity.ReturnReason;
import com.henu.service.ReturnReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class ReturnReasonServiceImpl implements ReturnReasonService {
    @Autowired
    private ReturnReasonDao returnReasonDao;

    @Override
    public ReturnReason selectById(Integer reasonId) {
        return returnReasonDao.selectById(reasonId);
    }

    @Override
    public List<ReturnReason> selectAll() {
        return returnReasonDao.selectAll();
    }

    @Override
    public List<String> selectAllName() {
        return returnReasonDao.selectAllName();
    }

    @Override
    public Boolean existsWithReasonName(Integer reasonId, String reasonName) {
        return returnReasonDao.existsWithReasonName(reasonId, reasonName);
    }

    @Override
    public Boolean insertData(ReturnReason returnReason) {
        return returnReasonDao.insertData(returnReason);
    }

    @Override
    public Boolean updateById(ReturnReason returnReason) {
        return returnReasonDao.updateById(returnReason);
    }

    @Override
    public Boolean deleteById(Integer reasonId) {
        return returnReasonDao.deleteById(reasonId);
    }
}
