package com.mapper;

import com.javaBean.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    @Select("select * from user where isadmin != '2'")
    public List<User> getAllUser();

    @Select("select * from user where isadmin = '2'")
    public List<User> getAllRiders();

    @Select("select * from user where isadmin = '2' limit #{offset}, #{pageSize}")
    public List<User> getRidersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from user where isadmin = '2'")
    public int getRiderCount();
    
    @Select("select * from user limit #{offset}, #{pageSize}")
    public List<User> getUserByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Insert("insert into user (username, password) values (#{username}, #{password})")
    public void addUser(User user);
    
    @Select("select * from user where username = #{username} and password = #{password}")
    public User login(@Param("username") String username, @Param("password") String password);
    
    @Select("select * from user where username = #{username}")
    public User getUserByName(String username);
    
    @Select("select count(*) from user")
    public int getUserCount();
    
    @Select("select * from user where id = #{id}")
    public User getUserById(int id);
    
    @Select("select * from user where email = #{email}")
    public User getUserByEmail(String email);
    
    @Update("update user set username = #{username}, password = #{password}, name = #{name}, phone = #{phone}, address = #{address}, email = #{email}, isadmin = #{isadmin} where id = #{id}")
    public void updateUser(User user);
    
    @Delete("delete from user where id = #{id}")
    public void deleteUser(int id);
    
    @Insert("insert into user (username, password, name, phone, address, email, isadmin) values (#{username}, #{password}, #{name}, #{phone}, #{address}, #{email}, #{isadmin})")
    public void register(@Param("username") String username, @Param("password") String password, 
                        @Param("name") String name, @Param("phone") String phone, 
                        @Param("address") String address, @Param("email") String email, 
                        @Param("isadmin") String isadmin);

    // ===== 审核/冻结/权限 =====
    @Select("select * from user where isvalidate = '0' or isvalidate is null order by id desc limit #{offset}, #{pageSize}")
    public List<User> getUnverifiedUsers(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from user where isvalidate = '0' or isvalidate is null")
    public int getUnverifiedCount();

    @Update("update user set isvalidate = '1' where id = #{id}")
    public void verifyUser(@Param("id") int id);

    @Update("update user set status = 1 where id = #{id}")
    public void freezeUser(@Param("id") int id);

    @Update("update user set status = 0 where id = #{id}")
    public void unfreezeUser(@Param("id") int id);

    @Update("update user set isadmin = #{isadmin} where id = #{id}")
    public void setUserAdmin(@Param("id") int id, @Param("isadmin") String isadmin);

    @Update("update user set balance = balance + #{amount} where id = #{id}")
    void addBalance(@Param("id") int id, @Param("amount") double amount);
}