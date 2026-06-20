package com.mapper;

import com.javaBean.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("insert into `order` (id, total, amount, status, paytype, name, phone, address, datetime, delivery_time, latitude, longitude, commission, user_id) " +
            "values (#{id}, #{total}, #{amount}, #{status}, #{paytype}, #{name}, #{phone}, #{address}, #{datetime}, #{deliveryTime}, #{latitude}, #{longitude}, #{commission}, #{userId})")
    public void addOrder(Order order);

    @Select("select * from `order` where user_id = #{userId} order by datetime desc")
    public List<Order> getOrdersByUserId(int userId);

    @Select("select * from `order` where id = #{id}")
    public Order getOrderById(String id);

    @Update("update `order` set status = #{status} where id = #{id}")
    public void updateOrderStatus(@Param("id") String id, @Param("status") int status);
    
    @Select("select * from `order` order by datetime desc")
    public List<Order> getAllOrders();
    
    @Select("select * from `order` order by datetime desc limit #{offset}, #{pageSize}")
    public List<Order> getOrdersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select * from `order` where user_id = #{userId} order by datetime desc limit #{offset}, #{pageSize}")
    public List<Order> getOrdersByUserIdPage(@Param("userId") int userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select count(*) from `order`")
    public int getOrderCount();
    
    @Select("select count(*) from `order` where user_id = #{userId}")
    public int getOrderCountByUserId(@Param("userId") int userId);
    
    @Select("select * from `order` where user_id = #{userId} and (id = #{keyword} or name like CONCAT('%', #{keyword}, '%')) order by datetime desc limit #{offset}, #{pageSize}")
    public List<Order> searchOrdersByUserId(@Param("userId") int userId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select count(*) from `order` where user_id = #{userId} and (id = #{keyword} or name like CONCAT('%', #{keyword}, '%'))")
    public int searchOrdersCountByUserId(@Param("userId") int userId, @Param("keyword") String keyword);

    @Select("<script>" +
            "select * from `order` where 1=1" +
            "<if test='status > 0'> and status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> and (id like CONCAT('%', #{keyword}, '%') or name like CONCAT('%', #{keyword}, '%'))</if>" +
            " order by datetime desc limit #{offset}, #{pageSize}" +
            "</script>")
    public List<Order> getFilteredOrders(@Param("status") int status, @Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("<script>" +
            "select count(*) from `order` where 1=1" +
            "<if test='status > 0'> and status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> and (id like CONCAT('%', #{keyword}, '%') or name like CONCAT('%', #{keyword}, '%'))</if>" +
            "</script>")
    public int getFilteredOrdersCount(@Param("status") int status, @Param("keyword") String keyword);

    @Select("select status as `status`, count(*) as `cnt` from `order` group by status order by status")
    public List<java.util.Map<String, Object>> getOrderStatusDistribution();

    // ===== 骑手相关 =====
    @Select("select * from `order` where status = 2 order by datetime desc limit #{offset}, #{pageSize}")
    List<Order> getPendingOrders(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from `order` where status = 2")
    int getPendingCount();

    @Select("select * from `order` where rider_id = #{riderId} and status = 3 order by datetime desc limit #{offset}, #{pageSize}")
    List<Order> getRiderPickupOrders(@Param("riderId") int riderId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from `order` where rider_id = #{riderId} and status = 3")
    int getRiderPickupCount(@Param("riderId") int riderId);

    @Select("select * from `order` where rider_id = #{riderId} and status = 4 order by datetime desc limit #{offset}, #{pageSize}")
    List<Order> getRiderDeliveringOrders(@Param("riderId") int riderId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from `order` where rider_id = #{riderId} and status = 4")
    int getRiderDeliveringCount(@Param("riderId") int riderId);

    @Select("select * from `order` where rider_id = #{riderId} and status = 5 order by datetime desc limit #{offset}, #{pageSize}")
    List<Order> getRiderCompletedOrders(@Param("riderId") int riderId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from `order` where rider_id = #{riderId} and status = 5")
    int getRiderCompletedCount(@Param("riderId") int riderId);

    @Select("select coalesce(sum(rider_income),0) from `order` where rider_id = #{riderId} and status = 5")
    double getRiderTotalIncome(@Param("riderId") int riderId);

    // 今日已完成订单数
    @Select("select count(*) from `order` where rider_id = #{riderId} and status = 5 and date(datetime) = curdate()")
    int getTodayCompletedCount(@Param("riderId") int riderId);

    // 今日配送收入
    @Select("select coalesce(sum(rider_income),0) from `order` where rider_id = #{riderId} and status = 5 and date(datetime) = curdate()")
    double getTodayIncome(@Param("riderId") int riderId);

    // 累计已完成订单数
    @Select("select count(*) from `order` where rider_id = #{riderId} and status = 5")
    int getTotalCompletedCount(@Param("riderId") int riderId);

    // 近7天每日配送收入统计
    @Select("select date(datetime) as `day`, count(*) as `order_count`, coalesce(sum(rider_income),0) as `total_income` " +
            "from `order` where rider_id = #{riderId} and status = 5 " +
            "and datetime >= #{startDate} " +
            "group by date(datetime) order by `day` desc")
    List<java.util.Map<String, Object>> getDailyIncomeLast7Days(@Param("riderId") int riderId, @Param("startDate") String startDate);

    @Update("update `order` set rider_id = #{riderId}, status = 3 where id = #{id}")
    void acceptOrder(@Param("id") String id, @Param("riderId") int riderId);

    @Update("update `order` set status = 4 where id = #{id} and rider_id = #{riderId}")
    void startDelivery(@Param("id") String id, @Param("riderId") int riderId);

    @Update("update `order` set status = 5, rider_income = #{income}, delivery_time = NOW() where id = #{id} and rider_id = #{riderId}")
    void completeDelivery(@Param("id") String id, @Param("riderId") int riderId, @Param("income") double income);

    @Update("update `order` set commission = #{commission} where id = #{id}")
    void setCommission(@Param("id") String id, @Param("commission") double commission);

    @Update("update `order` set delivery_time = #{deliveryTime} where id = #{id}")
    void setDeliveryTime(@Param("id") String id, @Param("deliveryTime") String deliveryTime);

    @Update("update `order` set review_rating = #{rating}, review_content = #{content} where id = #{id}")
    void setReview(@Param("id") String id, @Param("rating") int rating, @Param("content") String content);

    // ===== 管理后台统计 =====
    @Select("select coalesce(sum(total), 0) from `order` where status = 5")
    double getTotalRevenue();

    @Select("select coalesce(sum(total), 0) from `order` where status = 5 and date(datetime) = curdate()")
    double getTodayRevenue();

    @Select("select count(*) from `order` where date(datetime) = curdate()")
    int getTodayOrderCount();

    @Select("select count(*) from `order` where status = 5")
    int getCompletedOrderCount();

    @Select("select count(*) from `order` where status in (1, 2)")
    int getPendingOrderCount();

    @Select("select coalesce(avg(total), 0) from `order` where status in (2,3,4,5)")
    double getAvgOrderValue();

    @Select("select concat(year(datetime), '-', lpad(month(datetime), 2, '0')) as `month`, " +
            "count(*) as `orders`, coalesce(sum(total), 0) as `revenue` " +
            "from `order` where status >= 2 and datetime >= #{startDate} " +
            "group by concat(year(datetime), '-', lpad(month(datetime), 2, '0')) order by `month`")
    List<java.util.Map<String, Object>> getMonthlySales(@Param("startDate") String startDate);
    List<java.util.Map<String, Object>> getMonthlySales();

    @Select("select date(datetime) as `day`, count(*) as `cnt` from `order` " +
            "where datetime >= #{startDate} " +
            "group by date(datetime) order by `day`")
    List<java.util.Map<String, Object>> getWeeklyOrders(@Param("startDate") String startDate);

    @Select("select date(datetime) as `day`, coalesce(sum(total), 0) as `revenue` from `order` " +
            "where status >= 2 and datetime >= #{startDate} " +
            "group by date(datetime) order by `day`")
    List<java.util.Map<String, Object>> getRevenueLast30Days(@Param("startDate") String startDate);

    @Select("select t.name as `type_name`, coalesce(sum(oi.amount), 0) as `sales` " +
            "from orderitem oi join goods g on oi.goods_id = g.id " +
            "join type t on g.type_id = t.id join `order` o on oi.order_id = o.id " +
            "where o.status >= 2 group by t.id, t.name order by `sales` desc limit 10")
    List<java.util.Map<String, Object>> getCategorySales();

    @Select("select g.id, g.name, g.cover, g.sales from goods g order by g.sales desc limit 10")
    List<java.util.Map<String, Object>> getTopGoods();
}
