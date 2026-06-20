# 🍰 CakeShop 蛋糕在线商城

> **法式烘焙 · 匠心手作 · 一键送达**

基于 Spring Boot + Thymeleaf + MyBatis Plus 的全功能蛋糕电商系统。用户选购下单、管理员数据运营、骑手接单配送，三端协同，全链路闭环。

---

## ✨ 功能

### 🛒 用户端
- 商品浏览：分类筛选、关键词搜索、热销排行、新品上市
- 商品详情：多图展示、价格库存、收藏、评价
- 购物车：增删改查、库存校验、合并去重
- 订单管理：下单 → 支付 → 配送追踪，六状态流转
- 评价系统：1-5 星评分 + 文字评价
- **💬 联系骑手**：配送中与骑手实时对话
- 🤖 **AI 客服「小甜」**：DeepSeek 驱动

### 🔧 管理后台
- **📊 数据仪表盘**：8 KPI + 6 ECharts 图表，30s 自动刷新
- 👥 用户/🛵 骑手/🎂 商品/📦 订单/🏷️ 分类管理
- ⚙️ 系统设置 · 📋 操作审计日志

### 🛵 骑手端
- 配送闭环：待接单 → 接单 → 取货 → 配送中 → 送达
- **💬 与客人对话**
- 💰 收入统计：总收入/今日收入/7日趋势
- 📬 消息通知 · 👤 个人信息

---

## 🏗️ 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 2.2.6 |
| ORM | MyBatis Plus 3.5.0 |
| 模板 | Thymeleaf |
| 数据库 | MySQL 8.0 / H2 (开发) |
| 图表 | ECharts 5 |
| 动画 | GSAP 3 + ScrollTrigger |
| AI | DeepSeek API |
| 地图 | Leaflet + OpenStreetMap |
| 安全 | BCrypt · XSS防护 · 状态机校验 |
| 部署 | Docker Compose |

---

## 🚀 快速开始

```bash
cd demo

# 开发环境（H2 内存数据库，零依赖）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境（Docker + MySQL）
docker-compose up -d
```

访问 http://localhost:8090

---

## 🔑 账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | vili | Vili1234! |
| 骑手 | rider1 | Rider1234! |

---

## 📂 结构

```
demo/
├── src/main/java/com/
│   ├── controller/    9 个 · 50+ API
│   ├── service/       业务逻辑 + 状态机
│   ├── mapper/        12 个 Mapper
│   ├── javaBean/      12 个实体
│   └── config/        拦截器 · 切面 · 安全
├── src/main/resources/
│   ├── templates/     40+ Thymeleaf 模板
│   ├── static/        CSS / JS / 图片
│   └── i18n/          中英文
├── docs/              路演 PPT
├── sql/               数据库脚本
└── pom.xml
```

---

## 🧪 测试

```bash
mvn test    # 236 用例
```

---

## 📄 许可证

MIT
