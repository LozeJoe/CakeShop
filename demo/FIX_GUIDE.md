# 🔧 CakeShop Bug 修复补丁指南

> 配套文件：[BUG_REPORT.md](./BUG_REPORT.md)
> 以下按优先级提供可直接应用的代码修改

---

## P0-1：修复下单时扣库存+增销量（CRITICAL-01 + HIGH-01 + MEDIUM-05）

### 1.1 在 OrderService 接口中新增方法

**文件：** `src/main/java/com/service/OrderService.java`

新增方法声明：
```java
/**
 * 从购物车创建订单（事务保护，含库存扣减）
 * @return 创建的订单
 */
Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype);
```

### 1.2 在 OrderServiceImpl 中实现

**文件：** `src/main/java/com/service/OrderServiceImpl.java`

在类顶部新增导入：
```java
import com.javaBean.Cart;
import com.javaBean.User;
import com.mapper.CartMapper;
```

新增 `@Resource` 注入：
```java
@Resource
private CartMapper cartMapper;
```

新增方法实现：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype) {
    if (cartList == null || cartList.isEmpty()) {
        throw new RuntimeException("购物车为空，无法创建订单");
    }
    
    double total = 0;
    int amount = 0;
    
    // 预检库存
    for (Cart cart : cartList) {
        int goodsId = Integer.parseInt(cart.getGoodId());
        Goods goods = goodsMapper.getGoodsById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + cart.getGoodId());
        }
        if (goods.getStock() < cart.getAmount()) {
            throw new RuntimeException("商品 [" + goods.getName() + "] 库存不足，当前库存: " + goods.getStock());
        }
        total += cart.getTotalPrice();
        amount += cart.getAmount();
    }
    
    // 生成订单
    String orderId = String.valueOf(System.currentTimeMillis());
    Order order = new Order();
    order.setId(orderId);
    order.setTotal(total);
    order.setAmount(amount);
    order.setStatus(paytype > 0 ? 2 : 1); // paytype>0 表示直接支付
    order.setPaytype(paytype);
    order.setName(name != null && !name.trim().isEmpty() ? name : user.getUsername());
    order.setPhone(phone != null ? phone : "");
    order.setAddress(address != null ? address : "");
    order.setDatetime(new java.util.Date().toString());
    order.setUserId(user.getId());
    
    orderMapper.addOrder(order);
    
    // 扣库存、增销量、创建OrderItem
    for (Cart cart : cartList) {
        int goodsId = Integer.parseInt(cart.getGoodId());
        goodsMapper.decreaseStock(goodsId, cart.getAmount());
        goodsMapper.increaseSales(goodsId, cart.getAmount());
        
        OrderItem item = new OrderItem();
        item.setPrice(cart.getPrice());
        item.setAmount(cart.getAmount());
        item.setGoodsId(goodsId);
        item.setOrderId(orderId);
        orderItemMapper.addOrderItem(item);
    }
    
    // 清空购物车
    cartMapper.clearCart(user.getUsername());
    
    return order;
}
```

### 1.3 改造 CartController.pay()

**文件：** `src/main/java/com/controller/CartController.java`

将 `pay()` 方法中的下单逻辑替换为调用 Service：

```java
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

        String name = request.getParameter("username");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        int paytype = Integer.parseInt(request.getParameter("paytype"));

        // ★ 改为调用事务保护的 Service 方法
        Order order = orderService.createOrderFromCart(user, cartList, name, phone, address, paytype);

        modelAndView.addObject("orderId", order.getId());
        modelAndView.addObject("total", order.getTotal());
        modelAndView.addObject("paytype", paytype == 1 ? "支付宝" : "微信支付");  // 统一文案
        modelAndView.setViewName("paySuccess");
    } catch (Exception e) {
        e.printStackTrace();
        modelAndView.addObject("error", e.getMessage());
        modelAndView.setViewName("error");
    }
    return modelAndView;
}
```

### 1.4 改造 OrderController.createOrder()

**文件：** `src/main/java/com/controller/OrderController.java`

同样替换为调用 `orderService.createOrderFromCart()`，与上面逻辑一致。

### 1.5 在 GoodsMapper.decreaseStock 中增加保护条件

**文件：** `src/main/java/com/mapper/GoodsMapper.java`

```java
@Update("update goods set stock = stock - #{count} where id = #{id} and stock >= #{count}")
public void decreaseStock(@Param("id") int id, @Param("count") int count);
```

> 同时在 Service 层检查返回值（MyBatis 返回影响行数），若为 0 则抛出异常。

---

## P1-1：修复 @Param 缺失（HIGH-02）

### UserMapper.java

```java
// 修复前
public User login(String username, String password);

// 修复后
public User login(@Param("username") String username, @Param("password") String password);
```

### CartMapper.java

```java
// 修复前
public Cart getCartByUserNameAndGoodId(String userName, String goodId);

// 修复后
public Cart getCartByUserNameAndGoodId(@Param("userName") String userName, @Param("goodId") String goodId);
```

### OrderMapper.java

```java
// 修复前
public void updateOrderStatus(String id, int status);

// 修复后
public void updateOrderStatus(@Param("id") String id, @Param("status") int status);
```

---

## P1-2：修复订单取消越权（HIGH-03）

**文件：** `src/main/java/com/controller/OrderController.java` — `cancelOrder()` 方法

在获取 Order 之后、执行取消逻辑之前增加所有权校验：

```java
Order order = orderService.getOrderById(orderId);

// ★ 新增：校验订单归属
if (order == null || order.getUserId() != user.getId()) {
    modelAndView.addObject("msg", "订单不存在或无权操作");
    modelAndView.setViewName("redirect:/order/myOrder");
    return modelAndView;
}

// 原有逻辑...
if (order != null && order.getStatus() < 3) {
```

---

## P1-3：修复评论删除越权（HIGH-04）

**文件：** `src/main/java/com/controller/ReviewController.java`

需要在 `ReviewMapper` 中新增查询单条评论的方法，然后在 Controller 中校验所有权：

```java
// ReviewMapper.java 新增
@Select("select * from review where id = #{id}")
Review getReviewById(@Param("id") int id);
```

```java
// ReviewController.java deleteReview() 修改
Review review = reviewService.getReviewById(id);  // 需新增 Service 方法
if (review == null) {
    modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=评论不存在");
    return modelAndView;
}
// 仅允许本人或管理员删除
if (review.getUserId() != user.getId() && !"1".equals(user.getIsadmin())) {
    modelAndView.setViewName("redirect:/goods/detail?id=" + goodsId + "&msg=无权删除此评论");
    return modelAndView;
}
reviewService.deleteReview(id);
```

---

## P1-4：修复 Admin 4个接口权限缺失（MEDIUM-02）

**文件：** `src/main/java/com/controller/AdminController.java`

为以下 4 个方法添加统一的权限校验开头：

```java
// userVerify, userFreeze, userUnfreeze, userSetAdmin 各方法开头增加：
HttpSession session = request.getSession();
User admin = (User) session.getAttribute("user");
if (admin == null || !"1".equals(admin.getIsadmin())) {
    return new ModelAndView("redirect:/user/login");
}
```

> 注意：这 4 个方法当前没有 `HttpServletRequest request` 参数，需要新增。

---

## P2-1：MD5 替换为 BCrypt（MEDIUM-01）

**文件：** `src/main/java/com/service/UserServiceImpl.java`

```java
// 新增 BCrypt 编码器
private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

@Override
public String md5(String input) {
    // 改为 BCrypt
    return encoder.encode(input);
}

// 新增密码校验方法
public boolean checkPassword(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
}
```

**文件：** `src/main/java/com/mapper/UserMapper.java` — `login` 方法需要调整

> ⚠️ 注意：BCrypt 迁移需要数据迁移策略。建议：
> 1. 新增 `password_v2` 列
> 2. 用户登录时若 `password_v2` 为空，用旧 MD5 校验通过后自动升级为 BCrypt
> 3. 全量迁移完成后删除 MD5 逻辑和旧列

---

## P2-2：修复 Content-Encoding 错误（MEDIUM-04）

**文件：** `src/main/java/com/config/GlobalEncodingFilter.java`

**删除第 23 行：**
```java
// ❌ 删除这行
resp.setHeader("Content-Encoding", "UTF-8");
```

---

## P2-3：购物车加数量时校验库存（MEDIUM-03）

**文件：** `src/main/java/com/controller/CartController.java` — `addOne()` 方法

```java
// 在 cart.setAmount(cart.getAmount() + 1) 之前增加：
Goods goods = goodsService.getGoodsById(Integer.parseInt(cart.getGoodId()));
if (goods == null || cart.getAmount() + 1 > goods.getStock()) {
    modelAndView.setViewName("redirect:/cart/cartList?msg=" 
        + URLEncoder.encode("商品 [" + (goods != null ? goods.getName() : "未知") + "] 库存不足", "UTF-8"));
    return modelAndView;
}
```

同样修改 `CartController.addToCart()` 方法（第 55 行附近）。

---

## 应用顺序总结

```
1. HIGH-02     → @Param 修复（否则方法无法调用）
2. MEDIUM-05   → decreaseStock SQL 加保护
3. CRITICAL-01 → OrderService.createOrderFromCart() 实现
4. HIGH-01     → Controller 改为调用 Service
5. HIGH-03/04  → 越权修复
6. MEDIUM-02   → Admin 权限修复
7. MEDIUM-04   → Content-Encoding 删除
8. MEDIUM-01   → MD5→BCrypt（需要数据迁移窗口）
9. LOW-01~05   → 代码质量改进
```
