package com.mapper;

import com.javaBean.RiderChat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RiderChatMapper {

    @Insert("insert into rider_chat (order_id, sender, sender_name, content) values (#{orderId}, #{sender}, #{senderName}, #{content})")
    void addMessage(RiderChat chat);

    @Select("select * from rider_chat where order_id = #{orderId} order by create_time asc")
    List<RiderChat> getMessagesByOrderId(@Param("orderId") String orderId);

    /** 骑手获取自己配送订单的最新聊天（每个订单最新一条） */
    @Select("select rc.* from rider_chat rc " +
            "join `order` o on rc.order_id = o.id " +
            "where o.rider_id = #{riderId} and o.status in (3,4) " +
            "order by rc.create_time desc")
    List<RiderChat> getRecentChats(@Param("riderId") int riderId);
}
