package com.controller;

import com.javaBean.Cart;
import com.javaBean.Goods;
import com.javaBean.Type;
import com.javaBean.User;
import com.javaBean.Order;
import com.javaBean.OrderItem;
import com.service.CartService;
import com.service.GoodsService;
import com.service.TypeService;
import com.service.OrderService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.net.URLEncoder;
import java.util.Date;

@RequestMapping("/cart")
@Controller
public class CartController {

    @Resource
    private CartService cartService;
    
    @Resource
    private GoodsService goodsService;
    
    @Resource
    private TypeService typeService;
    
    @Resource
    private OrderService orderService;

    @RequestMapping("/cartList")
    public ModelAndView cartList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                modelAndView.setViewName("redirect:/user/login");
                return modelAndView;
            }
            
            List<Cart> cartList = cartService.getCartByUserName(user.getUsername());
            double total = 0;
            for (Cart cart : cartList) {
                total += cart.getTotalPrice();
            }
            
            modelAndView.addObject("cartList", cartList);
            modelAndView.addObject("total", total);
            modelAndView.addObject("typelist", typeService.getAllTypes());
            modelAndView.addObject("active", 1);
            modelAndView.addObject("count", cartService.getCartCount(user.getUsername()));
            
            modelAndView.setViewName("cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/addToCart")
    public ModelAndView addToCart(@RequestParam("goodId") int goodId, 
                                   @RequestParam(value = "amount", defaultValue = "1") String amountStr,
                                   @RequestParam(value = "from", defaultValue = "index") String from,
                                   HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Validate quantity parameter
            int amount = 1;
            try {
                amount = Integer.parseInt(amountStr);
                if (amount <= 0 || amount > 999) {
                    String msg = "数量不合法，请输入 1~999 之间的数量";
                    String encodedMsg = URLEncoder.encode(msg, "UTF-8");
                    if ("detail".equals(from)) {
                        modelAndView.setViewName("redirect:/goods/detail?id=" + goodId + "&msg=" + encodedMsg);
                    } else {
                        modelAndView.setViewName("redirect:/goods/goodList?msg=" + encodedMsg);
                    }
                    return modelAndView;
                }
            } catch (NumberFormatException e) {
                String msg = "数量格式不正确";
                String encodedMsg = URLEncoder.encode(msg, "UTF-8");
                if ("detail".equals(from)) {
                    modelAndView.setViewName("redirect:/goods/detail?id=" + goodId + "&msg=" + encodedMsg);
                } else {
                    modelAndView.setViewName("redirect:/goods/goodList?msg=" + encodedMsg);
                }
                return modelAndView;
            }
            
            Goods goods = null;
            boolean success = false;
            
            if (user != null) {
                goods = goodsService.getGoodsById(goodId);
                
                if (goods != null) {
                    // 检查库存
                    if (goods.getStock() >= amount) {
                        Cart existingCart = cartService.getCartByUserNameAndGoodId(user.getUsername(), String.valueOf(goodId));
                        
                        if (existingCart != null) {
                            // 更新现有购物车记录：数量加1，更新总价格
                            existingCart.setAmount(existingCart.getAmount() + amount);
                            existingCart.setTotalPrice(existingCart.getPrice() * existingCart.getAmount());
                            cartService.updateCart(existingCart);
                        } else {
                            // 创建新的购物车记录
                            Cart cart = new Cart();
                            cart.setGoodId(String.valueOf(goodId));
                            cart.setUserName(user.getUsername());
                            cart.setIntro(goods.getIntro());
                            cart.setAmount(amount);
                            cart.setPrice(goods.getPrice());
                            cart.setTotalPrice(goods.getPrice() * amount);
                            cart.setCover(goods.getCover());
                            cartService.addCart(cart);
                        }
                        
                        success = true;
                    }
                }
            }
            
            // 根据来源页面决定跳转方向
            String msg = success ? "添加购物车成功！" : "库存不足！";
            // 对中文消息进行URL编码
            String encodedMsg = null;
            if (msg != null && !msg.isEmpty()) {
                try {
                    encodedMsg = URLEncoder.encode(msg, "UTF-8");
                } catch (Exception e) {
                    encodedMsg = msg;
                }
            }
            
            if ("detail".equals(from)) {
                // 从详情页加购，返回详情页
                if (encodedMsg != null && !encodedMsg.isEmpty()) {
                    modelAndView.setViewName("redirect:/goods/detail?id=" + goodId + "&msg=" + encodedMsg);
                } else {
                    modelAndView.setViewName("redirect:/goods/detail?id=" + goodId);
                }
            } else {
                // 从首页加购，返回首页
                if (encodedMsg != null && !encodedMsg.isEmpty()) {
                    modelAndView.setViewName("redirect:/goods/goodList?msg=" + encodedMsg);
                } else {
                    modelAndView.setViewName("redirect:/goods/goodList");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/updateCart")
    public ModelAndView updateCart(@RequestParam("cartId") int cartId, 
                                    @RequestParam("amount") int amount,
                                    HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            // Validate amount
            if (amount <= 0 || amount > 999) {
                modelAndView.setViewName("redirect:/cart/cartList?msg=" + URLEncoder.encode("数量不合法", "UTF-8"));
                return modelAndView;
            }
            
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user != null) {
                List<Cart> cartList = cartService.getCartByUserName(user.getUsername());
                
                for (Cart cart : cartList) {
                    if (cart.getId() == cartId) {
                        // Check stock
                        Goods goods = goodsService.getGoodsById(Integer.parseInt(cart.getGoodId()));
                        if (goods != null && amount > goods.getStock()) {
                            modelAndView.setViewName("redirect:/cart/cartList?msg=" + URLEncoder.encode("库存不足", "UTF-8"));
                            return modelAndView;
                        }
                        cart.setAmount(amount);
                        cart.setTotalPrice(cart.getPrice() * amount);
                        cartService.updateCart(cart);
                        break;
                    }
                }
            }
            
            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/deleteCart")
    public ModelAndView deleteCart(@RequestParam("cartId") int cartId) {
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            cartService.deleteCart(cartId);
            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        
        return modelAndView;
    }

    @RequestMapping("/clearCart")
    public ModelAndView clearCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user != null) {
                cartService.clearCart(user.getUsername());
            }

            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/addOne")
    public ModelAndView addOne(@RequestParam("cartId") int cartId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user != null) {
                List<Cart> cartList = cartService.getCartByUserName(user.getUsername());

                for (Cart cart : cartList) {
                    if (cart.getId() == cartId) {
                        Goods goods = goodsService.getGoodsById(Integer.parseInt(cart.getGoodId()));
                        if (goods == null || cart.getAmount() + 1 > goods.getStock()) {
                            modelAndView.setViewName("redirect:/cart/cartList?msg=" + URLEncoder.encode("库存不足", "UTF-8"));
                            return modelAndView;
                        }
                        cart.setAmount(cart.getAmount() + 1);
                        cart.setTotalPrice(cart.getPrice() * cart.getAmount());
                        cartService.updateCart(cart);
                        break;
                    }
                }
            }

            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/subOne")
    public ModelAndView subOne(@RequestParam("cartId") int cartId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user != null) {
                List<Cart> cartList = cartService.getCartByUserName(user.getUsername());

                for (Cart cart : cartList) {
                    if (cart.getId() == cartId) {
                        if (cart.getAmount() > 1) {
                            cart.setAmount(cart.getAmount() - 1);
                            cart.setTotalPrice(cart.getPrice() * cart.getAmount());
                            cartService.updateCart(cart);
                        }
                        break;
                    }
                }
            }

            modelAndView.setViewName("redirect:/cart/cartList");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }

    @RequestMapping("/pay")
    public ModelAndView pay(HttpServletRequest request) {
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

            // Validate input fields — reject empty / malicious values
            String name = request.getParameter("username");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            if (name == null || name.trim().isEmpty()) {
                modelAndView.addObject("error", "请输入收货人姓名");
                modelAndView.addObject("cartList", cartList);
                modelAndView.addObject("total", calcCartTotal(cartList));
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.addObject("count", cartService.getCartCount(user.getUsername()));
                modelAndView.setViewName("cartList");
                return modelAndView;
            }
            if (phone == null || phone.trim().isEmpty()) {
                modelAndView.addObject("error", "请输入联系电话");
                modelAndView.addObject("cartList", cartList);
                modelAndView.addObject("total", calcCartTotal(cartList));
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.addObject("count", cartService.getCartCount(user.getUsername()));
                modelAndView.setViewName("cartList");
                return modelAndView;
            }
            if (address == null || address.trim().isEmpty()) {
                modelAndView.addObject("error", "请输入收货地址");
                modelAndView.addObject("cartList", cartList);
                modelAndView.addObject("total", calcCartTotal(cartList));
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.addObject("count", cartService.getCartCount(user.getUsername()));
                modelAndView.setViewName("cartList");
                return modelAndView;
            }

            // Sanitize inputs — strip HTML/script tags to prevent XSS
            name = sanitize(name);
            phone = sanitize(phone);
            address = sanitize(address);

            // Validate phone format (basic check)
            if (!phone.matches("^[0-9\\-\\+\\(\\)\\s]{5,20}$")) {
                modelAndView.addObject("error", "电话号码格式不正确");
                modelAndView.addObject("cartList", cartList);
                modelAndView.addObject("total", calcCartTotal(cartList));
                modelAndView.addObject("typelist", typeService.getAllTypes());
                modelAndView.addObject("active", 1);
                modelAndView.addObject("count", cartService.getCartCount(user.getUsername()));
                modelAndView.setViewName("cartList");
                return modelAndView;
            }

            String paytypeStr = request.getParameter("paytype");
            int paytype = 1;
            if (paytypeStr != null && !paytypeStr.isEmpty()) {
                try {
                    paytype = Integer.parseInt(paytypeStr);
                    if (paytype < 1 || paytype > 2) paytype = 1;
                } catch (NumberFormatException e) {
                    paytype = 1;
                }
            }

            // Collect delivery time
            String deliveryDate = request.getParameter("deliveryDate");
            String deliveryTimeVal = request.getParameter("deliveryTime");
            String deliveryTime = "";
            if (deliveryDate != null && !deliveryDate.isEmpty() && deliveryTimeVal != null && !deliveryTimeVal.isEmpty()) {
                deliveryTime = deliveryDate + " " + deliveryTimeVal;
            }

            // Collect coordinates (may be set by geocoding JS or left as 0)
            double lat = 0, lng = 0;
            try {
                String latStr = request.getParameter("latitude");
                String lngStr = request.getParameter("longitude");
                if (latStr != null && !latStr.isEmpty()) lat = Double.parseDouble(latStr);
                if (lngStr != null && !lngStr.isEmpty()) lng = Double.parseDouble(lngStr);
            } catch (NumberFormatException ignored) {}

            // Ignore client-submitted total; recalculate server-side
            // (the hidden 'total' field in the form is intentionally not read)
            Order order = orderService.createOrderFromCart(user, cartList, name, phone, address, paytype, deliveryTime, lat, lng);
            modelAndView.addObject("orderId", order.getId());
            modelAndView.addObject("total", order.getTotal());
            modelAndView.addObject("deliveryTime", deliveryTime);
            modelAndView.addObject("paytype", paytype == 1 ? "支付宝" : "微信支付");
            modelAndView.setViewName("paySuccess");
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    /** Calculate cart total server-side, ignoring any client-submitted amount. */
    private double calcCartTotal(List<Cart> cartList) {
        double total = 0;
        if (cartList != null) {
            for (Cart cart : cartList) {
                total += cart.getPrice() * cart.getAmount();
            }
        }
        return total;
    }

    /** Strip HTML/script tags from user input to prevent XSS. */
    private String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").trim();
    }
}