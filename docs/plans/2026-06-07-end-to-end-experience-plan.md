# CampusCheckin 完整闭环体验完善计划

## 当前背景

`docs/PRD.md` v1.3 已明确统一响应体 `code=200` 表示成功，并要求第一阶段能跑通教师发起签到、学生首页聚合展示、学生签到、教师详情和统计刷新的真实数据库闭环。

当前代码已具备主要页面、接口和 MySQL 数据结构，但成功码仍有 `code=0` 遗留，通用课程接口缺少按当前登录角色的课程访问校验，部分 Android 页面在接口失败时展示离线预览数据，容易干扰答辩演示。

## 目标

- 后端、Android 和人工文档统一使用 `code=200` 判断请求成功。
- 通用课程详情和课程当前签到任务接口按学生选课关系、教师授课关系做权限校验。
- Android 真实业务闭环失败时明确提示错误，不把预览数据当作真实数据展示。
- 支持按 PRD 验收脚本演示完整闭环。

## 计划改动

- 修改后端 `ApiResponse.success()` 成功码和 OpenAPI schema 描述。
- 修改 Android `ApiResponse.isSuccess()` 判断逻辑。
- 在 `CourseService` 增加课程访问校验，`CourseController` 查询当前签到任务前复用校验。
- 调整教师创建签到页、教师首页任务/课程列表、签到记录页、统计页的接口失败态。
- 同步 `docs/API.md` 和 `DEPLOY.md` 中的成功响应、字段说明和日志示例。

## 涉及文件

- `server/src/main/java/cn/nyc1/campuscheckin/common/ApiResponse.java`
- `server/src/main/java/cn/nyc1/campuscheckin/service/CourseService.java`
- `server/src/main/java/cn/nyc1/campuscheckin/controller/CourseController.java`
- `app/src/main/java/cn/nyc1/myapplication/network/ApiResponse.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/CreateCheckInActivity.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/TeacherHomeActivity.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/CheckInRecordActivity.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/AttendanceStatsActivity.java`
- `docs/API.md`
- `DEPLOY.md`

## 验证方式

- `.\gradlew.bat :server:compileJava`
- `.\gradlew.bat :app:compileDebugJavaWithJavac`
- 后端启动到 `8081` 后，用演示账号验证教师发起签到、学生首页可见、学生提交签到、教师详情刷新、教师手动截止和统计刷新。
- 验证未登录访问课程详情返回 `401`，学生访问未选课程和教师访问非本人课程返回业务失败。

## 当前状态

已完成。

## 验证结果

- `.\gradlew.bat :server:compileJava :app:compileDebugJavaWithJavac`：通过。
- 首次在沙箱内运行 Gradle 被用户目录 `.gradle` wrapper 锁文件权限拦截；授权沙箱外运行后通过。
- 未执行本地 MySQL 重置和接口联调，避免在未确认当前数据库状态时覆盖演示库数据。
