package com.controller;

import com.javaBean.Goods;
import com.javaBean.PageResult;
import com.javaBean.Type;
import com.javaBean.User;
import com.service.GoodsService;
import com.service.TypeService;
import com.service.UserService;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {

    @Resource
    private UserService userService;
    
    @Resource
    private GoodsService goodsService;
    
    @Resource
    private TypeService typeService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        // Redirect logged-in users to home page
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            modelAndView.setViewName("redirect:/goods/goodList");
            return modelAndView;
        }
        
        modelAndView.setViewName("login");
        
        // 获取注册成功消息
        String success = request.getParameter("success");
        if (success != null && !success.isEmpty()) {
            modelAndView.addObject("success", success);
        }

        // 获取骑手登录错误消息
        String riderError = request.getParameter("riderError");
        if (riderError != null && !riderError.isEmpty()) {
            modelAndView.addObject("riderError", riderError);
        }
        
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pageSize", defaultValue = "8") int pageSize){
        ModelAndView modelAndView = new ModelAndView();

        try {
            // 获取表单参数
            String userName = request.getParameter("userName");
            String userPassword = request.getParameter("userPassword");

            // Check for empty credentials
            if (userName == null || userName.trim().isEmpty() || userPassword == null || userPassword.trim().isEmpty()) {
                modelAndView.addObject("error", "用户名和密码不能为空");
                modelAndView.setViewName("login");
                return modelAndView;
            }

            // 调用服务层方法验证用户
            User user = userService.login(userName.trim(), userPassword);

            if (user != null) {
                // 检查账户是否被冻结
                if (user.getStatus() == 1) {
                    modelAndView.addObject("error", "账户已被冻结，请联系管理员");
                    modelAndView.setViewName("login");
                    return modelAndView;
                }

                // 登录成功，将用户信息存储到Session中
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // 重定向到商品列表页，让浏览器 URL 变为 /goods/goodList
                modelAndView.setViewName("redirect:/goods/goodList");
            } else {
                // 登录失败，返回登录页面并显示错误信息
                modelAndView.addObject("error", "用户名或密码错误");
                modelAndView.setViewName("login");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/loginout")
    public ModelAndView loginout(HttpServletRequest request) {
        // 清除Session中的用户信息
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        
        // 重定向到登录页面
        return new ModelAndView("redirect:/user/login");
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        // Alias for /loginout — consistent URL naming
        return loginout(request);
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerPage(HttpServletRequest request) {
        // Redirect logged-in users to home page
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/goods/goodList");
        }
        return new ModelAndView("register");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            // 获取表单参数
            String userName = request.getParameter("userName");
            String userPassword = request.getParameter("userPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String email = request.getParameter("email");

            // Validate username
            if (userName == null || userName.trim().isEmpty()) {
                modelAndView.addObject("error", "用户名不能为空");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            userName = userName.trim();
            if (userName.length() < 2 || userName.length() > 50) {
                modelAndView.addObject("error", "用户名长度需在 2-50 之间");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate password (长度至少8位, 含大小写字母+数字+特殊字符)
            if (userPassword == null || userPassword.isEmpty()) {
                modelAndView.addObject("error", "密码不能为空");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            if (userPassword.length() < 8) {
                modelAndView.addObject("error", "密码长度不能少于8位");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            if (!userPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$")) {
                modelAndView.addObject("error", "密码必须包含大写字母、小写字母、数字和特殊字符");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate confirm password
            if (confirmPassword == null || !confirmPassword.equals(userPassword)) {
                modelAndView.addObject("error", "两次输入的密码不一致");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate phone (11位, 1开头)
            if (phone == null || !phone.matches("^1\\d{10}$")) {
                modelAndView.addObject("error", "手机号必须为11位数字且以1开头");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate email
            if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                modelAndView.addObject("error", "请输入有效的邮箱地址");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate address
            if (address == null || address.trim().isEmpty()) {
                modelAndView.addObject("error", "请选择地址");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            address = address.trim();

            // 检查用户名是否已存在
            User existingUser = userService.getUserByName(userName);
            if (existingUser != null) {
                modelAndView.addObject("error", "用户名已存在");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // 检查邮箱是否已存在
            User emailUser = userService.getUserByEmail(email.trim());
            if (emailUser != null) {
                modelAndView.addObject("error", "该邮箱已被注册");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // 注册用户（使用BCrypt加密密码）
            userService.register(userName, userPassword, name, phone.trim(), address, email.trim(), "0");

            // 重定向到登录页面，并显示成功消息
            modelAndView.setViewName("redirect:/user/login?success=注册成功，请登录");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/profile")
    public ModelAndView profile(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            // 重新从数据库获取最新数据
            User freshUser = userService.getUserById(user.getId());
            session.setAttribute("user", freshUser);
            modelAndView.addObject("user", freshUser);
            modelAndView.setViewName("user/profile");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/editProfile")
    public ModelAndView editProfile(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            User freshUser = userService.getUserById(user.getId());
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");

            freshUser.setName(name != null ? name : freshUser.getName());
            freshUser.setPhone(phone != null ? phone : freshUser.getPhone());
            freshUser.setAddress(address != null ? address : freshUser.getAddress());

            // 邮箱唯一性检查
            if (email != null && !email.isEmpty() && (freshUser.getEmail() == null || !email.equals(freshUser.getEmail()))) {
                User emailUser = userService.getUserByEmail(email);
                if (emailUser != null && emailUser.getId() != freshUser.getId()) {
                    modelAndView.addObject("error", "邮箱已被使用");
                    modelAndView.addObject("user", freshUser);
                    modelAndView.setViewName("user/profile");
                    return modelAndView;
                }
                freshUser.setEmail(email);
            }

            // 密码处理：修改密码需要验证旧密码
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (oldPassword == null || oldPassword.isEmpty()) {
                    modelAndView.addObject("error", "请输入当前密码才能修改密码");
                    modelAndView.addObject("user", freshUser);
                    modelAndView.setViewName("user/profile");
                    return modelAndView;
                }
                // 验证旧密码
                if (!userService.matchesPassword(oldPassword, freshUser.getPassword())) {
                    modelAndView.addObject("error", "当前密码错误");
                    modelAndView.addObject("user", freshUser);
                    modelAndView.setViewName("user/profile");
                    return modelAndView;
                }
                // 新密码强度校验
                if (newPassword.length() < 8) {
                    modelAndView.addObject("error", "新密码长度不能少于8位");
                    modelAndView.addObject("user", freshUser);
                    modelAndView.setViewName("user/profile");
                    return modelAndView;
                }
                if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$")) {
                    modelAndView.addObject("error", "新密码必须包含大写字母、小写字母、数字和特殊字符");
                    modelAndView.addObject("user", freshUser);
                    modelAndView.setViewName("user/profile");
                    return modelAndView;
                }
                freshUser.setPassword(userService.encodePassword(newPassword.trim()));
            }

            userService.updateUser(freshUser);
            session.setAttribute("user", freshUser);

            modelAndView.addObject("user", freshUser);
            modelAndView.addObject("success", "个人信息修改成功！");
            modelAndView.setViewName("user/profile");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}