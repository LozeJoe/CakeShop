package com.service;

import com.javaBean.Review;
import java.util.List;


/**
 * 评论服务接口，定义评论提交、审核等业务方法。
 */
public interface ReviewService {
    List<Review> getReviewsByGoodsId(int goodsId);
    int getReviewCountByGoodsId(int goodsId);
    void addReview(Review review);
    void deleteReview(int id);
    Double getAvgRating(int goodsId);
    Review getReviewById(int id);
}
