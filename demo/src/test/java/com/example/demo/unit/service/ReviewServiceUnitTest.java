package com.example.demo.unit.service;

import com.javaBean.Review;
import com.mapper.ReviewMapper;
import com.service.ReviewServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 单元测试")
class ReviewServiceUnitTest {

    @Mock private ReviewMapper reviewMapper;
    @InjectMocks private ReviewServiceImpl reviewService;

    private Review createReview(int id, int goodsId, int rating, String content) {
        Review r = new Review();
        r.setId(id);
        r.setGoodsId(goodsId);
        r.setRating(rating);
        r.setContent(content);
        return r;
    }

    @Nested
    @DisplayName("查询操作")
    class QueryOperations {
        @Test @DisplayName("按商品ID获取评价")
        void getReviewsByGoodsId() {
            when(reviewMapper.getReviewsByGoodsId(1)).thenReturn(
                Arrays.asList(createReview(1, 1, 5, "好吃"), createReview(2, 1, 4, "不错"))
            );
            assertEquals(2, reviewService.getReviewsByGoodsId(1).size());
        }

        @Test @DisplayName("获取评价数量")
        void getReviewCount() {
            when(reviewMapper.getReviewCountByGoodsId(1)).thenReturn(5);
            assertEquals(5, reviewService.getReviewCountByGoodsId(1));
        }

        @Test @DisplayName("计算平均评分")
        void getAvgRating() {
            when(reviewMapper.getAvgRating(1)).thenReturn(4.5);
            assertEquals(4.5, reviewService.getAvgRating(1));
        }

        @Test @DisplayName("无评价时平均评分为null")
        void getAvgRatingWhenNoReviews() {
            when(reviewMapper.getAvgRating(999)).thenReturn(null);
            assertNull(reviewService.getAvgRating(999));
        }

        @Test @DisplayName("按ID获取评价")
        void getReviewById() {
            when(reviewMapper.getReviewById(1)).thenReturn(createReview(1, 1, 5, "好吃"));
            Review r = reviewService.getReviewById(1);
            assertEquals("好吃", r.getContent());
        }
    }

    @Nested
    @DisplayName("增删操作")
    class MutateOperations {
        @Test @DisplayName("添加评价")
        void addReview() {
            Review r = createReview(0, 1, 5, "太好吃了！");
            reviewService.addReview(r);
            verify(reviewMapper).addReview(r);
        }

        @Test @DisplayName("删除评价")
        void deleteReview() {
            reviewService.deleteReview(1);
            verify(reviewMapper).deleteReview(1);
        }
    }
}
