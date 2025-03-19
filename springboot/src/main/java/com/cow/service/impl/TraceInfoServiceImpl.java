package com.cow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cow.dao.ManagerDao;
import com.cow.dao.ProductSpecsDao;
import com.cow.dao.SpecsDao;
import com.cow.dao.TraceInfoDao;
import com.cow.entity.*;
import com.cow.service.ProductService;
import com.cow.service.TraceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraceInfoServiceImpl extends ServiceImpl<TraceInfoDao, TraceInfo> implements TraceInfoService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSpecsDao productSpecsDao;

    @Autowired
    private SpecsDao specsDao;

    @Autowired
    private ManagerDao managerDao;

    @Override
    public boolean save(TraceInfo entity) {
        // 从规格中提取产品编号
        String productNo = entity.getSpecification().split(" ")[0];

        // 根据产品编号查找产品
        Product product = productService.selectByKey(productNo);
        if (product != null) {
            // 获取产品规格信息
            ProductSpecs productSpecs = productSpecsDao.selectByProductId(product.getProductId());
            String specificationName = "";
            if (productSpecs != null) {
                Specs specs = specsDao.selectById(productSpecs.getSpecsId());
                if (specs != null) {
                    specificationName = specs.getSpecsName();
                }
            }

            // 获取负责人信息
            Manager manager = managerDao.selectById(entity.getManager());
            if (manager == null) {
                // 如果找不到管理员，使用默认ID为1的管理员
                manager = managerDao.selectById(1);
            }

            // 使用产品信息更新溯源信息
            entity.setProductName(product.getProductName());
            entity.setProductImage(product.getProductUrl());
            entity.setSpecification(specificationName); // 使用规格名称
            entity.setProductionAddress(product.getProductBrand() != null ?
                    product.getProductBrand() : "默认生产地址");

            // 设置管理员信息
            if (manager != null) {
                entity.setManager(manager.getManagerId());
            }
        } else {
            // 如果找不到产品，设置默认值
            entity.setProductName("未知产品");
            entity.setProductImage("");
            entity.setProductionAddress("默认生产地址");

            // 使用默认管理员
            Manager defaultManager = managerDao.selectById(1);
            if (defaultManager != null) {
                entity.setManager(defaultManager.getManagerId());
            }
        }

        return super.save(entity);
    }
}
