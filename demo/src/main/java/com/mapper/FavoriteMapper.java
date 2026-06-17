package com.mapper;

import com.javaBean.Favorite;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Select("select * from favorite where user_id = #{userId} order by create_time desc")
    List<Favorite> getFavoritesByUserId(@Param("userId") int userId);

    @Select("select * from favorite where user_id = #{userId} and goods_id = #{goodsId}")
    Favorite getFavorite(@Param("userId") int userId, @Param("goodsId") int goodsId);

    @Select("select count(*) from favorite where user_id = #{userId}")
    int getFavoriteCount(@Param("userId") int userId);

    @Insert("insert into favorite (user_id, goods_id, create_time) values (#{userId}, #{goodsId}, NOW())")
    void addFavorite(Favorite favorite);

    @Delete("delete from favorite where id = #{id}")
    void deleteFavorite(@Param("id") int id);

    @Delete("delete from favorite where user_id = #{userId} and goods_id = #{goodsId}")
    void removeFavorite(@Param("userId") int userId, @Param("goodsId") int goodsId);
}
