package com.mapper;

import com.javaBean.RiderMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RiderMessageMapper {

    @Select("<script>" +
            "select * from rider_message where rider_id = #{riderId}" +
            "<if test='type != null and type != \"all\"'> and type = #{type}</if>" +
            " order by create_time desc limit #{offset}, #{pageSize}" +
            "</script>")
    List<RiderMessage> getMessages(@Param("riderId") int riderId, @Param("type") String type,
                                   @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("<script>" +
            "select count(*) from rider_message where rider_id = #{riderId}" +
            "<if test='type != null and type != \"all\"'> and type = #{type}</if>" +
            "</script>")
    int getMessageCount(@Param("riderId") int riderId, @Param("type") String type);

    @Select("select * from rider_message where id = #{id}")
    RiderMessage getMessageById(@Param("id") int id);

    @Insert("insert into rider_message (rider_id, type, title, content) values (#{riderId}, #{type}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMessage(RiderMessage message);

    @Update("update rider_message set is_read = 1 where id = #{id}")
    void markAsRead(@Param("id") int id);

    @Update("update rider_message set is_read = 1 where rider_id = #{riderId}")
    void markAllAsRead(@Param("riderId") int riderId);

    @Select("select count(*) from rider_message where rider_id = #{riderId} and is_read = 0")
    int getUnreadCount(@Param("riderId") int riderId);
}
