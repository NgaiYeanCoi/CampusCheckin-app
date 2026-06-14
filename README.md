# CampusCheckin / 智课签

CampusCheckin / 智课签 是一个基于 Android 的校园课程考勤签到 APP，面向大学课程设计 / Android 实训项目。

项目采用 Android 原生客户端、Spring Boot 后端和 MySQL 8.0.45 数据库，实现学生签到、教师发起签到和课程维度考勤统计闭环。

当前 Android 前端视觉采用 `docs/notion/DESIGN.md` 的 Notion 风格：紫色主操作、深 navy 重点区域、暖灰背景、12dp 卡片和柔和状态色。

## 项目定位

- 适合大学课程设计、毕业设计雏形或 Android 实训演示。
- 第一阶段实现真实数据库和后端接口，不使用纯 mock 数据作为主流程。
- 功能范围聚焦校园课程考勤，避免复杂微服务、管理员后台、二维码、定位、蓝牙 / Wi-Fi 等非第一阶段内容。

## 核心功能

学生端：

- 学生登录
- 查看课程列表
- 查看周课表
- 查看课程详情
- 输入课堂口令签到
- 查看签到结果
- 查看签到记录
- 扫描二维码签到
- 按课程、状态和日期筛选签到记录

教师端：

- 教师登录
- 查看授课课程
- 发起签到任务
- 查看课程维度考勤统计
- 创建二维码签到任务
- 导出课程考勤统计 CSV

## 技术栈

Android 客户端：

- Java
- XML Layout
- AppCompat
- Material Components
- ConstraintLayout
- RecyclerView
- Retrofit
- Gson
- Gradle Kotlin DSL

后端：

- Java
- Spring Boot
- MyBatis
- SA-Token
- BCrypt
- MySQL 8.0.45
- Gradle Kotlin DSL

## 项目结构

```text
CampusCheckin/
├─ app/                 Android 客户端
├─ server/              Spring Boot 后端
├─ docs/
│  ├─ PRD.md            产品需求文档
│  ├─ API.md            REST API 接口文档
│  ├─ schema.sql        MySQL 建表和演示数据
│  ├─ notion/DESIGN.md  当前视觉设计参考
│  ├─ vercel/DESIGN.md  历史视觉设计参考
│  └─ plans/            计划模式记录
├─ AGENTS.md            Codex 协作和代码规则
├─ DEPLOY.md            部署和运行说明
└─ README.md            项目说明
```

## 文档入口

- 产品需求：[docs/PRD.md](docs/PRD.md)
- 接口文档：[docs/API.md](docs/API.md)
- 部署运行：[DEPLOY.md](DEPLOY.md)
- Android 模拟器后端地址：`http://10.0.2.2:8081/api/v1`
- 电脑端后端测试地址：`http://localhost:8081/api/v1`
- 动态 OpenAPI：后端启动后访问 `http://localhost:8081/v3/api-docs`
- Swagger UI：后端启动后访问 `http://localhost:8081/swagger-ui.html`
- 数据库脚本：[docs/schema.sql](docs/schema.sql)
- 协作规则：[AGENTS.md](AGENTS.md)
- 当前设计参考：[docs/notion/DESIGN.md](docs/notion/DESIGN.md)
- 历史设计参考：[docs/vercel/DESIGN.md](docs/vercel/DESIGN.md)

## 演示账号

所有演示账号密码均为：

```text
123456
```

学生账号：

```text
s20260001
s20260002
s20260003
s20260004
```

教师账号：

```text
t20260001
t20260002
```

详细启动和接口测试步骤见 [DEPLOY.md](DEPLOY.md)。
