package com.cow.dao;

import com.cow.entity.Vip;

public interface VipDao extends BaseDao<Vip> {
    /**
     * 查询会员是否存在
     * @param accountNumber 用户账号
     * @return 会员信息是否存在
     */
    Boolean existsVip(String accountNumber);
}
