# Android Vercel UI 实施计划

## 当前背景

Android 前端已经具备 Retrofit 网络层、学生端部分页面和后端真实接口。用户确认采用 `docs/vercel/DESIGN.md` 的视觉方向，并要求按 Pencil 中导出的 Vercel 高保真稿继续执行。

## 目标

将 Android 前端切换为 Vercel 风格，补齐教师端、个人中心和第一阶段核心页面，使 App 可以编译运行，并通过 Spring Boot + MySQL 完成学生签到、教师发起签到和课程统计闭环。

## 计划改动

- 使用 Vercel 黑白视觉 token：近黑主按钮、白色细边框卡片、浅灰背景、蓝色状态/链接。
- 保留 Java + XML + Activity 跳转结构，不引入 Compose 或复杂架构。
- 补齐 `TeacherHomeActivity`、`CreateCheckInActivity`、`AttendanceStatsActivity`、`ProfileActivity`。
- 改造登录、学生首页、课程详情、教师首页、发起签到、考勤统计等 XML。
- Android baseUrl 与当前后端端口统一为 `http://10.0.2.2:8081/api/v1/`。

## 涉及文件

- `app/src/main/java/cn/nyc1/myapplication/activity/*`
- `app/src/main/res/layout/*`
- `app/src/main/res/drawable/*`
- `app/src/main/res/values/colors.xml`
- `DEPLOY.md`
- `docs/API.md`
- `AGENTS.md`

## 验证方式

- `.\gradlew.bat :app:compileDebugJavaWithJavac`
- `.\gradlew.bat :app:assembleDebug`
- 学生账号 `s20260001 / 123456` 登录、查看课程、签到。
- 教师账号 `t20260001 / 123456` 登录、发起签到、查看统计。

## 当前状态

已完成：Vercel 资源、核心 XML、缺失 Activity、8081 文档同步。

## 验证结果

- `.\gradlew.bat :app:compileDebugJavaWithJavac`：通过。
- `.\gradlew.bat :app:assembleDebug`：通过。
- Debug APK 输出：`app/build/outputs/apk/debug/app-debug.apk`。
