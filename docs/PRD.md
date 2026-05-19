# CampusCheckin 智课签 PRD

## 1. 项目背景

高校课程考勤是课堂管理中的常见场景。传统点名、纸质签到或课后人工统计方式效率较低，容易出现遗漏、代签、统计不及时等问题。学生也很难快速确认自己某节课的签到状态，教师则需要花费额外时间整理课程出勤情况。

CampusCheckin / 智课签 是一个基于 Android 的校园课程考勤签到 APP，面向大学课程设计 / Android 实训项目。项目通过 Android 客户端、Spring Boot 后端和 MySQL 8.0.45 数据库完成学生签到、教师发起签到和考勤统计闭环。

## 2. 项目目标

本项目要解决校园课堂考勤流程中签到不便、统计低效和状态不透明的问题。

项目目标包括：

- 为学生提供课程查看、课程表查看、课堂口令签到和签到记录查询能力。
- 为教师提供课程管理视图、发起签到任务和课程维度考勤统计能力。
- 使用 MySQL 保存核心业务数据，避免只停留在静态 mock 展示。
- 使用 Spring Boot 提供 REST API，Android 端通过接口访问真实数据。
- 使用简单清晰的技术架构，保证项目适合课程设计、实训演示和答辩讲解。

## 3. 项目定位

本项目定位为适合课程设计 / Android 实训演示的校园考勤系统，不是正式上线的商业系统。

第一阶段重点是完成真实数据库版核心闭环：登录认证、课程查询、周课表、签到任务、学生签到、签到记录和教师统计。项目应避免过度复杂的架构和非必要第三方能力，但需要体现真实数据库、前后端分离接口和基础鉴权能力。

## 4. 用户角色

- 学生：查看课程、查看周课表、进入课程详情、输入课堂口令签到、查看签到结果和个人签到记录。
- 教师：查看授课课程、发起签到任务、查看签到状态、查看课程维度考勤统计。

第一阶段不实现管理员角色。管理员后台、用户管理和课程维护可作为后续扩展。

## 5. 用户痛点

学生痛点：

- 不清楚当前课程是否已经开放签到。
- 课堂签到结果不透明，难以及时确认自己是否已签到、迟到或缺勤。
- 无法方便查看个人历史考勤记录。
- 传统纸质签到或点名流程耗时，体验较差。

教师痛点：

- 课堂点名占用教学时间。
- 纸质签到和手工统计效率低。
- 缺少课程维度的出勤数据汇总。
- 难以及时查看学生签到完成情况。
- 缺少可用于课程答辩展示的完整数字化考勤流程。

## 6. 核心功能范围

学生端功能：

- 学生登录。
- 查看课程列表。
- 查看周课表。
- 查看课程详情。
- 查看课程签到状态。
- 输入课堂口令并提交签到。
- 查看签到结果。
- 查看个人签到记录。
- 查看个人中心信息。

教师端功能：

- 教师登录。
- 查看教师课程列表。
- 发起课程签到任务。
- 设置课堂签到口令、开始时间和截止时间。
- 查看签到任务状态。
- 查看最近签到任务列表。
- 手动截止正在进行的签到任务。
- 查看课程维度考勤统计。

通用功能：

- 统一登录入口。
- 角色区分：学生 / 教师。
- SA-Token 登录鉴权。
- Android 调用 Spring Boot REST API。
- MySQL 8.0.45 保存业务数据。
- API 统一返回 `ApiResponse { code, message, data }`。

## 7. 第一阶段功能

第一阶段功能要求容易实现、适合 Java + XML + Spring Boot + MySQL 的课程项目。

第一阶段实现内容：

- 统一登录页，支持学生和教师登录。
- 后端使用 SA-Token 完成登录鉴权和 token 管理。
- 用户密码使用 BCrypt 加密存储。
- Android 端通过 Retrofit + Gson 调用后端接口。
- 学生首页展示今日课程、签到提醒和功能入口。
- 周课表页按周一到周五和节次展示课程。
- 课程详情页展示课程名称、教师、地点、上课时间和签到状态。
- 签到确认页支持输入课堂口令并提交签到。
- 签到结果页展示签到成功、迟到、缺勤或异常提示。
- 签到记录页展示学生个人历史签到记录。
- 教师首页展示授课课程和签到管理入口。
- 教师首页展示最近签到任务、任务状态和手动截止入口。
- 教师发起签到页支持通过日期时间选择器创建签到任务。
- 考勤统计页按课程展示应到、已到、迟到、缺勤和出勤率。
- 个人中心页展示当前用户基本信息和退出登录入口。

第一阶段主流程必须使用真实后端接口和 MySQL 数据库。Android 端可以保留少量 mock data 作为离线兜底或 UI 预览，但不能作为主数据源。

## 8. 后续扩展功能

后续可扩展功能包括：

- 二维码签到。
- 定位签到。
- 蓝牙 / Wi-Fi 签到。
- 管理员后台。
- 课程和用户管理。
- 消息通知。
- 考勤导出。
- 异常申诉。
- 学生维度统计。
- 签到记录筛选和搜索。
- Redis 缓存或分布式会话。
- 更完整的权限管理。

## 9. 页面清单

| 页面名称 | 使用角色 | 页面目的 | 核心内容 | 是否第一阶段实现 |
|---|---|---|---|---|
| 登录页 | 学生 / 教师 | 用户登录并区分角色 | 账号、密码、角色、登录按钮 | 是 |
| 学生首页 | 学生 | 展示学生主要功能入口 | 今日课程、签到提醒、课程入口 | 是 |
| 周课表页 | 学生 | 查看一周课程安排 | 周一到周五、节次、课程卡片 | 是 |
| 课程详情页 | 学生 | 查看课程信息和签到入口 | 课程信息、教师、地点、签到状态 | 是 |
| 签到确认页 | 学生 | 提交课堂签到 | 课堂口令输入、签到按钮 | 是 |
| 签到结果页 | 学生 | 展示签到结果 | 已签到、迟到、失败原因 | 是 |
| 签到记录页 | 学生 | 查看个人考勤历史 | 课程、时间、状态列表 | 是 |
| 教师首页 | 教师 | 展示教师课程和签到任务管理入口 | 最近签到任务、任务状态、手动截止、课程列表、发起签到入口 | 是 |
| 教师发起签到页 | 教师 | 创建签到任务 | 课程选择、日期时间选择器、课堂口令 | 是 |
| 考勤统计页 | 教师 | 查看课程考勤结果 | 应到、已到、迟到、缺勤、出勤率 | 是 |
| 个人中心页 | 学生 / 教师 | 查看用户信息 | 姓名、学号/工号、角色、退出登录 | 是 |
| 管理员页面 | 管理员 | 管理用户和课程 | 用户管理、课程管理 | 否 |
| 二维码签到页 | 学生 / 教师 | 扫码或展示二维码签到 | 二维码生成、扫码 | 否 |
| 定位签到页 | 学生 | 校验位置签到 | 定位权限、距离判断 | 否 |

## 10. 业务流程

学生签到流程：

1. 学生在登录页输入账号、密码并选择学生角色。
2. Android 调用后端登录接口，后端校验 MySQL 中的用户信息。
3. 登录成功后，后端通过 SA-Token 返回 token。
4. Android 保存 token，并在后续请求中携带 token。
5. 学生进入首页或周课表页，选择需要签到的课程。
6. Android 调用课程详情和签到任务接口，展示当前签到状态。
7. 如果课程处于可签到状态，学生进入签到确认页。
8. 学生输入教师提供的课堂口令并提交。
9. 后端校验 token、课程、签到任务、口令和时间规则。
10. 后端写入或更新 `check_in_records`。
11. Android 展示签到结果页。
12. 学生可在签到记录页查询历史考勤记录。

教师发起签到流程：

1. 教师在登录页输入账号、密码并选择教师角色。
2. Android 调用后端登录接口，后端校验教师身份并返回 token。
3. 教师进入教师首页，查看自己负责的课程。
4. 教师选择课程并进入发起签到页。
5. 教师设置签到口令，并通过日期时间选择器选择开始时间和截止时间。
6. Android 调用创建签到任务接口。
7. 后端写入 `check_in_tasks`。
8. 教师首页刷新最近签到任务，展示任务状态。
9. 学生端对应课程可以查询到可签到任务。
10. 如果课堂提前结束，教师可以在教师首页手动截止正在进行的签到任务。

查看考勤统计流程：

1. 教师进入教师首页。
2. 教师选择某门课程的考勤统计入口。
3. Android 调用课程统计接口。
4. 后端查询 `course_enrollments`、`check_in_tasks` 和 `check_in_records`。
5. 后端计算应到人数、已签到人数、迟到人数、缺勤人数和出勤率。
6. Android 使用卡片和列表展示课程维度统计结果。

## 11. 签到状态规则

| 状态 | 说明 |
|---|---|
| 未开始 | 当前时间早于签到任务开始时间，学生不能签到。 |
| 可签到 | 当前时间在签到开始时间和截止时间之间，学生可以提交签到。 |
| 已签到 | 学生已在有效时间内完成签到。 |
| 迟到 | 学生在截止时间后提交，或后端规则判断为迟到。 |
| 缺勤 | 签到截止后学生没有有效签到记录。 |
| 异常 | 口令错误、任务不存在、重复签到、角色不匹配或数据状态异常。 |
| 已取消 | 签到任务被取消，学生不能继续签到。 |

第一阶段签到方式为课堂口令 + 按钮确认。二维码、定位、蓝牙和 Wi-Fi 签到不在第一阶段实现。

## 12. 数据模型

### User

| 字段名 | 类型 | 说明 |
|---|---|---|
| userId | Long | 用户 ID |
| username | String | 登录账号 |
| passwordHash | String | BCrypt 加密后的密码 |
| role | String | 用户角色：STUDENT 或 TEACHER |
| status | String | 账号状态：ENABLED 或 DISABLED |
| createdAt | DateTime | 创建时间 |
| updatedAt | DateTime | 更新时间 |

### Student

| 字段名 | 类型 | 说明 |
|---|---|---|
| studentId | Long | 学生 ID |
| userId | Long | 关联 users 表 ID |
| studentNo | String | 学号 |
| name | String | 学生姓名 |
| className | String | 班级名称 |
| phone | String | 联系方式 |
| avatarText | String | 头像占位文字 |

### Teacher

| 字段名 | 类型 | 说明 |
|---|---|---|
| teacherId | Long | 教师 ID |
| userId | Long | 关联 users 表 ID |
| teacherNo | String | 教师工号 |
| name | String | 教师姓名 |
| department | String | 所属院系 |
| phone | String | 联系方式 |
| avatarText | String | 头像占位文字 |

### Course

| 字段名 | 类型 | 说明 |
|---|---|---|
| courseId | Long | 课程 ID |
| courseName | String | 课程名称 |
| teacherId | Long | 授课教师 ID |
| location | String | 上课地点 |
| weekDay | int | 星期几，1 表示周一 |
| section | String | 上课节次 |
| startTime | String | 上课开始时间 |
| endTime | String | 上课结束时间 |
| semester | String | 学期 |

### CourseEnrollment

| 字段名 | 类型 | 说明 |
|---|---|---|
| enrollmentId | Long | 选课关系 ID |
| courseId | Long | 课程 ID |
| studentId | Long | 学生 ID |
| enrolledAt | DateTime | 选课时间 |
| status | String | 选课状态 |

### CheckInTask

| 字段名 | 类型 | 说明 |
|---|---|---|
| taskId | Long | 签到任务 ID |
| courseId | Long | 关联课程 ID |
| title | String | 签到任务标题 |
| password | String | 课堂口令 |
| startTime | DateTime | 签到开始时间 |
| endTime | DateTime | 签到截止时间 |
| status | String | 任务状态：NOT_STARTED、ACTIVE、ENDED |
| createdBy | Long | 发起教师 ID |
| createdAt | DateTime | 创建时间 |

### CheckInRecord

| 字段名 | 类型 | 说明 |
|---|---|---|
| recordId | Long | 签到记录 ID |
| taskId | Long | 关联签到任务 ID |
| courseId | Long | 关联课程 ID |
| studentId | Long | 学生 ID |
| checkInTime | DateTime | 签到时间 |
| status | String | 签到状态：SIGNED、LATE、ABSENT、EXCEPTION |
| remark | String | 备注信息 |

后续应新增 `docs/schema.sql`，包含 `campus_checkin` 数据库建库、建表和初始化演示数据。

## 13. 非功能需求

易用性：

- 学生和教师入口清晰。
- 签到状态使用明确文字和颜色区分。
- 签到流程不超过三步。
- 教师发起签到表单字段保持简单。
- 教师发起签到时间通过日期时间选择器完成，避免手动输入格式错误。

可维护性：

- Android 端按 Activity、Adapter、Model、Network、Data 等包分层。
- Spring Boot 后端按 Controller、Service、Mapper、Entity、DTO 等包分层。
- SQL 表结构清晰，字段命名统一。
- API 返回结构统一，便于 Android 端处理成功和失败状态。

页面一致性：

- 遵守 `docs/vercel/DESIGN.md` 的当前视觉方向，并适配校园考勤场景。
- 使用 clean / card-based / Vercel style。
- 课程、签到任务和统计信息使用卡片式布局。
- 颜色、间距、文字应放入 Android 资源文件管理。

演示友好性：

- 支持通过初始化数据快速登录学生和教师账号。
- 支持在本地 MySQL 8.0.45 中导入演示数据。
- 支持 Android 模拟器访问本机 Spring Boot 后端。
- 核心流程可在答辩现场完整演示。

## 14. 技术实现说明

Android 客户端采用：

- Java 编写 Activity 和业务逻辑。
- XML Layout 编写页面布局。
- AppCompat 提供基础兼容能力。
- Material Components 提供按钮、输入框、卡片等 UI 组件。
- ConstraintLayout 负责页面布局。
- RecyclerView 展示课程列表、签到记录和统计列表。
- Retrofit + Gson 调用 Spring Boot REST API。
- Gradle Kotlin DSL 管理构建配置。

后端服务采用：

- Spring Boot 提供 REST API。
- Java 编写 Controller、Service、Mapper 和业务逻辑。
- Gradle Kotlin DSL 管理 server 模块。
- MyBatis 访问 MySQL。
- MySQL 8.0.45 保存核心业务数据。
- SA-Token 负责登录鉴权和 token 管理。
- BCrypt 负责密码加密和校验。
- Springdoc OpenAPI 提供动态 Swagger / OpenAPI 文档，方便 Apifox 导入和接口管理。

接口规则：

- REST API 统一前缀为 `/api/v1`。
- API 统一返回 `ApiResponse { code, message, data }`。
- Android 模拟器默认 baseUrl 为 `http://10.0.2.2:8081/api/v1/`。
- 后端本机浏览器、PowerShell、Apifox 测试可使用 `http://localhost:8081/api/v1/`。
- 动态 OpenAPI JSON 默认地址为 `http://localhost:8081/v3/api-docs`。
- Swagger UI 默认地址为 `http://localhost:8081/swagger-ui.html`。
- Android 不允许直连 MySQL，必须通过 Spring Boot 后端访问数据库。

## 15. 验收标准

项目完成后应达到以下效果：

- 可以启动 Spring Boot 后端并连接 MySQL 8.0.45。
- 可以通过初始化数据登录学生账号和教师账号。
- Android 端登录成功后能保存并携带 SA-Token 返回的 token。
- 学生可以查看课程列表、周课表和课程详情。
- 学生可以输入课堂口令完成签到。
- 学生签到后，后端能写入或更新 MySQL 中的签到记录。
- 学生可以查看个人签到记录。
- 教师可以查看自己的课程列表。
- 教师可以发起课程签到任务。
- 教师可以查看最近签到任务状态。
- 教师可以手动截止正在进行的签到任务。
- 教师可以查看课程维度考勤统计。
- 统计结果能体现应到、已到、迟到、缺勤和出勤率。
- 页面风格统一，符合校园考勤 APP 场景。
- 项目使用 Java + XML 实现 Android 页面，不使用 Jetpack Compose。
- 第一阶段不实现管理员端、二维码签到、定位签到、蓝牙/Wi-Fi 签到、消息推送和考勤导出。
