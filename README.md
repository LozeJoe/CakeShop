# 🍰 CakeShop 蛋糕在线商城

> **法式烘焙 · 匠心手作 · 一键送达**  
> 学号：2400130326 | 姓名：刘哲凯 | 班级：软件技术3班

基于 Spring Boot + Thymeleaf + MyBatis Plus 的全功能蛋糕电商系统，覆盖用户选购、管理员运营、骑手配送三大核心场景。

## ✨ 核心功能

| 角色 | 功能 |
|------|------|
| 🛒 用户 | 注册登录、商品浏览搜索、购物车、下单支付、订单追踪、评价收藏、AI客服、地图定位 |
| 🔧 管理员 | 数据仪表盘(ECharts)、用户/商品/订单/分类管理、系统设置、操作日志、库存预警、国际化 |
| 🛵 骑手 | 独立注册、接单配送、收入统计、消息通知、与用户实时对话、移动端适配 |

## 🏗️ 技术栈

- **后端**: Spring Boot 2.2.6 + MyBatis Plus 3.5.0 + MySQL 8.0
- **前端**: Thymeleaf + GSAP + ECharts + Leaflet 地图
- **安全**: BCrypt 密码加密 + 登录拦截器 + 角色权限分级
- **AI**: DeepSeek API 智能客服 "小甜"
- **部署**: Docker Compose + 阿里云 ECS
- **辅助**: AI 协作开发 (Hermes Agent + Reasonix CLI)

## 🚀 快速开始

```bash
# 1. 启动 MySQL
# 2. 编译运行
cd demo
mvn spring-boot:run
# 3. 访问 http://localhost:8080
```

## 🌐 线上地址

http://112.124.53.155:8090

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 用户 | vili | Vili1234! |
| 骑手 | rider1 | Rider1234! |

## 📁 项目结构

```
CakeShop/
├── demo/                   # Spring Boot 主项目
│   ├── src/main/java/      # Java 源码 (94个文件, 7,000+行)
│   ├── src/main/resources/ # 配置、模板、SQL、i18n
│   ├── docs/               # 项目展示文档
│   ├── sql/                # 数据库初始化脚本
│   ├── Dockerfile          # Docker 镜像构建
│   └── docker-compose.yml  # 容器编排
├── screenshots/            # 系统截图
├── 3班2400130326刘哲凯.docx # 考核文档
└── README.md               # 本文件
```

## 📊 考核信息

- 代码注释覆盖率: 16.3% (≥15% ✅)
- 考核文档字数: 4,900+ (≥3,000 ✅)
- Java 文件数: 94 个
- 功能模块: 30+
- 数据库表: 10 张
