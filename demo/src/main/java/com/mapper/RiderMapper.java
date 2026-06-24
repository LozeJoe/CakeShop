package com.mapper;

import com.javaBean.Rider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * RiderMapper接口，MyBatis Plus数据访问层。
 */
@Mapper
public interface RiderMapper {

    @Select("select * from rider where username = #{username} and password = #{password}")
    Rider login(@Param("username") String username, @Param("password") String password);

    @Select("select * from rider where id = #{id}")
    Rider getById(@Param("id") int id);

    @Select("select * from rider")
    List<Rider> getAll();

    @Update("update rider set name=#{name}, phone=#{phone}, avatar=#{avatar} where id=#{id}")
    void updateProfile(Rider rider);

    @Update("update rider set total_orders = total_orders + 1, total_income = total_income + #{income} where id = #{id}")
    void addOrderStats(@Param("id") int id, @Param("income") double income);

    @Select("select count(*) from rider")
    int getCount();
}
