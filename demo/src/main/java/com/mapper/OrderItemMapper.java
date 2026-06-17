package com.mapper;

import com.javaBean.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Insert("insert into orderitem (price, amount, goods_id, order_id) " +
            "values (#{price}, #{amount}, #{goodsId}, #{orderId})")
    public void addOrderItem(OrderItem orderItem);

    @Select("select oi.*, g.name as goodsName, g.cover from orderitem oi " +
            "left join goods g on oi.goods_id = g.id where order_id = #{orderId}")
    public List<OrderItem> getOrderItemsByOrderId(String orderId);
}
