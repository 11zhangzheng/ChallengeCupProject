package com.henu.dao;

import com.henu.entity.Logistics;


public interface LogisticsDao extends BaseDao<Logistics> {
    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 物流信息
     */
    Logistics selectOrderNo(String orderNo);
}
