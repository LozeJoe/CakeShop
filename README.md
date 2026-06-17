# 🍰 CakeShop 蛋糕在线商城

> **法式烘焙 · 匠心手作 · 一键送达**

基于 Spring Boot + Thymeleaf + MyBatis 的全功能蛋糕在线商城系统，包含用户端、管理后台和骑手端三大模块。

---

## ✨ 核心功能

| 模块 | 功能 |
|------|------|
| 🛒 **用户端** | 商品浏览/搜索、购物车、下单支付、订单管理、评价收藏、AI智能客服 |
| 🔧 **管理后台** | ECharts 仪表盘、用户/商品/订单/分类管理、系统设置、操作日志 |
| 🛵 **骑手端** | 接单/配送、收入统计、消息通知、个人中心 |
| 📊 **236 个自动化测试** | 单元测试 + 集成测试 + Controller 测试 + E2E 闭环测试，全部通过 |

---

## 🚀 快速开始

```bash
# 克隆
git clone https://github.com/LozeJoe/CakeShop.git
cd CakeShop/demo

# Docker 部署（推荐）
docker-compose up -d
# 访问 http://localhost:8090

# 或本地运行
mvn spring-boot:run
```

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin |
| 普通用户 | vili | 123 |

---

## 📁 项目结构

```
CakeShop/
├── demo/                  # Spring Boot 项目（核心）
│   ├── src/main/          # Java 后端 + 前端模板
│   ├── src/test/          # 236 个自动化测试
│   ├── screenshots/       # 核心功能截图
│   ├── docs/              # 路演演示文稿
│   ├── sql/init.sql       # 数据库初始化脚本
│   ├── Dockerfile         # Docker 部署
│   ├── docker-compose.yml
│   └── README.md          # 项目详细文档
└── 项目上线操作.md         # 部署指南
```

---

## 🏗️ 技术栈

**后端:** Java 8, Spring Boot 2.2.6, MyBatis, MySQL, Maven  
**前端:** Thymeleaf, HTML5/CSS3, JavaScript, GSAP 动画  
**工具:** Docker, ECharts, Leaflet 地图, DeepSeek AI  
**测试:** 29 套件 / 236 用例（JUnit 5 + Mockito + Spring MockMvc）

---

## 📄 文档

- [项目详细文档](demo/README.md) — 完整功能列表、迭代规划、推广计划
- [路演演示文稿](demo/docs/cakeshop-roadshow.html) — 22 页 HTML 版 PPT（← → 翻页，T 换主题）
- [项目上线操作.md](项目上线操作.md) — Docker 部署 & 阿里云上线指南
- [截图预览](demo/screenshots/) — 10 张核心功能界面截图

---

<p align="center">🍰 用匠心做每一款蛋糕，用代码写好每一行程序</p>
