# 🐛 CakeShop 项目 Bug 报告与修复建议

> 项目：Cookie Shop (Spring Boot 2.2.6 + MyBatis-Plus 3.5.0 + Thymeleaf)
> 分析范围：全部 Java 源码、Mapper、Controller、配置、SQL Schema
> 生成时间：2025-07-15

---

## 一、严重程度分级

| 级别 | 标识 | 含义 |
| --- | --- | --- |
| 🔴 CRITICAL | 致命 | 核心业务流程错误，数据不一致，必须立即修复 |
| 🟠 HIGH | 高危 | 运行时崩溃风险 / 安全漏洞 |
| 🟡 MEDIUM | 中等 | 功能缺陷 / 数据准确性问题 |
| 🟢 LOW | 低 | 代码质量 / 潜在隐患 / 规范问题 |

---

## 二、Bug 清单（共 14 项）

---

### 🔴 CRITICAL-01：下单时未扣减库存，未增加销量

**位置：**
- `CartController.java:pay()` → [CartController.java](../demo/src/main/java/com/controller/CartController.java)
- `OrderController.java:createOrder()` → [OrderController.java](../demo/src/main/java/com/controller/OrderController.java)

**描述：**
`decreaseStock()` 和 `increaseSales()` 方法已在 `GoodsService` 和 `GoodsMapper` 中完整实现，但在两个下单入口（`CartController.pay` 和 `OrderController.createOrder`）中 **从未被调用**。

```java
// OrderController.createOrder — 下单流程中缺失以下关键步骤：
for (Cart cart : cartList) {
    // ❌ 缺失: goodsService.decreaseStock(cart.getGoodId(), cart.getAmount());
    // ❌ 缺失: goodsService.increaseSales(cart.getGoodId(), cart.getAmount());
    OrderItem orderItem = new OrderItem();
    // ...
}
```

同理 `CartController.pay()` 也存在相同问题。

**影响：**
- 库存永远不会减少，用户可无限下单超出实际库存
- 销量永远为 0，热销排行/统计功能完全失效
- 这是一个 **数据一致性灾难**，直接影响核心电商闭环

**修复建议：**
在下单循环中增加扣库存和增销量调用，并用 `@Transactional` 包裹整个下单方法（参见 HIGH-01）。

```java
// 修复后
for (Cart cart : cartList) {
    goodsService.decreaseStock(Integer.parseInt(cart.getGoodId()), cart.getAmount());
    goodsService.increaseSales(Integer.parseInt(cart.getGoodId()), cart.getAmount());
    // ... 创建 OrderItem
}
```

---

### 🟠 HIGH-01：下单操作缺少事务保护

**位置：**
- `CartController.java:pay()` 第 182-230 行
- `OrderController.java:createOrder()` 第 91-135 行
- `OrderController.java:cancelOrder()` 第 198-240 行

**描述：**
整个下单链路（创建订单 + 逐个插入 OrderItem + 清空购物车）分散在 Controller 中，且 **没有任何事务边界**。如果中途任何一步失败（如第 3 个 OrderItem 插入失败）：
- 订单已创建但部分 OrderItem 缺失 → 金额不一致
- 购物车已被清空 → 用户丢失数据且不可恢复
- 即使加上 CRITICAL-01 的库存扣减，也会出现"扣了库存但订单未完成"的脏数据

**对比：** `OrderServiceImpl.cancelOrder()` 正确使用了 `@Transactional`，但下单流程没有。

**修复建议：**
将下单逻辑抽取到 `OrderService` 中，使用 `@Transactional` 注解：

```java
// OrderService 中新增
@Transactional
public Order createOrderFromCart(User user, String name, String phone, String address, int paytype) {
    List<Cart> cartList = cartService.getCartByUserName(user.getUsername());
    if (cartList.isEmpty()) throw new BusinessException("购物车为空");
    
    // 校验库存
    for (Cart cart : cartList) {
        Goods goods = goodsMapper.getGoodsById(Integer.parseInt(cart.getGoodId()));
        if (goods.getStock() < cart.getAmount()) {
            throw new BusinessException("商品 [" + goods.getName() + "] 库存不足");
        }
    }
    
    Order order = buildOrder(user, cartList, name, phone, address, paytype);
    orderMapper.addOrder(order);
    
    for (Cart cart : cartList) {
        goodsMapper.decreaseStock(Integer.parseInt(cart.getGoodId()), cart.getAmount());
        goodsMapper.increaseSales(Integer.parseInt(cart.getGoodId()), cart.getAmount());
        OrderItem item = buildOrderItem(cart, order.getId());
        orderItemMapper.addOrderItem(item);
    }
    
    cartMapper.clearCart(user.getUsername());
    return order;
}
```

---

### 🟠 HIGH-02：多参数 Mapper 方法缺少 @Param 注解 — 运行时崩溃风险

**位置：**
- `UserMapper.java:23` — `login(String username, String password)` → SQL 引用 `#{username}`, `#{password}`
- `CartMapper.java:19` — `getCartByUserNameAndGoodId(String userName, String goodId)` → SQL 引用 `#{userName}`, `#{goodId}`
- `OrderMapper.java:28` — `updateOrderStatus(String id, int status)` → SQL 引用 `#{id}`, `#{status}`

**描述：**
MyBatis 在多参数场景下，如果未使用 `@Param` 注解，会尝试通过 Java 8 的 `-parameters` 编译标志获取参数名。如果该标志未开启（Spring Boot 2.2.6 默认未强制开启），MyBatis 会退回到 `arg0`/`arg1` 命名，导致 `#{username}` 无法匹配 → **运行时抛出 `BindingException`**。

注意：项目中其他多参方法（如 `getUserByPage`、`searchGoodsPage`、`register` 等）都正确添加了 `@Param`，三者遗漏属于疏忽。

**验证方法：**
```bash
# 检查是否包含 -parameters 标志
javap -verbose UserMapper.class | grep -c "MethodParameters"
# 若输出为 0，则这三个方法必定在运行时报错
```

**修复建议：**
```java
// UserMapper.java 修复
public User login(@Param("username") String username, @Param("password") String password);

// CartMapper.java 修复
public Cart getCartByUserNameAndGoodId(@Param("userName") String userName, @Param("goodId") String goodId);

// OrderMapper.java 修复
public void updateOrderStatus(@Param("id") String id, @Param("status") int status);
```

或统一方案：在 `pom.xml` 中强制开启 `-parameters`：
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

---

### 🟠 HIGH-03：取消订单可越权操作任意用户订单

**位置：**
- `OrderController.java:cancelOrder()` — [OrderController.java](../demo/src/main/java/com/controller/OrderController.java)

**描述：**
`cancelOrder` 方法仅校验用户是否登录，但 **未校验订单是否属于当前用户**：

```java
HttpSession session = request.getSession();
User user = (User) session.getAttribute("user");
if (user == null) { /* 重定向到登录 */ }

Order order = orderService.getOrderById(orderId); // ❌ 任何登录用户都能取消任何订单
```

恶意用户 A 登录后，只需知道用户 B 的 `orderId`（时间戳生成，可枚举），即可取消 B 的订单。

**修复建议：**
```java
Order order = orderService.getOrderById(orderId);
if (order == null || order.getUserId() != user.getId()) {
    modelAndView.addObject("msg", "订单不存在或无权操作");
    modelAndView.setViewName("redirect:/order/myOrder");
    return modelAndView;
}
```

---

### 🟠 HIGH-04：评论删除无归属校验，任意用户可删除他人评论

**位置：**
- `ReviewController.java:deleteReview()` — [ReviewController.java](../demo/src/main/java/com/controller/ReviewController.java)

**描述：**
删除评论时仅校验登录状态，未检查 `review.userId == currentUser.id` 或是否为管理员。

```java
// ❌ 任意登录用户可以删除任意评论，只需知道 review id
reviewService.deleteReview(id);
```

**修复建议：**
```java
// 方案 A: 仅允许自己删除
Review review = reviewService.getReviewById(id); // 需新增方法
if (review != null && review.getUserId() == user.getId()) {
    reviewService.deleteReview(id);
}
// 方案 B: 管理员也可删除
if (review.getUserId() == user.getId() || "1".equals(user.getIsadmin())) { ... }
```

---

### 🟡 MEDIUM-01：MD5 密码哈希不安全

**位置：**
- `UserServiceImpl.java:md5()` — [UserServiceImpl.java](../demo/src/main/java/com/service/UserServiceImpl.java)

**描述：**
使用 MD5 对密码进行单向哈希。MD5 已被证明存在碰撞漏洞，且无盐值的纯 MD5 极易被彩虹表攻击。

**现状：**
```java
public String md5(String input) {
    MessageDigest md = MessageDigest.getInstance("MD5");
    // 无盐值，无迭代
}
```

数据库中存储的是 `md5("123")` = `202cb962ac59075b964b07152d234b70`，攻击者用反向查询可在毫秒级破解。

**修复建议：**
使用 BCrypt 替代（Spring Security 内置支持）：
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 注册/添加用户时
String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

// 登录验证时
boolean matches = new BCryptPasswordEncoder().matches(rawPassword, storedHash);
```

若不想引入 Spring Security，至少加盐并使用 SHA-256 迭代多次。

---

### 🟡 MEDIUM-02：Admin 接口中 4 个方法缺少管理员权限校验

**位置：**
- `AdminController.java` — `userVerify()`, `userFreeze()`, `userUnfreeze()`, `userSetAdmin()`

**描述：**
AdminController 中绝大多数方法都有完整的 admin 权限校验：
```java
User user = (User) session.getAttribute("user");
if (user == null || !"1".equals(user.getIsadmin())) {
    modelAndView.setViewName("redirect:/user/login");
    return modelAndView;
}
```

但以下 4 个方法直接执行业务逻辑，无任何权限校验：
- `userVerify(@RequestParam("id") int id)`
- `userFreeze(@RequestParam("id") int id)`
- `userUnfreeze(@RequestParam("id") int id)`
- `userSetAdmin(@RequestParam("id") int id, @RequestParam("isadmin") String isadmin)`

**影响：**
任何登录用户（包括待审核用户）都可以通过构造 URL 直接审核、冻结、解冻其他用户，甚至将自己提权为管理员。

**修复建议：**
为这 4 个方法添加与其他 admin 接口一致的权限校验代码。

---

### 🟡 MEDIUM-03：购物车增加数量时未校验库存

**位置：**
- `CartController.java:addOne()` — [CartController.java](../demo/src/main/java/com/controller/CartController.java)
- `CartController.java:addToCart()`（CartController 版） — 第 55 行附近

**描述：**
`addOne` 方法直接 `cart.setAmount(cart.getAmount() + 1)` 更新购物车，未检查对应商品的 `stock` 是否足够。用户可以通过购物车界面反复点击 "+"，将数量加到远超库存的值。

**对比：** `GoodsController.addToCart()` 方法正确实现了库存检查：
```java
if (newTotalAmount > goods.getStock()) { /* 拒绝并提示 */ }
```

但 `CartController.addToCart()` 版本也同样缺少库存检查（第 55 行直接更新）。

**修复建议：**
在 `addOne` 和 `CartController.addToCart` 中增加库存校验：
```java
Goods goods = goodsService.getGoodsById(Integer.parseInt(cart.getGoodId()));
if (cart.getAmount() + 1 > goods.getStock()) {
    modelAndView.setViewName("redirect:/cart/cartList?msg=库存不足");
    return modelAndView;
}
```

---

### 🟡 MEDIUM-04：`GlobalEncodingFilter` 设置错误的 HTTP 响应头

**位置：**
- `GlobalEncodingFilter.java:23` — [GlobalEncodingFilter.java](../demo/src/main/java/com/config/GlobalEncodingFilter.java)

**描述：**
```java
resp.setHeader("Content-Encoding", "UTF-8");  // ❌ 错误！
```

HTTP 规范中 `Content-Encoding` 用于声明**传输编码**（如 `gzip`、`deflate`、`br`），而非字符编码。将其设为 `"UTF-8"` 会导致部分浏览器/代理尝试以 UTF-8 解码响应体 → **页面乱码或报错**。

字符编码应通过 `Content-Type` 头指定，该行已在上一行正确设置：
```java
resp.setContentType("text/html;charset=UTF-8");  // ✓ 正确
```

**修复建议：**
**直接删除** `resp.setHeader("Content-Encoding", "UTF-8");` 这一行。

---

### 🟡 MEDIUM-05：下单时未做库存充足性校验

**位置：**
- `CartController.java:pay()` 和 `OrderController.java:createOrder()`

**描述：**
即使修复 CRITICAL-01（增加扣库存逻辑），当前代码在创建订单时也**未预检库存是否充足**。如果购物车中有商品库存已不足，`decreaseStock` 的 SQL 会将 `stock` 更新为负数（`UPDATE goods SET stock = stock - #{count}` 无 `WHERE stock >= #{count}` 条件）。

**修复建议：**
在事务内下单前，先遍历检查所有商品的库存：
```java
for (Cart cart : cartList) {
    Goods goods = goodsService.getGoodsById(Integer.parseInt(cart.getGoodId()));
    if (goods == null || goods.getStock() < cart.getAmount()) {
        throw new BusinessException("商品 [" + goods.getName() + "] 库存不足");
    }
}
```

并在 `GoodsMapper.decreaseStock` 的 SQL 中加入库存保护：
```sql
UPDATE goods SET stock = stock - #{count} WHERE id = #{id} AND stock >= #{count}
```
并根据返回值判断是否扣减成功。

---

### 🟢 LOW-01：上传图片强制使用 `.jpg` 扩展名，忽略原始格式

**位置：**
- `AdminController.java:goodsSave()` 第 290-310 行

**描述：**
```java
String coverFileName = System.currentTimeMillis() + "-cover.jpg";  // 始终 .jpg
coverFile.transferTo(new File(uploadPath, coverFileName));
```

不管用户上传的是 PNG、GIF 还是 JPEG，都保存为 `.jpg`。虽然浏览器可根据 MIME 类型正确渲染，但文件扩展名与实际格式不匹配，可能导致某些场景（下载、备份处理）出错。

**修复建议：**
```java
String originalName = coverFile.getOriginalFilename();
String ext = originalName != null && originalName.contains(".") 
    ? originalName.substring(originalName.lastIndexOf(".")) 
    : ".jpg";
String coverFileName = System.currentTimeMillis() + "-cover" + ext;
```

---

### 🟢 LOW-02：两处 `paytype` 显示文案不一致

**位置：**
- `OrderController.java:pay()` 第 173 行：`paytype == 1 ? "支付宝" : "微信支付"`
- `CartController.java:pay()` 第 234 行：`paytype == 1 ? "微信支付" : "支付宝支付"`

**描述：**
同一个 `paytype=1` 在两个地方映射的文案相反。这会使用户在订单确认页和支付成功页看到不一致的支付方式。

**修复建议：**
统一映射关系，建议定义常量或枚举：
```java
public enum PayType {
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付");
    // ...
}
```

---

### 🟢 LOW-03：`DataInitConfig` 中硬编码已知凭据

**位置：**
- `DataInitConfig.java:initUsers()` — [DataInitConfig.java](../demo/src/main/java/com/config/DataInitConfig.java)

**描述：**
```java
// admin 密码是 md5("admin")
userMapper.register("admin", "21232f297a57a5a743894a0e4a801fc3", "管理员", ...);
```

- 管理员初始密码 `admin` 可被任何人轻易猜到
- 硬编码的 MD5 哈希可在生产部署时泄露到版本控制中

**修复建议：**
- 使用 BCrypt 后改为 BCrypt 哈希
- 首次启动时强制要求修改默认密码
- 或通过环境变量注入初始密码

---

### 🟢 LOW-04：`CartController.subOne()` 残留调试日志

**位置：**
- `CartController.java:subOne()` 第 280-292 行

**描述：**
```java
System.out.println("找到购物车项: id=" + cart.getId() + ", 当前数量=" + cart.getAmount());
System.out.println("更新后: 数量=" + cart.getAmount() + ", 总价=" + cart.getTotalPrice());
System.out.println("更新结果: " + result);
```

生产代码中不应出现 `System.out.println` 调试语句。应移除或替换为日志框架（SLF4J/Logback）输出。

---

### 🟢 LOW-05：`pom.xml` 中存在重复依赖声明

**位置：**
- `pom.xml` 第 38-55 行与第 68-75 行

**描述：**
`spring-boot-starter-test` 被声明了两次（一次带 vintage exclusion，一次不带）。Maven 会使用后声明的版本，前一个 exclusion 配置可能失效。

---

## 三、修复优先级建议

| 优先级 | Bug ID | 修复顺序 |
| --- | --- | --- |
| P0（立即） | CRITICAL-01 | 下单扣库存+增销量 |
| P0（立即） | HIGH-01 | 下单加事务 |
| P0（立即） | MEDIUM-05 | 下单前库存预检 |
| P1（本周） | HIGH-02 | @Param 缺失 |
| P1（本周） | HIGH-03 | 订单越权 |
| P1（本周） | HIGH-04 | 评论越权 |
| P1（本周） | MEDIUM-02 | Admin 权限校验缺失 |
| P2（本迭代） | MEDIUM-01 | MD5 → BCrypt |
| P2（本迭代） | MEDIUM-03 | 购物车库存校验 |
| P2（本迭代） | MEDIUM-04 | Content-Encoding 修复 |
| P3（下迭代） | LOW-01~05 | 代码质量改进 |

---

## 四、附加建议

1. **下单流程重构**：当前 `CartController.pay` 和 `OrderController.createOrder` 中存在 ~80% 重复代码，建议统一收敛到 `OrderService.createOrderFromCart()` 方法中。

2. **全局异常处理**：所有 Controller 的 `catch (Exception e)` 块都只是 `e.printStackTrace()` + 跳转错误页。建议使用 `@ControllerAdvice` + `@ExceptionHandler` 统一处理。

3. **SQL 注入检查**：经审查，所有动态 SQL 都使用了 MyBatis `#{}` 参数绑定（非 `${}`），未发现注入风险。但 `<script>` 块中的动态拼接需持续关注。

4. **HTTPS**：`application.yml` 中 `useSSL=false`，生产环境必须切换为 `true`。
