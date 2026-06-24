package com.mapper;

import com.javaBean.Cart;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * CartMapper接口，MyBatis Plus数据访问层。
 */
@Mapper
public interface CartMapper {
    @Select("select * from cart where user_name = #{userName}")
    public List<Cart> getCartByUserName(String userName);
    
    @Select("select * from cart where user_name = #{userName} and good_id = #{goodId}")
    public Cart getCartByUserNameAndGoodId(@Param("userName") String userName, @Param("goodId") String goodId);
    
    @Select("select count(*) from cart where user_name = #{userName}")
    public int getCartCount(String userName);
    
    @Insert("insert into cart (good_id, user_name, intro, amount, price, total_price, cover) values (#{goodId}, #{userName}, #{intro}, #{amount}, #{price}, #{totalPrice}, #{cover})")
    /**
     * 新增数据。
     */
    public int addCart(Cart cart);
    
    @Update("update cart set amount = #{amount}, total_price = #{totalPrice} where id = #{id}")
    /**
     * 更新数据。
     */
    public int updateCart(Cart cart);
    
    @Delete("delete from cart where id = #{id}")
    /**
     * 删除数据。
     */
    public int deleteCart(int id);
    
    @Delete("delete from cart where user_name = #{userName}")
    /**
     * 清空购物车。
     */
    public int clearCart(String userName);
}