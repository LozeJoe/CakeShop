package com.mapper;

import com.javaBean.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;


/**
 * UserMapper接口，MyBatis Plus数据访问层。
 */
@Mapper
public interface UserMapper {
    @Select("select * from user where isadmin != '2'")
    /**
     * 查询获取数据。
     */
    public List<User> getAllUser();

    @Select("select * from user where isadmin = '2'")
    /**
     * 获取所有骑手列表。
     */
    public List<User> getAllRiders();

    @Select("select * from user where isadmin = '2' limit #{offset}, #{pageSize}")
    /**
     * 分页获取骑手列表。
     */
    public List<User> getRidersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from user where isadmin = '2'")
    /**
     * 查询获取数据。
     */
    public int getRiderCount();
    
    @Select("select * from user limit #{offset}, #{pageSize}")
    /**
     * 分页获取用户列表。
     */
    public List<User> getUserByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Insert("insert into user (username, password) values (#{username}, #{password})")
    /**
     * 新增数据。
     */
    public void addUser(User user);
    
    @Select("select * from user where username = #{username} and password = #{password}")
    /**
     * 处理用户登录请求。
     */
    public User login(@Param("username") String username, @Param("password") String password);
    
    @Select("select * from user where username = #{username}")
    /**
     * 根据用户名查询用户。
     */
    public User getUserByName(String username);
    
    @Select("select count(*) from user")
    /**
     * 查询获取数据。
     */
    public int getUserCount();
    
    @Select("select * from user where id = #{id}")
    /**
     * 根据ID查询用户。
     */
    public User getUserById(int id);
    
    @Select("select * from user where email = #{email}")
    /**
     * 根据邮箱查询用户。
     */
    public User getUserByEmail(String email);
    
    @Update("update user set username = #{username}, password = #{password}, name = #{name}, phone = #{phone}, address = #{address}, email = #{email}, isadmin = #{isadmin} where id = #{id}")
    /**
     * 更新数据。
     */
    public void updateUser(User user);
    
    @Delete("delete from user where id = #{id}")
    /**
     * 删除数据。
     */
    public void deleteUser(int id);
    
    @Insert("insert into user (username, password, name, phone, address, email, isadmin) values (#{username}, #{password}, #{name}, #{phone}, #{address}, #{email}, #{isadmin})")
    /**
     * 处理用户注册请求。
     */
    public void register(@Param("username") String username, @Param("password") String password, 
                        @Param("name") String name, @Param("phone") String phone, 
                        @Param("address") String address, @Param("email") String email, 
                        @Param("isadmin") String isadmin);

    // ===== 审核/冻结/权限 =====
    @Select("select * from user where isvalidate = '0' or isvalidate is null order by id desc limit #{offset}, #{pageSize}")
    /**
     * 分页获取未审核用户列表。
     */
    public List<User> getUnverifiedUsers(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from user where isvalidate = '0' or isvalidate is null")
    /**
     * 查询获取数据。
     */
    public int getUnverifiedCount();

    @Update("update user set isvalidate = '1' where id = #{id}")
    /**
     * 执行对应业务操作。
     */
    public void verifyUser(@Param("id") int id);

    @Update("update user set status = 1 where id = #{id}")
    /**
     * 执行对应业务操作。
     */
    public void freezeUser(@Param("id") int id);

    @Update("update user set status = 0 where id = #{id}")
    /**
     * 执行对应业务操作。
     */
    public void unfreezeUser(@Param("id") int id);

    @Update("update user set isadmin = #{isadmin} where id = #{id}")
    public void setUserAdmin(@Param("id") int id, @Param("isadmin") String isadmin);

    @Update("update user set balance = balance + #{amount} where id = #{id}")
    void addBalance(@Param("id") int id, @Param("amount") double amount);

}
