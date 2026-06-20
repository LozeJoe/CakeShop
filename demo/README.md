# 🍰 CakeShop 蛋糕在线商城

> **法式烘焙 · 匠心手作 · 一键送达**

CakeShop 是一个基于 **Spring Boot + Thymeleaf + MyBatis Plus** 的全功能蛋糕在线商城系统。三端协同：**用户端**（选购下单）、**管理后台**（数据运营）、**骑手端**（接单配送），覆盖电商全链路。

---

## ✨ 功能概览

### 🛒 用户端
- 商品浏览：分类筛选、关键词搜索、热销排行、新品上市
- 商品详情：多图展示、价格库存、收藏、评价
- 购物车：增删改查、库存校验、合并去重
- 订单管理：下单 → 支付 → 配送追踪，六状态流转
- 评价系统：1-5 星评分 + 文字评价
- 收藏系统、个人中心、地址管理
- **💬 联系骑手**：配送中可与骑手实时对话
- 🤖 **AI 客服「小甜」**：DeepSeek 驱动，智能问答

### 🔧 管理后台
- **📊 数据仪表盘**：8 KPI 卡片 + 6 ECharts 图表，30s 自动刷新
- 👥 用户管理：CRUD、审核/冻结/解冻、角色分配
- 🎂 商品管理：多图上传、库存管理、分类树
- 📦 订单管理：状态筛选、配送费设置、状态流转控制
- 🛵 骑手管理、🏷️ 分类管理、⚙️ 系统设置
- 📋 操作审计日志 + 定时清理

### 🛵 骑手端
- 配送闭环：待接单 → 接单 → 取货 → 配送中 → 送达
- **💬 与客人对话**：配送中与用户实时聊天
- 💰 收入统计：总收入/今日收入/7日趋势
- 📬 消息通知：系统消息 + 已读标记
- 👤 个人信息编辑

---

## 🏗️ 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 2.2.6 |
| ORM | MyBatis Plus 3.5.0 |
| 模板 | Thymeleaf |
| 数据库 | MySQL 8.0 / H2（开发环境） |
| 前端 | HTML5 + CSS3 + jQuery |
| 动画 | GSAP 3 + ScrollTrigger |
| 图表 | ECharts 5 |
| 地图 | Leaflet + OpenStreetMap |
| AI | DeepSeek API |
| 安全 | BCrypt 加密、XSS 防护、状态机校验 |
| 部署 | Docker Compose 一键启动 |

---

## 🚀 快速开始

### 环境要求
- JDK 8+
- Maven 3.5+

### 本地运行（H2 内存数据库，零依赖）

```bash
cd demo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

访问 http://localhost:8090

### Docker 部署（MySQL）

```bash
cd demo
docker-compose up -d
```

---

## 🔑 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | vili | Vili1234! |
| 骑手 | rider1 | Rider1234! |

---

## 📂 项目结构

```
demo/
├── src/main/java/com/
│   ├── controller/    # 9 个 Controller，50+ API
│   ├── service/       # 业务逻辑 + 状态机
│   ├── mapper/        # 12 个 Mapper
│   ├── javaBean/      # 12 个实体类
│   └── config/        # 拦截器、切面、安全配置
├── src/main/resources/
│   ├── templates/     # 40+ Thymeleaf 模板
│   ├── static/        # CSS/JS/图片/字体
│   └── i18n/          # 中英文国际化
├── sql/               # 数据库脚本
├── docker-compose.yml
└── pom.xml
```

---

## 🧪 测试

```bash
mvn test    # 236 个自动化测试
```

---

## 📄 许可证

MIT License
