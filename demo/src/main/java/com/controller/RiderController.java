package com.controller;

import com.javaBean.Order;
import com.javaBean.PageResult;
import com.javaBean.RiderMessage;
import com.javaBean.User;
import com.service.OrderService;
import com.mapper.UserMapper;
import com.service.RiderService;
import com.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * 骑手控制器，处理骑手注册、登录、接单、配送及聊天等骑手端相关请求。
 */
@RequestMapping("/rider")
@Controller
public class RiderController {
	// 重定向 /rider -> /rider/index
	@RequestMapping("")
	/**
	 * 执行对应业务操作。
	 */
	public String root() {
		return "redirect:/rider/index";
	}


    @Resource
    private RiderService riderService;

    @Resource
    private OrderService orderService;

    @Resource
    private com.service.RiderChatService riderChatService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @RequestMapping("/login")
    /**
     * 显示登录页面。
     */
    public ModelAndView loginPage() {
        // Redirect to unified login page with rider tab active
        return new ModelAndView("redirect:/user/login?role=rider");
    }

    @RequestMapping("/doLogin")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView doLogin(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 HttpServletRequest request) {
        User rider = riderService.login(username, password);
        if (rider != null) {
            request.getSession().setAttribute("rider", rider);
            request.getSession().setAttribute("user", rider);  // 也存到 user，方便顶部导航栏识别
            return new ModelAndView("redirect:/rider/index");
        }
        // 登录失败 → 回到登录页，带上错误信息和角色参数
        try {
            return new ModelAndView("redirect:/user/login?role=rider&riderError="
                + java.net.URLEncoder.encode("骑手账号或密码错误", "UTF-8"));
        } catch (Exception e) {
            return new ModelAndView("redirect:/user/login?role=rider&riderError=login_failed");
        }
    }

    @RequestMapping("/logout")
    /**
     * 处理用户退出登录。
     */
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().removeAttribute("rider");
        request.getSession().removeAttribute("user");
        return new ModelAndView("redirect:/user/login?role=rider");
    }

    @RequestMapping("/index")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView index(@RequestParam(value = "tab", defaultValue = "pending") String tab,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");

        ModelAndView mv = new ModelAndView("rider/index");
        mv.addObject("rider", rider);
        mv.addObject("tab", tab);

        // 各状态统计
        int pendingCount = riderService.getPendingOrders(1, 1).getTotalCount();
        int pickupCount = riderService.getRiderPickupOrders(rider.getId(), 1, 1).getTotalCount();
        int deliveringCount = riderService.getRiderDeliveringOrders(rider.getId(), 1, 1).getTotalCount();
        int completedCount = riderService.getRiderCompletedOrders(rider.getId(), 1, 1).getTotalCount();

        mv.addObject("pendingCount", pendingCount);
        mv.addObject("pickupCount", pickupCount);
        mv.addObject("deliveringCount", deliveringCount);
        mv.addObject("completedCount", completedCount);

        mv.addObject("unreadCount", riderService.getUnreadCount(rider.getId()));

        if ("pending".equals(tab)) {
            mv.addObject("orders", riderService.getPendingOrders(page, 10));
        } else if ("pickup".equals(tab)) {
            mv.addObject("orders", riderService.getRiderPickupOrders(rider.getId(), page, 10));
        } else if ("delivering".equals(tab)) {
            mv.addObject("orders", riderService.getRiderDeliveringOrders(rider.getId(), page, 10));
        } else {
            mv.addObject("orders", riderService.getRiderCompletedOrders(rider.getId(), page, 10));
        }
        return mv;
    }

    @RequestMapping("/accept")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView accept(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        riderService.acceptOrder(orderId, rider.getId());
        return new ModelAndView("redirect:/rider/index?tab=pickup");
    }

    @RequestMapping("/startDelivery")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView startDelivery(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        riderService.startDelivery(orderId, rider.getId());
        return new ModelAndView("redirect:/rider/index?tab=delivering");
    }

    @RequestMapping("/complete")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView complete(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        Order order = orderService.getOrderById(orderId);
        // 优先使用管理员设置的配送费(commission)，未设置则按订单金额的10%计算
        double income = order.getCommission() > 0 ? order.getCommission() : order.getTotal() * 0.1;
        riderService.completeDelivery(orderId, rider.getId(), income);
        return new ModelAndView("redirect:/rider/index?tab=completed");
    }

    @RequestMapping("/orderDetail")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView orderDetail(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");

        Order order = orderService.getOrderById(orderId);
        List<com.javaBean.OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
        ModelAndView mv = new ModelAndView("rider/orderDetail");
        mv.addObject("order", order);
        mv.addObject("items", items);
        mv.addObject("rider", rider);
        return mv;
    }

    @RequestMapping("/profile")
    /**
     * 显示用户个人中心页面。
     */
    public ModelAndView profile(HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");

        User fullRider = riderService.getById(rider.getId());
        int completedCount = riderService.getTotalCompletedCount(rider.getId());
        double income = riderService.getTotalIncome(rider.getId());
        int unreadCount = riderService.getUnreadCount(rider.getId());

        ModelAndView mv = new ModelAndView("rider/profile");
        mv.addObject("rider", fullRider);
        mv.addObject("completedCount", completedCount);
        mv.addObject("totalIncome", income);
        mv.addObject("unreadCount", unreadCount);
        return mv;
    }

    @RequestMapping("/editProfile")
    /**
     * 更新数据。
     */
    public ModelAndView editProfile(HttpServletRequest request) {
        User session = (User) request.getSession().getAttribute("rider");
        if (session == null) return new ModelAndView("redirect:/rider/login");

        User fresh = riderService.getById(session.getId());
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String newPassword = request.getParameter("newPassword");

        if (name != null && !name.trim().isEmpty()) fresh.setName(name.trim());
        if (phone != null && !phone.trim().isEmpty()) fresh.setPhone(phone.trim());
        if (email != null && !email.trim().isEmpty()) fresh.setEmail(email.trim());
        if (address != null && !address.trim().isEmpty()) fresh.setAddress(address.trim());
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            fresh.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(newPassword.trim()));
        }

        riderService.updateProfile(fresh);
        request.getSession().setAttribute("rider", riderService.getById(session.getId()));
        request.getSession().setAttribute("user", riderService.getById(session.getId()));
        return new ModelAndView("redirect:/rider/profile");
    }

    @RequestMapping("/messages")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView messages(@RequestParam(value = "type", defaultValue = "all") String type,
                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                  HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");

        PageResult<RiderMessage> messages = riderService.getMessages(rider.getId(), type, page, 20);
        int unreadCount = riderService.getUnreadCount(rider.getId());
        // 获取配送中的订单及其聊天记录
        List<com.javaBean.RiderChat> recentChats = riderChatService.getRecentChats(rider.getId());

        ModelAndView mv = new ModelAndView("rider/messages");
        mv.addObject("rider", rider);
        mv.addObject("messages", messages);
        mv.addObject("currentType", type);
        mv.addObject("unreadCount", unreadCount);
        mv.addObject("recentChats", recentChats);
        return mv;
    }

    @RequestMapping("/markRead")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView markRead(@RequestParam(value = "messageId", defaultValue = "0") int messageId,
                                  HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        if (messageId > 0) {
            riderService.markMessageRead(messageId);
        } else {
            riderService.markAllMessagesRead(rider.getId());
        }
        return new ModelAndView("redirect:/rider/messages");
    }

    // ═══════════════ 骑手-用户对话 ═══════════════
    @RequestMapping("/chat")
    /**
     * 处理AI聊天请求。
     */
    public ModelAndView chat(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        Order order = orderService.getOrderById(orderId);
        List<com.javaBean.RiderChat> chats = riderChatService.getMessages(orderId);
        ModelAndView mv = new ModelAndView("rider/chat");
        mv.addObject("rider", rider);
        mv.addObject("order", order);
        mv.addObject("orderId", orderId);
        mv.addObject("chats", chats);
        return mv;
    }

    @RequestMapping("/chatSend")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView chatSend(@RequestParam("orderId") String orderId,
                                  @RequestParam("content") String content,
                                  HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        if (content != null && !content.trim().isEmpty()) {
            riderChatService.sendMessage(orderId, "rider", rider.getName(), content.trim());
        }
        return new ModelAndView("redirect:/rider/chat?orderId=" + orderId);
    }

    @RequestMapping("/income")
    /**
     * 执行对应业务操作。
     */
    public ModelAndView income(HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");

        User fullRider = riderService.getById(rider.getId());
        double totalIncome = riderService.getTotalIncome(rider.getId());
        int totalCompleted = riderService.getTotalCompletedCount(rider.getId());
        int todayCount = riderService.getTodayCompletedCount(rider.getId());
        double todayIncome = riderService.getTodayIncome(rider.getId());
        List<Map<String, Object>> dailyStats = riderService.getDailyIncomeLast7Days(rider.getId());

        ModelAndView mv = new ModelAndView("rider/income");
        mv.addObject("rider", fullRider);
        mv.addObject("totalIncome", totalIncome);
        mv.addObject("totalCompleted", totalCompleted);
        mv.addObject("todayCount", todayCount);
        mv.addObject("todayIncome", todayIncome);
        mv.addObject("dailyStats", dailyStats);
        return mv;
    }

    // ===== 骑手注册 =====
    @RequestMapping("/register")
    /**
     * 显示注册页面。
     */
    public ModelAndView registerPage(HttpServletRequest request) {
        // 如果已登录骑手，重定向到骑手首页
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider != null) {
            return new ModelAndView("redirect:/rider/index");
        }
        return new ModelAndView("rider/register");
    }

    @RequestMapping(value = "/register", method = org.springframework.web.bind.annotation.RequestMethod.POST)
    /**
     * 执行对应业务操作。
     */
    public ModelAndView doRegister(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("rider/register");
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");

            // 验证
            if (username == null || username.trim().length() < 2) {
                mv.addObject("error", "骑手账号至少2个字符");
                return mv;
            }
            if (password == null || password.length() < 8) {
                mv.addObject("error", "密码至少8位");
                return mv;
            }
            if (!password.equals(confirmPassword)) {
                mv.addObject("error", "两次密码不一致");
                return mv;
            }
            if (name == null || name.trim().isEmpty()) {
                mv.addObject("error", "请输入真实姓名");
                return mv;
            }
            if (phone == null || !phone.matches("1\\d{10}")) {
                mv.addObject("error", "手机号必须为11位数字且以1开头");
                return mv;
            }

            // 检查用户名是否已存在
            User exist = userMapper.getUserByName(username.trim());
            if (exist != null) {
                mv.addObject("error", "该账号已被注册");
                return mv;
            }

            // 注册骑手 (isadmin="2", isvalidate="1" 自动通过)
            userMapper.register(username.trim(), userService.encodePassword(password),
                    name.trim(), phone.trim(), "",
                    email != null ? email.trim() : "",
                    "2");
            // 设置为已审核
            User newUser = userMapper.getUserByName(username.trim());
            if (newUser != null) {
                userMapper.verifyUser(newUser.getId());
            }

            mv.addObject("success", "骑手注册成功！请返回登录页面登录。");
        } catch (Exception e) {
            mv.addObject("error", "注册失败：" + e.getMessage());
        }
        return mv;
    }

}