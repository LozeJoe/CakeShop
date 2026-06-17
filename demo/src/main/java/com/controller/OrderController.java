package com.controller;

import com.javaBean.*;
import com.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RequestMapping("/order")
@Controller
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private CartService cartService;

    @Resource
    private TypeService typeService;

    @Resource
    private GoodsService goodsService;

    @RequestMapping("/orderList")
    public ModelAndView orderList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user != null) {
                List<Order> orderList = orderService.getOrdersByUserId(user.getId());
                modelAndView.addObject("orderList", orderList);
            }

            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.setViewName("orderList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/myOrder")
    public ModelAndView myOrder(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            int pageSize = 5;
            PageResult<Order> pageResult;

            if (keyword != null && !keyword.trim().isEmpty()) {
                pageResult = orderService.searchOrdersByUserId(user.getId(), keyword.trim(), page, pageSize);
            } else {
                pageResult = orderService.getOrdersByUserIdPage(user.getId(), page, pageSize);
            }

            modelAndView.addObject("orderList", pageResult.getData());
            modelAndView.addObject("currentPage", pageResult.getCurrentPage());
            modelAndView.addObject("totalPages", pageResult.getTotalPages());
            modelAndView.addObject("totalCount", pageResult.getTotalCount());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.setViewName("myOrder");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/createOrder")
    public ModelAndView createOrder(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            List<Cart> cartList = cartService.getCartByUserName(user.getUsername());
            if (cartList == null || cartList.isEmpty()) {
                modelAndView.addObject("error", "购物车为空");
                modelAndView.setViewName("cartList");
                return modelAndView;
            }
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            Order order = orderService.createOrderFromCart(user, cartList, name, phone, address, 0);
            modelAndView.addObject("orderId", order.getId());
            modelAndView.addObject("total", order.getTotal());
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.setViewName("orderConfirm");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/toPay")
    public ModelAndView toPay(@RequestParam("orderId") String orderId) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            Order order = orderService.getOrderById(orderId);

            if (order != null && order.getStatus() == 1) {
                modelAndView.addObject("orderId", orderId);
                modelAndView.addObject("total", order.getTotal());
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.setViewName("orderConfirm");
            } else {
                modelAndView.addObject("error", "订单不存在或已支付");
                modelAndView.setViewName("orderList");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/pay")
    public ModelAndView pay(@RequestParam("orderId") String orderId,
                           @RequestParam("paytype") int paytype,
                           HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            Order order = orderService.getOrderById(orderId);

            if (order != null && order.getStatus() == 1) {
                orderService.updateOrderStatus(orderId, 2);

                modelAndView.addObject("orderId", orderId);
                modelAndView.addObject("paytype", paytype == 1 ? "支付宝" : "微信支付");
                modelAndView.addObject("total", order.getTotal());
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.setViewName("paySuccess");
            } else {
                modelAndView.addObject("error", "订单不存在或已支付");
                modelAndView.setViewName("orderList");
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/detail")
    public ModelAndView detail(@RequestParam("orderId") String orderId) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                modelAndView.addObject("error", "订单不存在");
                modelAndView.setViewName("error");
                return modelAndView;
            }
            List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);

            modelAndView.addObject("order", order);
            modelAndView.addObject("orderItems", orderItems);
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.setViewName("orderDetail");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/cancelOrder")
    public ModelAndView cancelOrder(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Order order = orderService.getOrderById(orderId);
            
            if (order == null || order.getUserId() != user.getId()) {
                modelAndView.addObject("msg", "订单不存在或无权操作");
                modelAndView.setViewName("redirect:/order/myOrder");
                return modelAndView;
            }
            
            // 订单状态机在 orderService.cancelOrder 内部校验
            // 先获取订单项（用于还原到购物车，取消后获取不到）
            List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);
            
            // 取消订单（状态机校验在此执行）
            orderService.cancelOrder(orderId);
            
            // 取消成功，将商品合并到购物车（不重复）
            for (OrderItem item : orderItems) {
                Goods goods = goodsService.getGoodsById(item.getGoodsId());
                if (goods == null) continue;
                
                Cart existingCart = cartService.getCartByUserNameAndGoodId(user.getUsername(), String.valueOf(item.getGoodsId()));
                if (existingCart != null) {
                    existingCart.setAmount(existingCart.getAmount() + item.getAmount());
                    existingCart.setTotalPrice(existingCart.getPrice() * existingCart.getAmount());
                    cartService.updateCart(existingCart);
                } else {
                    Cart cart = new Cart();
                    cart.setGoodId(String.valueOf(item.getGoodsId()));
                    cart.setUserName(user.getUsername());
                    cart.setAmount(item.getAmount());
                    cart.setIntro(goods.getIntro());
                    cart.setPrice(goods.getPrice());
                    cart.setTotalPrice(goods.getPrice() * item.getAmount());
                    cart.setCover(goods.getCover());
                    cartService.addCart(cart);
                }
            }
            
            modelAndView.addObject("msg", "订单已取消，商品已还原到购物车");
            
            modelAndView.setViewName("redirect:/order/myOrder");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/review")
    public ModelAndView reviewOrder(@RequestParam("orderId") String orderId,
                                     @RequestParam("rating") int rating,
                                     @RequestParam("content") String content,
                                     HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }

            Order order = orderService.getOrderById(orderId);
            if (order == null || order.getUserId() != user.getId()) {
                modelAndView.addObject("msg", "订单不存在或无权操作");
                modelAndView.setViewName("redirect:/order/myOrder");
                return modelAndView;
            }

            orderService.setReview(orderId, rating, content);
            modelAndView.addObject("msg", "评价提交成功，感谢您的反馈！");
            modelAndView.setViewName("redirect:/order/myOrder");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    @RequestMapping("/reorder")
    public ModelAndView reorder(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            
            // 获取原订单的订单项
            List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(orderId);
            
            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) {
                    Goods goods = goodsService.getGoodsById(item.getGoodsId());
                    if (goods == null) continue;
                    
                    // 将商品合并到购物车（不重复）
                    Cart existingCart = cartService.getCartByUserNameAndGoodId(user.getUsername(), String.valueOf(item.getGoodsId()));
                    if (existingCart != null) {
                        // 购物车已有该商品，合并数量
                        existingCart.setAmount(existingCart.getAmount() + item.getAmount());
                        existingCart.setTotalPrice(existingCart.getPrice() * existingCart.getAmount());
                        cartService.updateCart(existingCart);
                    } else {
                        Cart cart = new Cart();
                        cart.setGoodId(String.valueOf(item.getGoodsId()));
                        cart.setUserName(user.getUsername());
                        cart.setAmount(item.getAmount());
                        cart.setIntro(goods.getIntro());
                        cart.setPrice(goods.getPrice());
                        cart.setTotalPrice(goods.getPrice() * item.getAmount());
                        cart.setCover(goods.getCover());
                        cartService.addCart(cart);
                    }
                }
                
                modelAndView.setViewName("redirect:/cart/cartList");
            } else {
                modelAndView.addObject("msg", "无法找到订单商品");
                modelAndView.setViewName("redirect:/order/orderList");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }
}
