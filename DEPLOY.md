# CampusCheckin 部署与运行说明

本文档用于说明 CampusCheckin / 智课签 的数据库导入、后端启动、接口测试和 Android 运行方式。

完整接口字段、请求体和响应示例见 [docs/API.md](docs/API.md)。Apifox 推荐直接导入动态 OpenAPI 地址，见本文档第 9 节。

## 1. 环境要求

- JDK 21
- Android Studio
- Android SDK 35
- MySQL 8.0.45
- PowerShell

## 2. 数据库初始化

数据库脚本位置：

```text
docs/schema.sql
```

导入 SQL：

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < docs\schema.sql"
```

输入 MySQL 密码后，脚本会创建并初始化：

```text
campus_checkin
```

验证数据库：

```powershell
mysql -u root -p -e "USE campus_checkin; SHOW TABLES; SELECT username, role FROM users;"
```

## 3. 后端数据库连接配置

配置文件位置：

```text
server/src/main/resources/application.yml
```

关键配置：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:campus_checkin}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:10086}
```

环境变量说明：

- `DB_HOST`：MySQL 地址，默认 `localhost`
- `DB_PORT`：MySQL 端口，默认 `3306`
- `DB_NAME`：数据库名，默认 `campus_checkin`
- `DB_USERNAME`：数据库用户名，默认 `root`
- `DB_PASSWORD`：数据库密码，当前本地默认 `10086`，建议用环境变量覆盖

不要把真实 MySQL 密码写入代码仓库。建议在 PowerShell 当前窗口中设置：

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="campus_checkin"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
```

## 4. 后端运行

编译后端：

```powershell
.\gradlew.bat :server:compileJava
```

启动后端：

```powershell
.\gradlew.bat :server:bootRun --args="--server.port=8081"
```

当前后端本机调试地址：

```text
http://localhost:8081/api/v1
```

如果你要使用 Spring Boot 默认 8080 端口，需要同步修改 Android 端 `RetrofitClient.BASE_URL`。

后端日志：

- 控制台会输出 Spring Boot 启动日志、业务异常和未处理异常堆栈。
- 每次 `/api/v1/**` 接口调用都会输出 `API_CALL` 日志，包含请求方法、路径、HTTP 状态、业务 `code/message` 和耗时。
- 同时写入文件：`logs/campus-checkin-server.log`。
- 如果接口返回 `服务器内部错误`，优先查看控制台或上述日志文件中的 `未处理的服务器异常`。

示例：

```text
API_CALL POST /api/v1/auth/login -> http=200 code=200 message=success duration=523ms
API_CALL GET /api/v1/teacher/check-in-tasks/17/detail -> http=200 code=200 message=success duration=31ms
```

如果修改了后端代码但 8081 仍表现为旧逻辑，通常是旧进程还在运行。可先查看占用端口的进程：

```powershell
Get-NetTCPConnection -LocalPort 8081 | Select-Object LocalAddress,LocalPort,State,OwningProcess
Get-Process -Id <OwningProcess>
```

停止旧进程后，重新执行：

```powershell
.\gradlew.bat :server:bootRun --args="--server.port=8081"
```

## 5. Android 运行

构建 Debug APK：

```powershell
.\gradlew.bat :app:assembleDebug
```

APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

Android 模拟器访问电脑本机后端时不能使用 `localhost`，应使用：

```text
http://10.0.2.2:8081/api/v1
```

当前 Android 端真正生效的 baseUrl 配置在：

```text
app/src/main/java/cn/nyc1/myapplication/network/RetrofitClient.java
```

其中 `RetrofitClient.BASE_URL` 当前为：

```text
http://10.0.2.2:8081/api/v1/
```

`app/src/main/res/values/strings.xml` 中的 `base_url` 目前只作为同步保留的资源文本，不是当前 Retrofit 的运行时配置来源。

电脑浏览器、PowerShell、Postman 测试后端时使用：

```text
http://localhost:8081/api/v1
```

## 6. 演示账号

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

当前课堂签到口令示例：

```text
246810
```

## 7. 接口测试

以下命令需要在后端启动后执行。

### 7.1 学生登录

```powershell
$studentLogin = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"username":"s20260001","password":"123456","role":"STUDENT"}'

$studentToken = $studentLogin.data.token
$studentToken
```

### 7.2 教师登录

```powershell
$teacherLogin = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"username":"t20260001","password":"123456","role":"TEACHER"}'

$teacherToken = $teacherLogin.data.token
$teacherToken
```

### 7.3 查询当前用户

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/auth/me" `
  -Headers @{ Authorization = $studentToken }
```

### 7.4 学生查询课程

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/student/courses" `
  -Headers @{ Authorization = $studentToken }
```

### 7.5 学生查询周课表

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/student/schedule" `
  -Headers @{ Authorization = $studentToken }
```

### 7.6 学生查询当前可签到任务

该接口用于学生首页聚合展示所有已选课程中的当前签到任务。教师在 Java、Android 等任意课程下发布签到后，选修该课程的学生都应能在这里查到。

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/student/check-in-tasks/active" `
  -Headers @{ Authorization = $studentToken }
```

### 7.7 查询课程当前签到任务

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/courses/1/active-check-in-task" `
  -Headers @{ Authorization = $studentToken }
```

### 7.8 学生提交签到

如果 `docs/schema.sql` 中的演示数据没有被改动，课程 1 当前有效签到任务口令是 `246810`。

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/student/check-in" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $studentToken } `
  -Body '{"taskId":1,"password":"246810"}'
```

重复执行会返回“请勿重复签到”，这是正常业务规则。

### 7.9 学生查询签到记录

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/student/check-in-records" `
  -Headers @{ Authorization = $studentToken }
```

### 7.10 教师查询课程

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/courses" `
  -Headers @{ Authorization = $teacherToken }
```

### 7.11 教师发起签到

```powershell
$createdTask = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $teacherToken } `
  -Body '{"courseId":1,"title":"Android应用开发 临时签到","password":"888888","startTime":"2026-05-19T14:00:00","endTime":"2026-05-19T15:00:00"}'

$createdTask.data.taskId
```

二维码签到任务示例：

```powershell
$qrTask = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $teacherToken } `
  -Body '{"courseId":1,"title":"Android应用开发 二维码签到","checkInType":"QR_CODE","startTime":"2026-06-14T14:00:00","endTime":"2026-06-14T15:00:00"}'

$qrTask.data.taskId
```

### 7.12 教师查询签到任务列表

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks" `
  -Headers @{ Authorization = $teacherToken }
```

也可以按课程筛选：

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks?courseId=1" `
  -Headers @{ Authorization = $teacherToken }
```

### 7.13 教师手动截止签到任务

只允许截止当前教师自己课程下正在进行的签到任务。

```powershell
$taskId = $createdTask.data.taskId

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks/$taskId/end" `
  -Headers @{ Authorization = $teacherToken }
```

### 7.14 教师查看签到任务详情

用于查看某一次签到的实时统计和学生名单。

```powershell
$taskId = $createdTask.data.taskId

Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks/$taskId/detail" `
  -Headers @{ Authorization = $teacherToken }
```

二维码任务详情中会返回：

```text
qrPayload = campuscheckin://check-in?taskId=<taskId>&token=<qrToken>
```

学生扫码后等价于提交：

```powershell
$qrDetail = Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/check-in-tasks/$($qrTask.data.taskId)/detail" `
  -Headers @{ Authorization = $teacherToken }

$qrToken = [regex]::Match($qrDetail.data.qrPayload, "token=([^&]+)").Groups[1].Value

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/student/check-in" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $studentToken } `
  -Body (@{ taskId = $qrTask.data.taskId; qrToken = $qrToken } | ConvertTo-Json)
```

### 7.15 教师查看考勤统计

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/courses/1/attendance-stats" `
  -Headers @{ Authorization = $teacherToken }
```

### 7.16 学生筛选签到记录

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/student/check-in-records?courseId=1&status=SIGNED&startDate=2026-06-01&endDate=2026-06-30" `
  -Headers @{ Authorization = $studentToken }
```

### 7.17 教师导出考勤统计 CSV

Android 教师端在考勤统计页点击“下载 CSV”会调用同一接口：

- Android 10 及以上：保存到系统 `Downloads/CampusCheckin/attendance-course-{courseId}.csv`。
- Android 9 及以下：保存到 app 专属 Documents 目录，不申请外部存储权限。

```powershell
Invoke-WebRequest `
  -Method Get `
  -Uri "http://localhost:8081/api/v1/teacher/courses/1/attendance-stats/export" `
  -Headers @{ Authorization = $teacherToken } `
  -OutFile "attendance-stats-course-1.csv"
```

## 8. 常用接口

```text
POST /api/v1/auth/login
GET  /api/v1/auth/me
POST /api/v1/auth/logout

GET  /api/v1/student/courses
GET  /api/v1/student/schedule
GET  /api/v1/student/check-in-tasks/active
POST /api/v1/student/check-in
GET  /api/v1/student/check-in-records

GET  /api/v1/teacher/courses
POST /api/v1/teacher/check-in-tasks
GET  /api/v1/teacher/check-in-tasks
POST /api/v1/teacher/check-in-tasks/{taskId}/end
GET  /api/v1/teacher/check-in-tasks/{taskId}/detail
GET  /api/v1/teacher/courses/{courseId}/attendance-stats
GET  /api/v1/teacher/courses/{courseId}/attendance-stats/export

GET  /api/v1/courses/{courseId}
GET  /api/v1/courses/{courseId}/active-check-in-task
```

## 9. Apifox / Swagger 导入

后端已接入动态 OpenAPI。启动后端后，可以直接把 OpenAPI URL 导入 Apifox。

如果后端运行在 8081：

```text
Swagger UI:   http://localhost:8081/swagger-ui.html
OpenAPI JSON: http://localhost:8081/v3/api-docs
OpenAPI YAML: http://localhost:8081/v3/api-docs.yaml
```

如果后端运行在 8080：

```text
Swagger UI:   http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
```

Apifox 导入建议：

1. 选择 OpenAPI / Swagger URL 导入。
2. 填入 `http://localhost:8081/v3/api-docs`。
3. 登录接口拿到 token 后，在 Apifox 环境变量或全局 Header 中设置：

```text
Authorization: 登录返回的token
```

注意：`10.0.2.2` 是 Android 模拟器访问电脑宿主机的地址，不用于 Apifox 导入。Apifox 在电脑上运行时使用 `localhost`。

## 10. 常见问题

### 10.1 Access denied for user root

说明数据库用户名或密码不对。检查：

```powershell
$env:DB_USERNAME
$env:DB_PASSWORD
```

重新设置：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
```

### 10.2 Unknown database campus_checkin

说明还没有导入 SQL。执行：

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < docs\schema.sql"
```

### 10.3 Port 8080 already in use

说明 8080 端口被占用。可以临时换端口：

```powershell
.\gradlew.bat :server:bootRun --args="--server.port=8081"
```

### 10.4 返回“请先登录”

说明没有传 token，或者 token 过期。先重新登录，再把返回的 token 放到请求头：

```text
Authorization: 返回的token
```

### 10.5 Android Gradle 写入 D:\.android 报错

如果出现 `D:\.android\.android\debug.keystore.lock`，通常是旧环境变量 `ANDROID_SDK_HOME` 干扰。

建议：

```text
删除 ANDROID_SDK_HOME
保留或新增 ANDROID_USER_HOME=D:\.android
```

修改系统环境变量后，重启 VS Code 和 Android Studio。

