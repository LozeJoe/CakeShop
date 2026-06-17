package com.mapper;

import com.javaBean.Review;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("select * from review where goods_id = #{goodsId} order by create_time desc")
    List<Review> getReviewsByGoodsId(@Param("goodsId") int goodsId);

    @Select("select count(*) from review where goods_id = #{goodsId}")
    int getReviewCountByGoodsId(@Param("goodsId") int goodsId);

    @Insert("insert into review (goods_id, user_id, user_name, content, rating, create_time) " +
            "values (#{goodsId}, #{userId}, #{userName}, #{content}, #{rating}, NOW())")
    void addReview(Review review);

    @Select("select * from review where id = #{id}")
    Review getReviewById(@Param("id") int id);

    @Delete("delete from review where id = #{id}")
    void deleteReview(@Param("id") int id);

    @Select("select avg(rating) from review where goods_id = #{goodsId}")
    Double getAvgRating(@Param("goodsId") int goodsId);
}
