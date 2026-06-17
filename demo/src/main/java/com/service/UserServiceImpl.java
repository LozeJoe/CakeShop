package com.service;

import com.javaBean.PageResult;
import com.javaBean.User;
import com.mapper.UserMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        // 使用BCrypt加密密码
        user.setPassword(encodePassword(user.getPassword()));
        userMapper.addUser(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.getUserByName(username);
        if (user == null) {
            return null;
        }
        // 先尝试 BCrypt 验证
        if (matchesPassword(password, user.getPassword())) {
            return user;
        }
        // 兼容旧 MD5 密码：如果 MD5 匹配，则升级为 BCrypt
        String md5Hash = legacyMd5(password);
        if (md5Hash.equals(user.getPassword())) {
            user.setPassword(encodePassword(password));
            userMapper.updateUser(user);
            return user;
        }
        return null;
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
        // 使用BCrypt加密密码
        userMapper.register(username, encodePassword(password), name, phone, address, email, isadmin);
    }

    /**
     * BCrypt 加密密码
     */
    @Override
    public String encodePassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    /**
     * BCrypt 密码验证
     */
    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }

    /**
     * 兼容旧版 MD5（用于密码迁移，不应用于新密码）
     */
    private String legacyMd5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
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
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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

}