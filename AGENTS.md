# AGENTS.md

## Project Role

Codex 在本项目中扮演 Android + Spring Boot 开发助手，负责根据 PRD、设计文档和本文件协助实现 CampusCheckin / 智课签。

Codex 后续工作必须优先保证项目适合大学课程设计 / Android 实训演示。实现应清晰、可运行、便于答辩讲解，不应自由扩展未经 PRD 确认的功能。

## Project Summary

项目名称：CampusCheckin / 智课签。

项目主题：基于 Android 的校园课程考勤签到 APP 设计与实现。

项目目标：使用 Android 原生客户端、Spring Boot 后端和 MySQL 8.0.45 数据库，实现学生签到、教师发起签到和课程维度考勤统计闭环。

第一阶段主流程使用真实数据库和后端接口。mock data 仅允许作为离线兜底或 UI 预览，不作为主数据源。

## Tech Stack

Android 客户端必须使用：

- Java
- XML Layout
- AppCompat
- Material Components
- ConstraintLayout
- RecyclerView
- Retrofit
- Gson
- Gradle Kotlin DSL

后端必须使用：

- Java
- Spring Boot
- Gradle Kotlin DSL
- MyBatis
- SA-Token
- BCrypt
- MySQL 8.0.45

明确禁止：

- Jetpack Compose
- Flutter
- React Native
- Android 直连 MySQL
- 复杂微服务架构
- 第一阶段 Redis 强依赖
- 第一阶段二维码、定位、蓝牙或 Wi-Fi 签到
- 不必要第三方库

SA-Token 参考项目：

- `https://github.com/dromara/Sa-Token`

## Important Files

每次开发前应优先阅读：

- `README.md`
- `DEPLOY.md`
- `docs/API.md`
- `docs/PRD.md`
- `docs/notion/DESIGN.md`
- `docs/schema.sql`
- `AGENTS.md`

说明：

- 当前仓库中的设计文件路径是 `docs/notion/DESIGN.md`。
- 如果后续新增或移动为 `docs/DESIGN.md`，则优先阅读 `docs/DESIGN.md`。
- `docs/PRD.md` 决定功能范围。
- `docs/notion/DESIGN.md` 决定视觉方向。
- `docs/schema.sql` 决定数据库结构和演示数据。
- `README.md` 决定项目介绍和文档入口。
- `DEPLOY.md` 决定部署、运行、数据库连接和接口测试方式。
- `docs/API.md` 决定 REST API 的路径、请求体、响应体和字段含义。
- `AGENTS.md` 决定代码规则和协作规则。
- 修改数据库、后端配置或接口后，必须同步更新相关文档。

## Development Rules

- 不要自由发挥功能范围。
- `docs/PRD.md` 决定第一阶段要做什么功能。
- `docs/notion/DESIGN.md` 决定视觉方向。
- `AGENTS.md` 决定代码结构和实现规则。
- 不要把 Android 项目改成 Jetpack Compose。
- 不要引入复杂架构。
- 不要让 Android 直接连接 MySQL。
- 第一阶段主流程必须通过 Spring Boot REST API 访问 MySQL。
- mock data 只能作为离线兜底或 UI 预览。
- 页面之间使用 Activity 跳转即可。
- 优先保证课程设计 / 实训项目容易理解、容易运行、容易答辩。
- Android 当前工程包名沿用 `cn.nyc1.myapplication`。
- 后续新增后端模块时，server 包名使用 `cn.nyc1.campuscheckin`。
- 修改后端接口、数据库连接配置、运行命令或演示账号后，必须同步更新 `DEPLOY.md`。
- 修改后端接口路径、请求字段、响应字段或业务状态后，必须同步更新 `docs/API.md`。
- 修改表结构或初始化数据后，必须同步更新 `docs/schema.sql`。

## Android Code Rules

- Activity 使用 Java 编写。
- 页面使用 XML Layout 编写。
- `colors.xml` 管理颜色。
- `dimens.xml` 管理间距和尺寸。
- `strings.xml` 管理文本。
- `drawable` 管理圆角背景、卡片背景和状态标签背景。
- 使用 RecyclerView 展示课程列表、签到记录和考勤列表。
- 使用 Retrofit + Gson 调用后端 REST API。
- 网络 baseUrl 必须集中配置，不要散落在 Activity 中。
- Android 模拟器默认 baseUrl 使用 `http://10.0.2.2:8080/api/v1/`。
- 后端本机浏览器测试可使用 `http://localhost:8080/api/v1/`。
- 登录成功后保存后端返回的 token，并在后续请求中携带。
- 避免把大量业务数据硬编码在 Activity 中。
- Activity 负责页面展示、事件处理和页面跳转。
- Adapter 负责 RecyclerView item 绑定。
- model 类只负责数据结构。
- network 包负责 API service、请求 DTO、响应 DTO 和 Retrofit 配置。
- utils 只放通用工具方法。

## Backend Code Rules

- 后端使用 Spring Boot。
- 后端模块建议命名为 `server`。
- 后端包名使用 `cn.nyc1.campuscheckin`。
- 持久层使用 MyBatis。
- 认证使用 SA-Token。
- 密码使用 BCrypt 加密存储和校验。
- 数据库使用 MySQL 8.0.45。
- REST API 统一前缀为 `/api/v1`。
- API 统一返回 `ApiResponse { code, message, data }`。
- Controller 只处理请求参数、响应封装和简单校验。
- Service 处理业务逻辑。
- Mapper 负责数据库访问。
- Entity 对应数据库表。
- DTO 用于接口入参和出参。
- 不要在 Controller 中直接写 SQL。
- 不要把数据库账号密码硬编码在 Java 代码中。

## Package Structure

Android 当前包名使用：

```text
cn.nyc1.myapplication
├─ activity
├─ adapter
├─ model
├─ network
├─ data
└─ utils
```

Android 包职责：

- `activity`：登录页、学生端页面、教师端页面。
- `adapter`：课程列表、签到记录、统计列表等 RecyclerView Adapter。
- `model`：Student、Teacher、Course、CheckInTask、CheckInRecord 等数据模型。
- `network`：RetrofitClient、ApiService、请求 DTO、响应 DTO。
- `data`：离线兜底数据或 UI 预览数据。
- `utils`：时间、状态、格式化等辅助方法。

后端建议包结构：

```text
cn.nyc1.campuscheckin
├─ controller
├─ service
├─ mapper
├─ entity
├─ dto
├─ config
├─ common
└─ utils
```

后端包职责：

- `controller`：REST API 控制器。
- `service`：业务逻辑。
- `mapper`：MyBatis 数据访问。
- `entity`：数据库实体。
- `dto`：接口请求和响应对象。
- `config`：SA-Token、跨域、MyBatis 等配置。
- `common`：ApiResponse、错误码、常量。
- `utils`：密码、时间、状态等工具。

## Page Naming Rules

Activity 命名规则：

- `LoginActivity`
- `StudentHomeActivity`
- `ScheduleActivity`
- `CourseDetailActivity`
- `CheckInConfirmActivity`
- `CheckInResultActivity`
- `CheckInRecordActivity`
- `TeacherHomeActivity`
- `CreateCheckInActivity`
- `AttendanceStatsActivity`
- `ProfileActivity`

XML 命名规则：

- `activity_login.xml`
- `activity_student_home.xml`
- `activity_schedule.xml`
- `activity_course_detail.xml`
- `activity_check_in_confirm.xml`
- `activity_check_in_result.xml`
- `activity_check_in_record.xml`
- `activity_teacher_home.xml`
- `activity_create_check_in.xml`
- `activity_attendance_stats.xml`
- `activity_profile.xml`

RecyclerView item 命名规则：

- `item_course.xml`
- `item_schedule_course.xml`
- `item_check_in_record.xml`
- `item_attendance_stat.xml`

Drawable 命名规则：

- `bg_card.xml`
- `bg_button_primary.xml`
- `bg_status_success.xml`
- `bg_status_warning.xml`
- `bg_status_error.xml`
- `bg_status_neutral.xml`

## API Rules

- REST API 统一前缀：`/api/v1`。
- 返回结构统一为 `ApiResponse { code, message, data }`。
- 后端必须保持动态 OpenAPI 可用，Apifox 导入地址为运行时的 `/v3/api-docs`。
- 登录接口返回 token、用户基础信息和角色。
- Android 端不得依赖中文 message 判断业务逻辑。
- 业务判断应依赖 code、状态字段或枚举字段。
- token 应通过请求头传递。
- 第一阶段核心接口至少覆盖：
  - 登录
  - 获取当前用户信息
  - 获取课程列表
  - 获取周课表
  - 获取课程详情
  - 获取当前签到任务
  - 学生提交签到
  - 学生查询签到记录
  - 教师发起签到
  - 教师查询课程考勤统计

## Database Rules

- 数据库版本：MySQL 8.0.45。
- 数据库名：`campus_checkin`。
- 后续应新增 `docs/schema.sql`。
- `docs/schema.sql` 应包含建库、建表和初始化演示数据。
- 表名使用小写下划线。
- 主键建议使用 BIGINT。
- 时间字段建议使用 DATETIME。
- 用户密码字段保存 BCrypt hash，不保存明文密码。
- 第一阶段至少包含以下表：
  - `users`
  - `students`
  - `teachers`
  - `courses`
  - `course_enrollments`
  - `check_in_tasks`
  - `check_in_records`

## UI Rules

- 遵守 `docs/notion/DESIGN.md` 的视觉方向，并适配校园考勤 APP。
- 使用 clean / card-based / campus style。
- 不复制任何品牌 Logo、商标或专有插画。
- 页面应适合 Android 手机屏幕。
- 课程、签到任务、签到记录、统计信息优先使用卡片式布局。
- 状态必须明确，例如未开始、可签到、已签到、迟到、缺勤、异常。
- 主要按钮使用 MaterialButton。
- 输入课堂口令使用 TextInputLayout / TextInputEditText。
- 列表使用 RecyclerView。
- 页面间距、圆角、颜色应保持一致。
- 不要制作复杂动画。
- 不要使用过度装饰性的 UI。

## Mock Data Rules

- 第一阶段主流程不使用 mock data。
- mock data 只允许作为离线兜底或 UI 预览。
- 如果后端不可用，Android 可以展示少量预置课程或提示页，不能伪装成真实写库成功。
- 学生、教师、课程、签到任务、签到记录的真实数据应来自 MySQL。
- 教师发起签到后，应通过后端写入 `check_in_tasks`。
- 学生签到后，应通过后端写入或更新 `check_in_records`。

## Plan Record Rules

- 仅当任务处于计划模式、用户明确要求先做计划、或需要输出 `<proposed_plan>` 级别的正式实施计划时，才必须在 `docs/plans/` 下创建或更新计划记录。
- 普通问答、简单解释、单步修复、环境检查、直接执行型小任务不强制创建计划文件。
- 计划文件命名规则：

```text
docs/plans/YYYY-MM-DD-task-name-plan.md
```

- `YYYY-MM-DD` 使用当前日期，例如 `2026-05-17`。
- `task-name` 使用本次对话或任务的简短英文 / 拼音 / kebab-case 描述。
- 示例：

```text
docs/plans/2026-05-17-server-api-integration-plan.md
docs/plans/2026-05-17-android-login-page-plan.md
docs/plans/2026-05-17-database-schema-update-plan.md
```

- 计划记录至少包含：
  - 任务标题
  - 当前背景
  - 目标
  - 计划改动
  - 涉及文件
  - 验证方式
  - 当前状态
- 如果后续对同一计划任务继续讨论或开发，应优先更新已有计划文件，不要重复创建多个相同任务计划。
- 如果是全新的计划模式任务，则创建新的计划文件。
- 计划内容或实施方案发生明显变化时，必须同步更新对应 `docs/plans/` 计划记录。

## Output Rules

Codex 后续实现功能时必须按以下格式输出：

1. 实现思路
2. 修改文件
3. 完整代码
4. 运行方式
5. 下一步建议

输出要求：

- 使用中文。
- 说明本次改动对应 PRD 中的哪个功能。
- 明确列出修改过的文件。
- 如果无法运行或测试，需要说明原因。
- 不要省略关键代码。
- 不要引入 PRD 未确认的功能。
