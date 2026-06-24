package com.service;

import com.javaBean.PageResult;
import com.javaBean.User;
import java.util.List;


/**
 * 用户服务接口，定义用户注册、登录等业务方法。
 */
public interface UserService {
    List<User> getAllUser();
    PageResult<User> getUserByPage(int pageNum, int pageSize);
    void addUser(User user);
    User login(String username, String password);
    User getUserByName(String username);
    int getUserCount();
    List<User> getAllUsers();
    User getUserById(int id);
    User getUserByEmail(String email);
    void updateUser(User user);
    void deleteUser(int id);
    void register(String username, String password, String name, String phone, String address, String email, String isadmin);
    String encodePassword(String rawPassword);
    boolean matchesPassword(String rawPassword, String encodedPassword);
    PageResult<User> getUnverifiedUsers(int pageNum, int pageSize);
    int getUnverifiedCount();
    PageResult<User> getRidersByPage(int pageNum, int pageSize);
    int getRiderCount();
    void verifyUser(int id);
    void freezeUser(int id);
    void unfreezeUser(int id);
    void setUserAdmin(int id, String isadmin);

}
