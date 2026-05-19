# 教师端签到体验修复计划

## 当前背景

教师端发起签到页原先使用手动文本输入开始/截止时间，右上角个人中心入口显示为空白胶囊，创建签到任务后教师首页看不到刚创建的任务状态，也不能手动截止正在进行的签到任务。

## 目标

补齐教师端“创建任务 -> 查看状态 -> 手动截止 -> 查看统计”的第一阶段闭环，同时修复首页右上角入口和发起签到页时间选择体验。

## 计划改动

- 学生首页和教师首页右上角入口改为圆形头像按钮。
- 发起签到页使用 Android 原生日期选择器和时间选择器，不再手动输入时间文本。
- 后端新增教师签到任务列表接口和手动截止接口。
- 教师首页新增最近签到任务列表，展示任务状态并支持手动截止。
- 同步 `docs/API.md`、`docs/PRD.md`、`DEPLOY.md`。

## 涉及文件

- `server/src/main/java/cn/nyc1/campuscheckin/controller/TeacherController.java`
- `server/src/main/java/cn/nyc1/campuscheckin/service/CheckInService.java`
- `server/src/main/java/cn/nyc1/campuscheckin/mapper/CheckInTaskMapper.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/TeacherHomeActivity.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/CreateCheckInActivity.java`
- `app/src/main/res/layout/activity_teacher_home.xml`
- `app/src/main/res/layout/activity_create_check_in.xml`
- `docs/API.md`
- `docs/PRD.md`
- `DEPLOY.md`

## 验证方式

- `.\gradlew.bat :server:compileJava`
- `.\gradlew.bat :server:bootJar`
- `.\gradlew.bat :app:compileDebugJavaWithJavac`
- `.\gradlew.bat :app:assembleDebug`
- 教师登录后创建签到任务、查看任务列表、手动截止任务。

## 当前状态

已完成。

## 验证结果

- `.\gradlew.bat :server:compileJava`：通过。
- `.\gradlew.bat :server:bootJar`：通过。
- `.\gradlew.bat :app:compileDebugJavaWithJavac`：通过。
- `.\gradlew.bat :app:assembleDebug`：通过。
- 临时启动后端到 `18081` 后完成教师登录、查询任务、创建签到任务和手动截止任务验证。
