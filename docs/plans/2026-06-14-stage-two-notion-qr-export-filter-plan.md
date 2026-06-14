# 第二阶段 Notion UI + QR / 导出 / 筛选计划

## 任务标题

第二阶段：Notion 风格重构 + 静态二维码签到 + CSV 导出 + 签到记录筛选

## 当前背景

第一阶段 Android + Spring Boot + MySQL 主闭环已经覆盖登录、课程、签到任务、学生签到、教师任务详情和课程统计。当前第二阶段在不引入复杂架构的前提下增强演示效果。

## 目标

- 将 Android 当前主视觉切换为 `docs/notion/DESIGN.md`。
- 支持教师创建静态二维码签到任务，学生扫码提交签到。
- 支持教师导出课程考勤统计 CSV。
- 支持学生按课程、状态和日期筛选签到记录。

## 计划改动

- 后端：扩展 `check_in_tasks` 的 `check_in_type`、`qr_token` 字段，更新 DTO、Mapper、Service 和 Controller。
- Android：新增扫码页、二维码生成展示、创建签到类型选择、记录筛选控件、CSV 导出分享入口。
- 文档：同步 `docs/schema.sql`、`docs/API.md`、`DEPLOY.md`、`README.md`、`docs/PRD.md`、`AGENTS.md`。

## 涉及文件

- Android：`app/src/main/java/cn/nyc1/myapplication`、`app/src/main/res`
- 后端：`server/src/main/java/cn/nyc1/campuscheckin`
- 文档：`docs/schema.sql`、`docs/API.md`、`DEPLOY.md`、`README.md`、`docs/PRD.md`、`AGENTS.md`

## 验证方式

- 已通过：`.\gradlew.bat :server:compileJava :app:assembleDebug`
- 已通过：`.\gradlew.bat :server:test :app:testDebugUnitTest`
- 使用 `DEPLOY.md` 中的二阶段接口命令验证 QR 签到、记录筛选和 CSV 导出。

## 当前状态

已完成。后端当前无测试源码，`server:test` 结果为 `NO-SOURCE`；真实 MySQL 接口冒烟需按 `DEPLOY.md` 重新导入 `docs/schema.sql` 后执行。
