# CampusCheckin API 文档

本文档描述 CampusCheckin / 智课签 第一阶段后端 REST API，供 Android 客户端联调使用。

后端已接入动态 OpenAPI。Apifox / Swagger 管理接口时，以运行时生成的 OpenAPI 为准：

```text
http://localhost:8081/v3/api-docs
http://localhost:8081/v3/api-docs.yaml
http://localhost:8081/swagger-ui.html
```

本文档保留为人工阅读版接口说明。

## 1. 基础信息

本机调试地址：

```text
http://localhost:8081/api/v1
```

Android 模拟器访问宿主机后端地址：

```text
http://10.0.2.2:8081/api/v1
```

Android 当前实际生效的 baseUrl 配置在：

```text
app/src/main/java/cn/nyc1/myapplication/network/RetrofitClient.java
```

`RetrofitClient.BASE_URL` 是当前运行时来源。`app/src/main/res/values/strings.xml` 中的 `base_url` 仅同步保留为资源文本，当前 Retrofit 不从该字符串读取配置。

请求和响应格式：

```text
Content-Type: application/json
```

## 2. 统一响应结构

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

字段说明：

| 字段 | 类型 | 说明 |
|---|---|---|
| code | int | 业务状态码，0 表示成功 |
| message | string | 提示信息 |
| data | object / array / null | 响应数据 |

常见错误码：

| code | 说明 |
|---|---|
| 400 | 请求参数错误或业务规则不满足 |
| 401 | 未登录或 token 无效 |
| 500 | 服务器内部错误 |

Android 端不要依赖中文 `message` 判断业务逻辑，应优先使用 `code` 和 `data` 中的状态字段。

## 3. 鉴权方式

登录成功后，后端返回 `token`。后续需要登录的接口必须携带请求头：

```text
Authorization: <token>
```

示例：

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/auth/me" `
  -Headers @{ Authorization = $studentToken }
```

## 4. 演示账号

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

## 5. Auth 接口

### 5.1 登录

```text
POST /auth/login
```

是否需要 token：否

请求体：

```json
{
  "username": "s20260001",
  "password": "123456",
  "role": "STUDENT"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | string | 是 | 登录账号 |
| password | string | 是 | 登录密码 |
| role | string | 是 | `STUDENT` 或 `TEACHER` |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "token-value",
    "userId": 1001,
    "username": "s20260001",
    "role": "STUDENT",
    "profileId": 1,
    "displayName": "林一凡"
  }
}
```

### 5.2 获取当前用户

```text
GET /auth/me
```

是否需要 token：是

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "token-value",
    "userId": 1001,
    "username": "s20260001",
    "role": "STUDENT",
    "profileId": 1,
    "displayName": "林一凡"
  }
}
```

### 5.3 退出登录

```text
POST /auth/logout
```

是否需要 token：是

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 6. 学生端接口

### 6.1 学生课程列表

```text
GET /student/courses
```

是否需要 token：是，学生角色

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "courseId": 1,
      "courseCode": "CSE101",
      "courseName": "Android应用开发",
      "teacherId": 1,
      "teacherName": "李老师",
      "location": "教学楼A-301",
      "weekDay": 1,
      "section": "1-2节",
      "startTime": "08:00:00",
      "endTime": "09:40:00",
      "semester": "2025-2026-2",
      "status": "ACTIVE"
    }
  ]
}
```

### 6.2 学生周课表

```text
GET /student/schedule
```

是否需要 token：是，学生角色

响应结构同 `GET /student/courses`。Android 端可按 `weekDay` 和 `section` 渲染周课表。

### 6.3 学生提交签到

```text
POST /student/check-in
```

是否需要 token：是，学生角色

请求体：

```json
{
  "taskId": 1,
  "password": "246810"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| taskId | long | 是 | 签到任务 ID |
| password | string | 是 | 课堂签到口令 |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "recordId": 10,
    "taskId": 1,
    "courseId": 1,
    "courseName": "Android应用开发",
    "checkInTime": "2026-05-17 14:30:00",
    "status": "SIGNED",
    "message": "签到成功"
  }
}
```

可能状态：

| status | 说明 |
|---|---|
| SIGNED | 正常签到 |
| LATE | 迟到签到 |

常见失败：

| 场景 | code | message |
|---|---|---|
| 口令错误 | 400 | 签到口令错误 |
| 重复签到 | 400 | 请勿重复签到 |
| 未加入课程 | 400 | 你未加入该课程 |
| 签到未开始 | 400 | 签到尚未开始 |

### 6.4 学生签到记录

```text
GET /student/check-in-records
```

是否需要 token：是，学生角色

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "recordId": 1,
      "taskId": 1,
      "taskTitle": "Android应用开发 今日课堂签到",
      "courseId": 1,
      "courseName": "Android应用开发",
      "checkInTime": "2026-05-17 14:30:00",
      "status": "SIGNED",
      "remark": "按时签到"
    }
  ]
}
```

## 7. 教师端接口

### 7.1 教师课程列表

```text
GET /teacher/courses
```

是否需要 token：是，教师角色

成功响应同课程对象数组：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "courseId": 1,
      "courseCode": "CSE101",
      "courseName": "Android应用开发",
      "teacherId": 1,
      "teacherName": "李老师",
      "location": "教学楼A-301",
      "weekDay": 1,
      "section": "1-2节",
      "startTime": "08:00:00",
      "endTime": "09:40:00",
      "semester": "2025-2026-2",
      "status": "ACTIVE"
    }
  ]
}
```

### 7.2 教师发起签到

```text
POST /teacher/check-in-tasks
```

是否需要 token：是，教师角色

请求体：

```json
{
  "courseId": 1,
  "title": "Android应用开发 临时签到",
  "password": "888888",
  "startTime": "2026-05-17T14:00:00",
  "endTime": "2026-05-17T15:00:00"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| courseId | long | 是 | 课程 ID |
| title | string | 是 | 签到任务标题 |
| password | string | 是 | 课堂口令 |
| startTime | string | 是 | ISO 日期时间 |
| endTime | string | 是 | ISO 日期时间 |

成功响应：

```json
{
  "code": 0,
  "message": "success",
    "data": {
      "taskId": 4,
      "courseId": 1,
      "courseName": "Android应用开发",
      "title": "Android应用开发 临时签到",
      "startTime": "2026-05-17 14:00:00",
      "endTime": "2026-05-17 15:00:00",
    "status": "ACTIVE"
  }
}
```

可能状态：

| status | 说明 |
|---|---|
| NOT_STARTED | 未开始 |
| ACTIVE | 可签到 |
| ENDED | 已结束 |

### 7.3 教师签到任务列表

```text
GET /teacher/check-in-tasks
```

是否需要 token：是，教师角色

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| courseId | long | 否 | 按课程筛选签到任务 |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "taskId": 4,
      "courseId": 1,
      "courseName": "Android应用开发",
      "title": "Android应用开发 临时签到",
      "startTime": "2026-05-17 14:00:00",
      "endTime": "2026-05-17 15:00:00",
      "status": "ACTIVE"
    }
  ]
}
```

### 7.4 教师手动截止签到任务

```text
POST /teacher/check-in-tasks/{taskId}/end
```

是否需要 token：是，教师角色

路径参数：

| 参数 | 类型 | 说明 |
|---|---|---|
| taskId | long | 签到任务 ID |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "taskId": 4,
    "courseId": 1,
    "courseName": "Android应用开发",
    "title": "Android应用开发 临时签到",
    "startTime": "2026-05-17 14:00:00",
    "endTime": "2026-05-17 14:32:00",
    "status": "ENDED"
  }
}
```

常见失败：

| 场景 | code | message |
|---|---|---|
| 操作其他教师课程任务 | 400 | 签到任务不存在，或无权操作该任务 |
| 任务未开始或已结束 | 400 | 只能手动截止进行中的签到任务 |

### 7.5 教师查看课程考勤统计

```text
GET /teacher/courses/{courseId}/attendance-stats
```

是否需要 token：是，教师角色

路径参数：

| 参数 | 类型 | 说明 |
|---|---|---|
| courseId | long | 课程 ID |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "courseId": 1,
      "courseCode": "CSE101",
      "courseName": "Android应用开发",
      "taskId": 1,
      "taskTitle": "Android应用开发 今日课堂签到",
      "totalCount": 4,
      "signedCount": 1,
      "lateCount": 0,
      "absentCount": 0,
      "exceptionCount": 0,
      "attendanceRate": 25.00
    }
  ]
}
```

## 8. 通用课程接口

### 8.1 课程详情

```text
GET /courses/{courseId}
```

是否需要 token：是

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "courseId": 1,
    "courseCode": "CSE101",
    "courseName": "Android应用开发",
    "teacherId": 1,
    "teacherName": "李老师",
    "location": "教学楼A-301",
    "weekDay": 1,
    "section": "1-2节",
    "startTime": "08:00:00",
    "endTime": "09:40:00",
    "semester": "2025-2026-2",
    "status": "ACTIVE"
  }
}
```

### 8.2 查询课程当前签到任务

```text
GET /courses/{courseId}/active-check-in-task
```

是否需要 token：是

成功响应：

```json
{
  "code": 0,
  "message": "success",
    "data": {
      "taskId": 1,
      "courseId": 1,
      "courseName": "Android应用开发",
      "title": "Android应用开发 今日课堂签到",
      "startTime": "2026-05-17 14:00:00",
    "endTime": "2026-05-17 15:00:00",
    "status": "ACTIVE"
  }
}
```

如果当前课程没有可签到任务：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 9. 数据枚举

用户角色：

| 值 | 说明 |
|---|---|
| STUDENT | 学生 |
| TEACHER | 教师 |

课程状态：

| 值 | 说明 |
|---|---|
| ACTIVE | 启用 |
| ARCHIVED | 归档 |

签到任务状态：

| 值 | 说明 |
|---|---|
| NOT_STARTED | 未开始 |
| ACTIVE | 可签到 |
| ENDED | 已结束 |
| CANCELLED | 已取消 |

签到记录状态：

| 值 | 说明 |
|---|---|
| SIGNED | 已签到 |
| LATE | 迟到 |
| ABSENT | 缺勤 |
| EXCEPTION | 异常 |

