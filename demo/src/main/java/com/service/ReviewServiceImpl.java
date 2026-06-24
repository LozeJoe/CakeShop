package com.service;

import com.javaBean.Review;
import com.mapper.ReviewMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * 评论服务实现类，提供评论提交、审核等业务逻辑实现。
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public List<Review> getReviewsByGoodsId(int goodsId) {
        return reviewMapper.getReviewsByGoodsId(goodsId);
    }

    @Override
    public int getReviewCountByGoodsId(int goodsId) {
        return reviewMapper.getReviewCountByGoodsId(goodsId);
    }

    /**
     * 新增数据。
     */
    @Override
    public void addReview(Review review) {
        reviewMapper.addReview(review);
    }

    @Override
    public Review getReviewById(int id) { return reviewMapper.getReviewById(id); }

    /**
     * 删除数据。
     */
    @Override
    public void deleteReview(int id) {
        reviewMapper.deleteReview(id);
    }

    @Override
    public Double getAvgRating(int goodsId) {
        return reviewMapper.getAvgRating(goodsId);
    }
}
