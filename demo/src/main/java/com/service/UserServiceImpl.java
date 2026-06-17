package com.service;

import com.javaBean.PageResult;
import com.javaBean.User;
import com.mapper.UserMapper;
import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public PageResult<User> getUserByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> data = userMapper.getUserByPage(offset, pageSize);
        int totalCount = userMapper.getUserCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public void addUser(User user) {
        // 对密码进行MD5加密
        user.setPassword(md5(user.getPassword()));
        userMapper.addUser(user);
    }

    @Override
    public User login(String username, String password) {
        // 对密码进行MD5加密后再验证
        return userMapper.login(username, md5(password));
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public int getUserCount() {
        return userMapper.getUserCount();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUser();
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }

    @Override
    public void register(String username, String password, String name, String phone, String address, String email, String isadmin) {
        // 对密码进行MD5加密
        userMapper.register(username, md5(password), name, phone, address, email, isadmin);
    }

    /**
     * MD5加密方法
     * @param input 明文密码
     * @return MD5加密后的密码
     */
    @Override
    public PageResult<User> getUnverifiedUsers(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> data = userMapper.getUnverifiedUsers(offset, pageSize);
        int totalCount = userMapper.getUnverifiedCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public int getUnverifiedCount() { return userMapper.getUnverifiedCount(); }

    @Override
    public PageResult<User> getRidersByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> data = userMapper.getRidersByPage(offset, pageSize);
        int totalCount = userMapper.getRiderCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public void verifyUser(int id) { userMapper.verifyUser(id); }

    @Override
    public void freezeUser(int id) { userMapper.freezeUser(id); }

    @Override
    public void unfreezeUser(int id) { userMapper.unfreezeUser(id); }

    @Override
    public void setUserAdmin(int id, String isadmin) { userMapper.setUserAdmin(id, isadmin); }

    @Override
    public String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}