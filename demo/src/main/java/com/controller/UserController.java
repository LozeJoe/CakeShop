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

            // Validate username
            if (userName == null || userName.trim().isEmpty()) {
                modelAndView.addObject("error", "用户名不能为空");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            userName = userName.trim();

            // Validate password
            if (userPassword == null || userPassword.isEmpty()) {
                modelAndView.addObject("error", "密码不能为空");
                modelAndView.setViewName("register");
                return modelAndView;
            }
            if (userPassword.length() < 6) {
                modelAndView.addObject("error", "密码长度不能少于6位");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // Validate confirm password
            if (confirmPassword == null || !confirmPassword.equals(userPassword)) {
                modelAndView.addObject("error", "两次输入的密码不一致");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // 检查用户名是否已存在
            User existingUser = userService.getUserByName(userName);
            if (existingUser != null) {
                modelAndView.addObject("error", "用户名已存在");
                modelAndView.setViewName("register");
                return modelAndView;
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(userName);
            user.setPassword(userPassword);

            // 调用服务层方法添加用户
            userService.addUser(user);

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

            // 密码处理
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                freshUser.setPassword(userService.md5(newPassword.trim()));
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