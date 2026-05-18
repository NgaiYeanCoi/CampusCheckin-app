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
    password: ${DB_PASSWORD:}
```

环境变量说明：

- `DB_HOST`：MySQL 地址，默认 `localhost`
- `DB_PORT`：MySQL 端口，默认 `3306`
- `DB_NAME`：数据库名，默认 `campus_checkin`
- `DB_USERNAME`：数据库用户名，默认 `root`
- `DB_PASSWORD`：数据库密码，默认空字符串

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
.\gradlew.bat :server:bootRun
```

后端默认地址：

```text
http://localhost:8080/api/v1
```

如果 8080 端口被占用，可以临时换端口：

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
http://10.0.2.2:8080/api/v1
```

电脑浏览器、PowerShell、Postman 测试后端时使用：

```text
http://localhost:8080/api/v1
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
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"username":"s20260001","password":"123456","role":"STUDENT"}'

$studentToken = $studentLogin.data.token
$studentToken
```

### 7.2 教师登录

```powershell
$teacherLogin = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"username":"t20260001","password":"123456","role":"TEACHER"}'

$teacherToken = $teacherLogin.data.token
$teacherToken
```

### 7.3 查询当前用户

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/auth/me" `
  -Headers @{ Authorization = $studentToken }
```

### 7.4 学生查询课程

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/student/courses" `
  -Headers @{ Authorization = $studentToken }
```

### 7.5 学生查询周课表

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/student/schedule" `
  -Headers @{ Authorization = $studentToken }
```

### 7.6 查询课程当前签到任务

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/courses/1/active-check-in-task" `
  -Headers @{ Authorization = $studentToken }
```

### 7.7 学生提交签到

如果 `docs/schema.sql` 中的演示数据没有被改动，课程 1 当前有效签到任务口令是 `246810`。

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/v1/student/check-in" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $studentToken } `
  -Body '{"taskId":1,"password":"246810"}'
```

重复执行会返回“请勿重复签到”，这是正常业务规则。

### 7.8 学生查询签到记录

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/student/check-in-records" `
  -Headers @{ Authorization = $studentToken }
```

### 7.9 教师查询课程

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/teacher/courses" `
  -Headers @{ Authorization = $teacherToken }
```

### 7.10 教师发起签到

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/v1/teacher/check-in-tasks" `
  -ContentType "application/json" `
  -Headers @{ Authorization = $teacherToken } `
  -Body '{"courseId":1,"title":"Android应用开发 临时签到","password":"888888","startTime":"2026-05-17T14:00:00","endTime":"2026-05-17T15:00:00"}'
```

### 7.11 教师查看考勤统计

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/api/v1/teacher/courses/1/attendance-stats" `
  -Headers @{ Authorization = $teacherToken }
```

## 8. 常用接口

```text
POST /api/v1/auth/login
GET  /api/v1/auth/me
POST /api/v1/auth/logout

GET  /api/v1/student/courses
GET  /api/v1/student/schedule
POST /api/v1/student/check-in
GET  /api/v1/student/check-in-records

GET  /api/v1/teacher/courses
POST /api/v1/teacher/check-in-tasks
GET  /api/v1/teacher/courses/{courseId}/attendance-stats

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
