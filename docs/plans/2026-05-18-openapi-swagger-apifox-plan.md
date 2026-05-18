# 动态 OpenAPI / Swagger 接入计划

## 当前背景

后端已经具备登录、课程、签到、统计等 REST API，`docs/API.md` 提供了人工维护的接口说明。用户希望将接口导入 Apifox，要求提供动态 OpenAPI / Swagger，而不是只维护静态 Markdown 文档。

## 目标

接入 springdoc OpenAPI，让后端启动后自动生成 OpenAPI JSON/YAML，并提供 Swagger UI，方便 Apifox 通过 URL 导入和持续同步。

## 计划改动

- 增加 `springdoc-openapi-starter-webmvc-ui` 依赖，使用适配 Spring Boot 3.3.x 的 `2.6.0`。
- 新增 OpenAPI 全局配置，包含标题、描述、服务器地址和 Authorization header 鉴权方案。
- 给 Controller 增加接口分组、接口说明和鉴权标记。
- 给主要 DTO 增加字段说明和示例值。
- 更新 `DEPLOY.md`、`docs/API.md`、`README.md` 的 OpenAPI / Apifox 说明。

## 涉及文件

- `gradle/libs.versions.toml`
- `server/build.gradle.kts`
- `server/src/main/resources/application.yml`
- `server/src/main/java/cn/nyc1/campuscheckin/config/OpenApiConfig.java`
- `server/src/main/java/cn/nyc1/campuscheckin/controller/*Controller.java`
- `server/src/main/java/cn/nyc1/campuscheckin/dto/*.java`
- `server/src/main/java/cn/nyc1/campuscheckin/common/ApiResponse.java`
- `DEPLOY.md`
- `docs/API.md`
- `README.md`

## 验证方式

- 执行 `.\gradlew.bat :server:compileJava`。
- 执行 `.\gradlew.bat :server:bootJar`。
- 后端启动后访问 `http://localhost:8081/swagger-ui.html`。
- 后端启动后访问 `http://localhost:8081/v3/api-docs`。
- 在 Apifox 中使用 `http://localhost:8081/v3/api-docs` 导入。

## 当前状态

已完成实现，等待运行时验证。
