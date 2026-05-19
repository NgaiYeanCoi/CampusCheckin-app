# 文档同步：BaseUrl 与 Vercel 设计源

## 当前背景

Android 端实际生效配置已经是 `RetrofitClient.BASE_URL = http://10.0.2.2:8081/api/v1/`，后端当前联调端口为 `8081`。Android 前端视觉也已切换为 `docs/vercel/DESIGN.md` 的 Vercel 风格，但部分文档仍残留旧端口和 `docs/notion/DESIGN.md` 主视觉说明。

## 目标

同步 README、PRD、DEPLOY、API 和 AGENTS，使默认端口、Android 模拟器访问地址、Apifox / Swagger 入口和当前视觉设计源保持一致。

## 计划改动

- 将默认 Android 模拟器 baseUrl 统一为 `http://10.0.2.2:8081/api/v1/`。
- 将电脑端浏览器、PowerShell、Apifox 测试地址统一为 `http://localhost:8081/api/v1/`。
- 将当前主视觉设计源统一为 `docs/vercel/DESIGN.md`。
- 说明 `RetrofitClient.BASE_URL` 是当前 Android 运行时 baseUrl 来源。
- 保留 `docs/notion/DESIGN.md` 作为历史设计参考。

## 涉及文件

- `README.md`
- `DEPLOY.md`
- `AGENTS.md`
- `docs/PRD.md`
- `docs/API.md`

## 验证方式

- `rg "localhost:8080|10\\.0\\.2\\.2:8080" README.md DEPLOY.md AGENTS.md docs/PRD.md docs/API.md`
- `rg "docs/notion/DESIGN.md|notion/DESIGN" README.md AGENTS.md docs/PRD.md docs/API.md DEPLOY.md`

## 当前状态

已完成文档同步。8080 仅允许保留在可选端口或故障排查说明中，`docs/notion/DESIGN.md` 仅作为历史设计参考出现。
