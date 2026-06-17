package com.service;

import com.javaBean.Review;
import java.util.List;

public interface ReviewService {
    List<Review> getReviewsByGoodsId(int goodsId);
    int getReviewCountByGoodsId(int goodsId);
    void addReview(Review review);
    void deleteReview(int id);
    Double getAvgRating(int goodsId);
    Review getReviewById(int id);
}
