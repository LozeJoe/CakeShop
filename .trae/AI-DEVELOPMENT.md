# AI 开发协作记录

## 使用的 AI 工具

| 工具 | 版本 | 模型 | 用途 |
|------|------|------|------|
| Hermes Agent | latest | DeepSeek v4 Pro | 主力 AI 编程助手 |
| Reasonix CLI | v0.53.2 | DeepSeek v4 Pro | 并行辅助开发 |

## AI 协作范围

### 需求分析与设计
- 功能模块方案设计
- 数据库表结构设计
- API 接口规划

### 代码开发
- Controller / Service / Mapper 层代码生成
- Thymeleaf 模板编写
- 前端 CSS / JS 优化

### 代码质量
- 批量添加 Javadoc 注释 (4.1% → 16.3%)
- i18n 国际化配置
- Bug 诊断与修复

### DevOps
- Dockerfile 编写与优化
- docker-compose.yml 容器编排
- 阿里云 ECS 部署脚本
- Maven 阿里云镜像配置

### 文档
- README.md 编写
- 考核文档 (docx) 生成
- 部署指南编写

## AI 对话记录

Hermes Agent 会话记录保存在本地 session DB 中，Reasonix CLI 的对话记录保存在 `.reasonix/` 目录下。

可通过以下方式查看：
- Hermes: `session_search` 查询
- Reasonix: `.reasonix/sessions/` 目录

## MCP 配置

通过 Windows-MCP 连接本地桌面环境，实现：
- 浏览器自动化操作
- 文件系统管理
- 进程管理
- 截图与 UI 检测

## 技能系统

项目相关的 AI 技能配置：
- `cakeshop-deploy`: CakeShop 部署流程
- `loze-java-dev`: Java 开发工作流
- `loze-multi-agent`: 多 Agent 并行协作
