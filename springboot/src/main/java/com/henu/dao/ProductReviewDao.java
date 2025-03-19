package com.henu.dao;

import com.henu.entity.ProductReview;

import java.util.List;
import java.util.Map;


public interface ProductReviewDao extends BaseDao<ProductReview> {
    /**
     * 查询所有
     *
     * @param productNo 商品编号
     * @return 商品评论信息
     */
    List<Map<String, Object>> selectAll(String productNo);

}
