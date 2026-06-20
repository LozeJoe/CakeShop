package com.controller;

import com.javaBean.Order;
import com.javaBean.PageResult;
import com.javaBean.RiderMessage;
import com.javaBean.User;
import com.service.OrderService;
import com.service.RiderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("/rider")
@Controller
public class RiderController {

    @Resource
    private RiderService riderService;

    @Resource
    private OrderService orderService;

    @Resource
    private com.service.RiderChatService riderChatService;

    @RequestMapping("/login")
    public ModelAndView loginPage() {
        // Redirect to unified login page with rider tab active
        return new ModelAndView("redirect:/user/login?role=rider");
    }

    @RequestMapping("/doLogin")
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
            return new ModelAndView("redirect:/user/login?role=rider&riderError=登录失败");
        }
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().removeAttribute("rider");
        request.getSession().removeAttribute("user");
        return new ModelAndView("redirect:/user/login?role=rider");
    }

    @RequestMapping("/index")
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
    public ModelAndView accept(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        riderService.acceptOrder(orderId, rider.getId());
        return new ModelAndView("redirect:/rider/index?tab=pickup");
    }

    @RequestMapping("/startDelivery")
    public ModelAndView startDelivery(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        User rider = (User) request.getSession().getAttribute("rider");
        if (rider == null) return new ModelAndView("redirect:/rider/login");
        riderService.startDelivery(orderId, rider.getId());
        return new ModelAndView("redirect:/rider/index?tab=delivering");
    }

    @RequestMapping("/complete")
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
}
