package com.henu.service.impl;

import com.henu.dao.ProductSpecsDao;
import com.henu.entity.ProductSpecs;
import com.henu.service.ProductSpecsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class ProductSpecsServiceImpl implements ProductSpecsService {
    @Autowired
    private ProductSpecsDao productSpecsDao;

    @Override
    public List<ProductSpecs> selectAll() {
        return productSpecsDao.selectAll();
    }

    @Override
    public List<String> selectAllByProId(Integer productId) {
        return productSpecsDao.selectAllByProId(productId);
    }

    @Override
    public Boolean insertData(ProductSpecs productSpecs) {
        return productSpecsDao.insertData(productSpecs);
    }

    @Override
    public Boolean updateById(ProductSpecs productSpecs) {
        return productSpecsDao.updateById(productSpecs);
    }

    @Override
    public Boolean deleteById(ProductSpecs productSpecs) {
        return productSpecsDao.deleteById(productSpecs);
    }

    @Override
    public Boolean insertBatch(List<ProductSpecs> productSpecsList) {
        return productSpecsDao.insertBatch(productSpecsList);
    }
}
