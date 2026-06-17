package com.controller;

import com.config.SystemConfigService;
import com.javaBean.*;
import com.javaBean.PageResult;
import com.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @Resource
    private UserService userService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private OrderService orderService;

    @Resource
    private TypeService typeService;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private AdminLogService adminLogService;

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            // 统计数据
            int userCount = userService.getUserCount();
            int goodsCount = goodsService.getGoodsCount();
            int orderCount = orderService.getOrderCount();
            int typeCount = typeService.getTypeCount();
            int lowStockCount = goodsService.getLowStockCount(5);

            // 最近订单（取最新5条）
            PageResult<Order> recentOrders = orderService.getFilteredOrdersPage(0, null, 1, 5);

            // ECharts 数据：订单状态分布
            java.util.List<java.util.Map<String, Object>> statusDist = orderService.getOrderStatusDistribution();
            // 构建状态分布 JSON
            StringBuilder statusJson = new StringBuilder("[");
            String[] statusNames = {"", "待支付", "待配送", "待取货", "配送中", "已送达", "已取消"};
            for (java.util.Map<String, Object> row : statusDist) {
                int s = ((Number) row.get("status")).intValue();
                long cnt = ((Number) row.get("cnt")).longValue();
                String name = (s >= 1 && s <= 5) ? statusNames[s] : "未知";
                statusJson.append("{\"value\":").append(cnt).append(",\"name\":\"").append(name).append("\"},");
            }
            if (statusJson.length() > 1 && statusJson.charAt(statusJson.length() - 1) == ',') statusJson.setLength(statusJson.length() - 1);
            statusJson.append("]");

            // 分类销售数据
            java.util.List<Type> types = typeService.getAllTypes();
            StringBuilder typeNamesJson = new StringBuilder("[");
            StringBuilder typeSalesJson = new StringBuilder("[");
            for (Type t : types) {
                java.util.List<Goods> goodsList = goodsService.getGoodsByType(t.getId());
                int totalSales = 0;
                for (Goods g : goodsList) totalSales += g.getSales();
                typeNamesJson.append("\"").append(t.getName()).append("\",");
                typeSalesJson.append(totalSales).append(",");
            }
            if (types.size() > 0) {
                typeNamesJson.setLength(typeNamesJson.length() - 1);
                typeSalesJson.setLength(typeSalesJson.length() - 1);
            }
            typeNamesJson.append("]");
            typeSalesJson.append("]");

            modelAndView.addObject("userCount", userCount);
            modelAndView.addObject("goodsCount", goodsCount);
            modelAndView.addObject("orderCount", orderCount);
            modelAndView.addObject("typeCount", typeCount);
            modelAndView.addObject("lowStockCount", lowStockCount);
            modelAndView.addObject("recentOrders", recentOrders.getData());
            modelAndView.addObject("statusDistributionJson", statusJson.toString());
            modelAndView.addObject("typeNamesJson", typeNamesJson.toString());
            modelAndView.addObject("typeSalesJson", typeSalesJson.toString());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("sidebarActive", "index");
            modelAndView.addObject("pageTitle", "数据概览");
            modelAndView.setViewName("admin/index");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 用户管理 ====================
    @RequestMapping("/users")
    public ModelAndView userList(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(value = "unverified", defaultValue = "false") boolean unverified,
                                 @RequestParam(value = "type", required = false) String type,
                                 HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User admin = (User) session.getAttribute("user");
            
            if (admin == null || !"1".equals(admin.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            PageResult<User> pageResult;
            String pageTitle;

            if ("rider".equals(type)) {
                // Rider management
                pageResult = userService.getRidersByPage(page, pageSize);
                pageTitle = "🛵 骑手管理";
            } else if (unverified) {
                pageResult = userService.getUnverifiedUsers(page, pageSize);
                pageTitle = "⏳ 待审核用户";
            } else {
                pageResult = userService.getUserByPage(page, pageSize);
                pageTitle = "👥 全部用户";
            }

            modelAndView.addObject("userList", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("unverified", unverified);
            modelAndView.addObject("filterType", type);
            modelAndView.addObject("sidebarActive", "users");
            modelAndView.addObject("pageTitle", pageTitle);
            modelAndView.addObject("headerExtra", "<a href='/admin/userEdit' class='btn btn-primary'>添加用户</a>");
            modelAndView.setViewName("admin/userList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/userVerify")
    public ModelAndView userVerify(@RequestParam("id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) return new ModelAndView("redirect:/user/login");
        userService.verifyUser(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping("/userFreeze")
    public ModelAndView userFreeze(@RequestParam("id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) return new ModelAndView("redirect:/user/login");
        userService.freezeUser(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping("/userUnfreeze")
    public ModelAndView userUnfreeze(@RequestParam("id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) return new ModelAndView("redirect:/user/login");
        userService.unfreezeUser(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping("/userSetAdmin")
    public ModelAndView userSetAdmin(@RequestParam("id") int id, @RequestParam("isadmin") String isadmin, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) return new ModelAndView("redirect:/user/login");
        userService.setUserAdmin(id, isadmin);
        return new ModelAndView("redirect:/admin/users");
    }

    @RequestMapping("/userEdit")
    public ModelAndView userEdit(@RequestParam(value = "id", required = false) Integer id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (id != null) {
                User editUser = userService.getUserById(id);
                modelAndView.addObject("user", editUser);
            }
            modelAndView.addObject("sidebarActive", "users");
            modelAndView.addObject("pageTitle", id != null ? "✏️ 编辑用户" : "➕ 添加用户");
            modelAndView.addObject("headerExtra", "<a href='/admin/users' class='btn btn-secondary'>返回列表</a>");
            modelAndView.setViewName("admin/userEdit");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/userSave")
    public ModelAndView userSave(@Valid User user, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "输入数据有误";
                modelAndView.addObject("error", errorMsg);
                modelAndView.addObject("user", user);
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.setViewName("admin/userEdit");
                return modelAndView;
            }
            HttpSession session = request.getSession();
            User admin = (User) session.getAttribute("user");
            
            if (admin == null || !"1".equals(admin.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (user.getId() != 0) {
                User existingUser = userService.getUserById(user.getId());
                boolean emailChanged = false;
                
                if (existingUser.getEmail() == null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                    emailChanged = true;
                } else if (existingUser.getEmail() != null && user.getEmail() != null && !user.getEmail().isEmpty() && !existingUser.getEmail().equals(user.getEmail())) {
                    emailChanged = true;
                }
                
                if (emailChanged) {
                    User emailUser = userService.getUserByEmail(user.getEmail());
                    if (emailUser != null && emailUser.getId() != user.getId()) {
                        modelAndView.addObject("error", "邮箱已被使用");
                        modelAndView.addObject("user", user);
                        modelAndView.addObject("typelist", typeService.getAllTypes());
                        modelAndView.setViewName("admin/userEdit");
                        return modelAndView;
                    }
                }
                // 密码处理：使用 BCrypt 加密
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    // 只要管理员填了密码，就重新 BCrypt 加密（即使与旧 hash 不同，BCrypt 随机盐也能保证安全）
                    user.setPassword(userService.encodePassword(user.getPassword()));
                } else {
                    // 未填密码则保留原密码
                    user.setPassword(existingUser.getPassword());
                }
                userService.updateUser(user);
            } else {
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    User emailUser = userService.getUserByEmail(user.getEmail());
                    if (emailUser != null) {
                        modelAndView.addObject("error", "邮箱已被使用");
                        modelAndView.addObject("user", user);
                        modelAndView.addObject("typelist", typeService.getAllTypes());
                        modelAndView.setViewName("admin/userEdit");
                        return modelAndView;
                    }
                }
                userService.register(user.getUsername(), user.getPassword(), user.getName(), user.getPhone(), user.getAddress(), user.getEmail(), user.getIsadmin() != null ? user.getIsadmin() : "0");
            }
            modelAndView.setViewName("redirect:/admin/users");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/userDelete")
    public ModelAndView userDelete(@RequestParam("id") int id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            userService.deleteUser(id);
            modelAndView.setViewName("redirect:/admin/users");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 商品管理 ====================
    @RequestMapping("/goods")
    public ModelAndView goodsList(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                  HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            PageResult<Goods> pageResult = goodsService.getGoodsByPage(page, pageSize);
            List<Type> typeList = typeService.getAllTypes();
            modelAndView.addObject("goodsList", pageResult.getData());
            modelAndView.addObject("typeList", typeList);
            modelAndView.addObject("sidebarActive", "goods");
            modelAndView.addObject("pageTitle", "🎂 商品管理");
            modelAndView.addObject("headerExtra", "<a href='/admin/goodsEdit' class='btn btn-primary'>添加商品</a>");
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.setViewName("admin/goodsList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/goodsEdit")
    public ModelAndView goodsEdit(@RequestParam(value = "id", required = false) Integer id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Goods editGoods = null;
            if (id != null) {
                editGoods = goodsService.getGoodsById(id);
                modelAndView.addObject("goods", editGoods);
            }
            modelAndView.addObject("typeList", typeService.getAllTypes());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("sidebarActive", "goods");
            modelAndView.addObject("pageTitle", editGoods != null ? "✏️ 编辑商品" : "➕ 添加商品");
            modelAndView.addObject("headerExtra", "<a href='/admin/goods' class='btn btn-secondary'>返回列表</a>");
            modelAndView.setViewName("admin/goodsEdit");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/goodsSave")
    public ModelAndView goodsSave(@Valid Goods goods, BindingResult bindingResult,
                                  @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                                  @RequestParam(value = "image1File", required = false) MultipartFile image1File,
                                  @RequestParam(value = "image2File", required = false) MultipartFile image2File,
                                  HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "输入数据有误";
                modelAndView.addObject("error", errorMsg);
                modelAndView.addObject("goods", goods);
                modelAndView.addObject("typeList", typeService.getAllTypes());
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.setViewName("admin/goodsEdit");
                return modelAndView;
            }
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            String uploadPath = request.getServletContext().getRealPath("/picture");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 图片上传校验
            MultipartFile[] files = {coverFile, image1File, image2File};
            String[] allowedTypes = {"image/jpeg", "image/png", "image/gif"};
            long maxSize = 5 * 1024 * 1024; // 5MB
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String contentType = file.getContentType();
                    boolean validType = false;
                    for (String allowed : allowedTypes) {
                        if (allowed.equals(contentType)) { validType = true; break; }
                    }
                    if (!validType) {
                        modelAndView.addObject("error", "仅支持 JPG/PNG/GIF 格式图片");
                        modelAndView.addObject("goods", goods);
                        modelAndView.addObject("typeList", typeService.getAllTypes());
                        modelAndView.setViewName("admin/goodsEdit");
                        return modelAndView;
                    }
                    if (file.getSize() > maxSize) {
                        modelAndView.addObject("error", "图片大小不能超过 5MB");
                        modelAndView.addObject("goods", goods);
                        modelAndView.addObject("typeList", typeService.getAllTypes());
                        modelAndView.setViewName("admin/goodsEdit");
                        return modelAndView;
                    }
                }
            }

            if (coverFile != null && !coverFile.isEmpty()) {
                String coverFileName = System.currentTimeMillis() + "-cover.jpg";
                coverFile.transferTo(new File(uploadPath, coverFileName));
                goods.setCover("/picture/" + coverFileName);
            }

            if (image1File != null && !image1File.isEmpty()) {
                String image1FileName = System.currentTimeMillis() + "-1.jpg";
                image1File.transferTo(new File(uploadPath, image1FileName));
                goods.setImage1("/picture/" + image1FileName);
            }

            if (image2File != null && !image2File.isEmpty()) {
                String image2FileName = System.currentTimeMillis() + "-2.jpg";
                image2File.transferTo(new File(uploadPath, image2FileName));
                goods.setImage2("/picture/" + image2FileName);
            }

            if (goods.getId() != 0) {
                goodsService.updateGoods(goods);
            } else {
                goodsService.addGoods(goods);
            }
            modelAndView.setViewName("redirect:/admin/goods");
        } catch (IOException e) {
            e.printStackTrace();
            modelAndView.addObject("error", "图片上传失败");
            modelAndView.setViewName("error");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/goodsDelete")
    public ModelAndView goodsDelete(@RequestParam("id") int id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            goodsService.deleteGoods(id);
            modelAndView.setViewName("redirect:/admin/goods");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 订单管理 ====================
    @RequestMapping("/orders")
    public ModelAndView orderList(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                  @RequestParam(value = "status", defaultValue = "0") int status,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            PageResult<Order> pageResult = orderService.getFilteredOrdersPage(status, keyword, page, pageSize);
            modelAndView.addObject("orderList", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("filterStatus", status);
            modelAndView.addObject("filterKeyword", keyword != null ? keyword : "");
            modelAndView.addObject("sidebarActive", "orders");
            modelAndView.addObject("pageTitle", "📦 订单管理");
            modelAndView.setViewName("admin/orderList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "订单管理出错: " + e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/orderDetail")
    public ModelAndView orderDetail(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                modelAndView.addObject("error", "订单不存在");
                modelAndView.setViewName("error");
                return modelAndView;
            }
            List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);
            modelAndView.addObject("order", order);
            modelAndView.addObject("orderItems", orderItems);
            modelAndView.addObject("sidebarActive", "orders");
            modelAndView.addObject("pageTitle", "📋 订单详情");
            modelAndView.addObject("headerExtra", "<a href='/admin/orders' class='btn btn-secondary'>返回列表</a>");
            modelAndView.setViewName("admin/orderDetail");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/orderUpdateStatus")
    public ModelAndView orderUpdateStatus(@RequestParam("orderId") String orderId, 
                                          @RequestParam("status") int status,
                                          HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            orderService.updateOrderStatus(orderId, status);
            modelAndView.setViewName("redirect:/admin/orders");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/orderSetCommission")
    public ModelAndView orderSetCommission(@RequestParam("orderId") String orderId,
                                           @RequestParam("commission") double commission,
                                           HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (commission < 0) commission = 0;
            orderService.setCommission(orderId, commission);
            modelAndView.setViewName("redirect:/admin/orders");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 分类管理 ====================
    @RequestMapping("/types")
    public ModelAndView typeList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            List<Type> typeList = typeService.getAllTypes();
            modelAndView.addObject("typeList", typeList);
            modelAndView.addObject("sidebarActive", "types");
            modelAndView.addObject("pageTitle", "🏷️ 分类管理");
            modelAndView.addObject("headerExtra", "<a href='/admin/typeEdit' class='btn btn-primary'>添加分类</a>");
            modelAndView.setViewName("admin/typeList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/typeEdit")
    public ModelAndView typeEdit(@RequestParam(value = "id", required = false) Integer id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (id != null) {
                Type type = typeService.getTypeById(id);
                modelAndView.addObject("type", type);
            }
            modelAndView.addObject("sidebarActive", "types");
            modelAndView.addObject("pageTitle", id != null ? "✏️ 编辑分类" : "➕ 添加分类");
            modelAndView.addObject("headerExtra", "<a href='/admin/types' class='btn btn-secondary'>返回列表</a>");
            modelAndView.setViewName("admin/typeEdit");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/typeSave")
    public ModelAndView typeSave(@Valid Type type, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "输入数据有误";
                modelAndView.addObject("error", errorMsg);
                modelAndView.addObject("type", type);
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.setViewName("admin/typeEdit");
                return modelAndView;
            }
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            if (type.getId() != 0) {
                typeService.updateType(type);
            } else {
                typeService.addType(type);
            }
            modelAndView.setViewName("redirect:/admin/types");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/typeDelete")
    public ModelAndView typeDelete(@RequestParam("id") int id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            typeService.deleteType(id);
            modelAndView.setViewName("redirect:/admin/types");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 操作日志 ====================
    @RequestMapping("/logs")
    public ModelAndView logs(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = "15") int pageSize,
                             HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            PageResult<AdminLog> pageResult = adminLogService.getLogsByPage(page, pageSize);
            modelAndView.addObject("logList", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("sidebarActive", "logs");
            modelAndView.addObject("pageTitle", "📋 操作日志");
            modelAndView.setViewName("admin/logs");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // ==================== 系统管理 ====================
    @RequestMapping("/settings")
    public ModelAndView systemConfig(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            modelAndView.addObject("config", systemConfigService.getAll());
            modelAndView.addObject("defaults", systemConfigService.getDefaults());
            modelAndView.addObject("sidebarActive", "settings");
            modelAndView.addObject("pageTitle", "⚙️ 系统管理");
            modelAndView.setViewName("admin/settings");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/saveSettings")
    public ModelAndView saveSystemConfig(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null || !"1".equals(user.getIsadmin())) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
            values.put("site_name", request.getParameter("site_name"));
            values.put("hero_title", request.getParameter("hero_title"));
            values.put("hero_subtitle", request.getParameter("hero_subtitle"));
            values.put("page_size", request.getParameter("page_size"));
            values.put("copyright", request.getParameter("copyright"));
            values.put("contact_phone", request.getParameter("contact_phone"));
            values.put("contact_email", request.getParameter("contact_email"));
            systemConfigService.updateAll(values);

            modelAndView.addObject("config", systemConfigService.getAll());
            modelAndView.addObject("defaults", systemConfigService.getDefaults());
            modelAndView.addObject("success", "设置已保存，重启后生效");
            modelAndView.addObject("sidebarActive", "settings");
            modelAndView.addObject("pageTitle", "⚙️ 系统管理");
            modelAndView.setViewName("admin/settings");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}
