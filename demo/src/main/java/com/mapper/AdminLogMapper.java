package com.mapper;

import com.javaBean.AdminLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminLogMapper {

    @Insert("insert into admin_log (admin_name, action, target, ip, create_time) values (#{adminName}, #{action}, #{target}, #{ip}, NOW())")
    void addLog(AdminLog log);

    @Select("select * from admin_log order by create_time desc limit #{offset}, #{pageSize}")
    List<AdminLog> getLogsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from admin_log")
    int getLogCount();

    @Delete("delete from admin_log where create_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteOldLogs(@Param("days") int days);
}
