package com.henu.service.impl;

import com.henu.dao.ProductTypeDao;
import com.henu.entity.ProductType;
import com.henu.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    private ProductTypeDao productTypeDao;

    @Override
    public ProductType selectById(Integer typeId) {
        return productTypeDao.selectById(typeId);
    }

    @Override
    public List<ProductType> selectAll() {
        return productTypeDao.selectAll();
    }

    @Override
    public Boolean existsWithTypeName(Integer typeId, String typeName) {
        return productTypeDao.existsWithTypeName(typeId, typeName);
    }


    @Override
    public List<String> selectAllName() {
        return productTypeDao.selectAllName();
    }

    @Override
    public Boolean insertData(ProductType productType) {
        return productTypeDao.insertData(productType);
    }

    @Override
    public Boolean updateById(ProductType productType) {
        return productTypeDao.updateById(productType);
    }

    @Override
    public Boolean deleteById(Integer typeId) {
        return productTypeDao.deleteById(typeId);
    }

    @Override
    public Boolean deleteByName(String typeName) {
        return productTypeDao.deleteByName(typeName);
    }
}
