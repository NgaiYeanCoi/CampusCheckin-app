# 教师端签到明细与实时刷新计划

## 当前背景

教师端 `Recent tasks` 只能看到任务状态，不能点击查看某一次签到的学生明细；课程统计页卡片只有数字，没有明确标签，用户不清楚数字含义。用户要求对标学习通，让教师能看到实时签到的人是谁。

## 目标

新增签到任务详情页和后端详情接口，使教师能点击某次签到任务查看统计汇总、已签 / 未签 / 缺勤学生名单，并支持手动刷新和 10 秒自动刷新。

## 计划改动

- 新增后端接口 `GET /api/v1/teacher/check-in-tasks/{taskId}/detail`。
- 新增任务详情 DTO、统计汇总 DTO、学生明细 DTO。
- Android 新增 `CheckInTaskDetailActivity`、学生明细 Adapter 和详情页布局。
- 教师首页任务卡支持点击进入详情，并显示已签 / 应到摘要。
- 优化课程统计卡片，让数字带上 `应到`、`已签`、`迟到`、`缺勤` 标签。
- 同步更新 `docs/API.md`、`docs/PRD.md` 和 `DEPLOY.md`。

## 涉及文件

- `server/src/main/java/cn/nyc1/campuscheckin/controller/TeacherController.java`
- `server/src/main/java/cn/nyc1/campuscheckin/service/AttendanceService.java`
- `server/src/main/java/cn/nyc1/campuscheckin/mapper/AttendanceStatsMapper.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/CheckInTaskDetailActivity.java`
- `app/src/main/java/cn/nyc1/myapplication/activity/TeacherHomeActivity.java`
- `docs/API.md`
- `docs/PRD.md`
- `DEPLOY.md`

## 验证方式

- 编译后端和 Android。
- 教师登录后创建签到任务。
- 调用任务详情接口，确认返回应到学生列表。
- 学生签到后刷新详情，确认学生状态变化。
- 教师手动截止后，未签到学生显示缺勤。

## 当前状态

已完成。

## 2026-05-21 后续修复

- 后端全局异常处理器补充日志输出，接口返回 `服务器内部错误` 时，控制台和 `logs/campus-checkin-server.log` 能看到完整异常堆栈。
- 教师签到任务详情页增加加载失败状态，接口异常时不再显示空白统计卡片。
- `DEPLOY.md` 补充后端日志文件、8081 旧进程检查和重启说明。
