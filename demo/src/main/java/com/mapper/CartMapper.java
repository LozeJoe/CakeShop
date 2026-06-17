package com.mapper;

import com.javaBean.Cart;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CartMapper {
    @Select("select * from cart where user_name = #{userName}")
    public List<Cart> getCartByUserName(String userName);
    
    @Select("select * from cart where user_name = #{userName} and good_id = #{goodId}")
    public Cart getCartByUserNameAndGoodId(@Param("userName") String userName, @Param("goodId") String goodId);
    
    @Select("select count(*) from cart where user_name = #{userName}")
    public int getCartCount(String userName);
    
    @Insert("insert into cart (good_id, user_name, intro, amount, price, total_price, cover) values (#{goodId}, #{userName}, #{intro}, #{amount}, #{price}, #{totalPrice}, #{cover})")
    public int addCart(Cart cart);
    
    @Update("update cart set amount = #{amount}, total_price = #{totalPrice} where id = #{id}")
    public int updateCart(Cart cart);
    
    @Delete("delete from cart where id = #{id}")
    public int deleteCart(int id);
    
    @Delete("delete from cart where user_name = #{userName}")
    public int clearCart(String userName);
}