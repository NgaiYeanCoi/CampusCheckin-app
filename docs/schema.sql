-- CampusCheckin / 智课签 database schema
-- Target database: MySQL 8.0.45
-- Demo password for all seeded users: 123456
-- BCrypt hash generated with cost 10.

CREATE DATABASE IF NOT EXISTS campus_checkin
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE campus_checkin;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '登录账号',
  password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt加密后的密码',
  role VARCHAR(20) NOT NULL COMMENT '用户角色：STUDENT或TEACHER',
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '账号状态：ENABLED或DISABLED',
  last_login_at DATETIME NULL COMMENT '最后登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_users_username (username),
  CONSTRAINT ck_users_role CHECK (role IN ('STUDENT', 'TEACHER')),
  CONSTRAINT ck_users_status CHECK (status IN ('ENABLED', 'DISABLED'))
) ENGINE=InnoDB COMMENT='登录用户表';

CREATE TABLE IF NOT EXISTS students (
  student_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
  user_id BIGINT NOT NULL COMMENT '关联用户ID',
  student_no VARCHAR(30) NOT NULL COMMENT '学号',
  name VARCHAR(50) NOT NULL COMMENT '学生姓名',
  class_name VARCHAR(80) NOT NULL COMMENT '班级名称',
  major VARCHAR(80) NULL COMMENT '专业',
  phone VARCHAR(30) NULL COMMENT '联系电话',
  avatar_text VARCHAR(10) NULL COMMENT '头像占位文字',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_students_user_id (user_id),
  UNIQUE KEY uk_students_student_no (student_no),
  CONSTRAINT fk_students_user_id FOREIGN KEY (user_id)
    REFERENCES users (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='学生信息表';

CREATE TABLE IF NOT EXISTS teachers (
  teacher_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
  user_id BIGINT NOT NULL COMMENT '关联用户ID',
  teacher_no VARCHAR(30) NOT NULL COMMENT '教师工号',
  name VARCHAR(50) NOT NULL COMMENT '教师姓名',
  department VARCHAR(80) NOT NULL COMMENT '所属院系',
  title VARCHAR(50) NULL COMMENT '职称',
  phone VARCHAR(30) NULL COMMENT '联系电话',
  avatar_text VARCHAR(10) NULL COMMENT '头像占位文字',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_teachers_user_id (user_id),
  UNIQUE KEY uk_teachers_teacher_no (teacher_no),
  CONSTRAINT fk_teachers_user_id FOREIGN KEY (user_id)
    REFERENCES users (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='教师信息表';

CREATE TABLE IF NOT EXISTS courses (
  course_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  course_code VARCHAR(30) NOT NULL COMMENT '课程编号',
  course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
  teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
  location VARCHAR(100) NOT NULL COMMENT '上课地点',
  week_day TINYINT NOT NULL COMMENT '星期几：1表示周一，7表示周日',
  section VARCHAR(30) NOT NULL COMMENT '上课节次',
  start_time TIME NOT NULL COMMENT '上课开始时间',
  end_time TIME NOT NULL COMMENT '上课结束时间',
  semester VARCHAR(30) NOT NULL COMMENT '学期',
  credit DECIMAL(3,1) NOT NULL DEFAULT 2.0 COMMENT '学分',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '课程状态：ACTIVE或ARCHIVED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_courses_course_code (course_code),
  KEY idx_courses_teacher_id (teacher_id),
  KEY idx_courses_week_day (week_day),
  CONSTRAINT fk_courses_teacher_id FOREIGN KEY (teacher_id)
    REFERENCES teachers (teacher_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_courses_week_day CHECK (week_day BETWEEN 1 AND 7),
  CONSTRAINT ck_courses_status CHECK (status IN ('ACTIVE', 'ARCHIVED'))
) ENGINE=InnoDB COMMENT='课程信息表';

CREATE TABLE IF NOT EXISTS course_enrollments (
  enrollment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课关系ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  student_id BIGINT NOT NULL COMMENT '学生ID',
  enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '选课状态：ACTIVE或DROPPED',
  UNIQUE KEY uk_course_enrollments_course_student (course_id, student_id),
  KEY idx_course_enrollments_student_id (student_id),
  CONSTRAINT fk_course_enrollments_course_id FOREIGN KEY (course_id)
    REFERENCES courses (course_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_course_enrollments_student_id FOREIGN KEY (student_id)
    REFERENCES students (student_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_course_enrollments_status CHECK (status IN ('ACTIVE', 'DROPPED'))
) ENGINE=InnoDB COMMENT='学生选课关系表';

CREATE TABLE IF NOT EXISTS check_in_tasks (
  task_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到任务ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  title VARCHAR(100) NOT NULL COMMENT '签到任务标题',
  password VARCHAR(20) NOT NULL COMMENT '课堂签到口令',
  start_time DATETIME NOT NULL COMMENT '签到开始时间',
  end_time DATETIME NOT NULL COMMENT '签到截止时间',
  status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED' COMMENT '任务状态：NOT_STARTED、ACTIVE、ENDED或CANCELLED',
  created_by BIGINT NOT NULL COMMENT '发起教师ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_check_in_tasks_course_id (course_id),
  KEY idx_check_in_tasks_created_by (created_by),
  KEY idx_check_in_tasks_time (start_time, end_time),
  CONSTRAINT fk_check_in_tasks_course_id FOREIGN KEY (course_id)
    REFERENCES courses (course_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_check_in_tasks_created_by FOREIGN KEY (created_by)
    REFERENCES teachers (teacher_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_check_in_tasks_status CHECK (status IN ('NOT_STARTED', 'ACTIVE', 'ENDED', 'CANCELLED')),
  CONSTRAINT ck_check_in_tasks_time CHECK (start_time < end_time)
) ENGINE=InnoDB COMMENT='签到任务表';

CREATE TABLE IF NOT EXISTS check_in_records (
  record_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到记录ID',
  task_id BIGINT NOT NULL COMMENT '签到任务ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  student_id BIGINT NOT NULL COMMENT '学生ID',
  check_in_time DATETIME NULL COMMENT '签到时间，缺勤可为空',
  status VARCHAR(20) NOT NULL COMMENT '签到状态：SIGNED、LATE、ABSENT或EXCEPTION',
  remark VARCHAR(255) NULL COMMENT '备注信息',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_check_in_records_task_student (task_id, student_id),
  KEY idx_check_in_records_course_id (course_id),
  KEY idx_check_in_records_student_id (student_id),
  KEY idx_check_in_records_status (status),
  CONSTRAINT fk_check_in_records_task_id FOREIGN KEY (task_id)
    REFERENCES check_in_tasks (task_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_check_in_records_course_id FOREIGN KEY (course_id)
    REFERENCES courses (course_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_check_in_records_student_id FOREIGN KEY (student_id)
    REFERENCES students (student_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_check_in_records_status CHECK (status IN ('SIGNED', 'LATE', 'ABSENT', 'EXCEPTION'))
) ENGINE=InnoDB COMMENT='签到记录表';

CREATE OR REPLACE VIEW v_course_attendance_stats AS
SELECT
  c.course_id,
  c.course_code,
  c.course_name,
  t.task_id,
  t.title AS task_title,
  COUNT(ce.student_id) AS total_count,
  COALESCE(SUM(CASE WHEN r.status = 'SIGNED' THEN 1 ELSE 0 END), 0) AS signed_count,
  COALESCE(SUM(CASE WHEN r.status = 'LATE' THEN 1 ELSE 0 END), 0) AS late_count,
  COALESCE(SUM(CASE WHEN r.status = 'ABSENT' OR (t.status = 'ENDED' AND r.record_id IS NULL) THEN 1 ELSE 0 END), 0) AS absent_count,
  COALESCE(SUM(CASE WHEN r.status = 'EXCEPTION' THEN 1 ELSE 0 END), 0) AS exception_count,
  ROUND(
    CASE
      WHEN COUNT(ce.student_id) = 0 THEN 0
      ELSE (
        COALESCE(SUM(CASE WHEN r.status IN ('SIGNED', 'LATE') THEN 1 ELSE 0 END), 0)
        / COUNT(ce.student_id)
      ) * 100
    END,
    2
  ) AS attendance_rate
FROM courses c
JOIN check_in_tasks t ON t.course_id = c.course_id
LEFT JOIN course_enrollments ce
  ON ce.course_id = c.course_id
  AND ce.status = 'ACTIVE'
LEFT JOIN check_in_records r
  ON r.task_id = t.task_id
  AND r.student_id = ce.student_id
GROUP BY
  c.course_id,
  c.course_code,
  c.course_name,
  t.task_id,
  t.title;

SET @demo_password_hash = '$2y$10$8Oklw6PcWw9CUZKwg9kEEO87QMJgux2PElCOKpPLBVykKHCVnAYnq';

INSERT INTO users (user_id, username, password_hash, role, status)
VALUES
  (1001, 's20260001', @demo_password_hash, 'STUDENT', 'ENABLED'),
  (1002, 's20260002', @demo_password_hash, 'STUDENT', 'ENABLED'),
  (1003, 's20260003', @demo_password_hash, 'STUDENT', 'ENABLED'),
  (1004, 's20260004', @demo_password_hash, 'STUDENT', 'ENABLED'),
  (2001, 't20260001', @demo_password_hash, 'TEACHER', 'ENABLED'),
  (2002, 't20260002', @demo_password_hash, 'TEACHER', 'ENABLED')
ON DUPLICATE KEY UPDATE
  password_hash = VALUES(password_hash),
  role = VALUES(role),
  status = VALUES(status);

INSERT INTO students (student_id, user_id, student_no, name, class_name, major, phone, avatar_text)
VALUES
  (1, 1001, '20260001', '林一凡', '软件工程2301班', '软件工程', '13800000001', '林'),
  (2, 1002, '20260002', '陈思雨', '软件工程2301班', '软件工程', '13800000002', '陈'),
  (3, 1003, '20260003', '周明远', '软件工程2301班', '软件工程', '13800000003', '周'),
  (4, 1004, '20260004', '黄若曦', '软件工程2301班', '软件工程', '13800000004', '黄')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  name = VALUES(name),
  class_name = VALUES(class_name),
  major = VALUES(major),
  phone = VALUES(phone),
  avatar_text = VALUES(avatar_text);

INSERT INTO teachers (teacher_id, user_id, teacher_no, name, department, title, phone, avatar_text)
VALUES
  (1, 2001, 'T20260001', '李老师', '计算机学院', '讲师', '13900000001', '李'),
  (2, 2002, 'T20260002', '王老师', '基础教学部', '副教授', '13900000002', '王')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  name = VALUES(name),
  department = VALUES(department),
  title = VALUES(title),
  phone = VALUES(phone),
  avatar_text = VALUES(avatar_text);

INSERT INTO courses (
  course_id,
  course_code,
  course_name,
  teacher_id,
  location,
  week_day,
  section,
  start_time,
  end_time,
  semester,
  credit,
  status
)
VALUES
  (1, 'CSE101', 'Android应用开发', 1, '教学楼A-301', 1, '1-2节', '08:00:00', '09:40:00', '2025-2026-2', 3.0, 'ACTIVE'),
  (2, 'CSE102', 'Java程序设计', 1, '教学楼B-205', 3, '3-4节', '10:10:00', '11:50:00', '2025-2026-2', 3.0, 'ACTIVE'),
  (3, 'MATH201', '高等数学', 2, '教学楼C-102', 2, '5-6节', '14:00:00', '15:40:00', '2025-2026-2', 4.0, 'ACTIVE'),
  (4, 'ENG101', '大学英语', 2, '教学楼D-404', 5, '1-2节', '08:00:00', '09:40:00', '2025-2026-2', 2.0, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  course_name = VALUES(course_name),
  teacher_id = VALUES(teacher_id),
  location = VALUES(location),
  week_day = VALUES(week_day),
  section = VALUES(section),
  start_time = VALUES(start_time),
  end_time = VALUES(end_time),
  semester = VALUES(semester),
  credit = VALUES(credit),
  status = VALUES(status);

INSERT INTO course_enrollments (enrollment_id, course_id, student_id, enrolled_at, status)
VALUES
  (1, 1, 1, CURRENT_TIMESTAMP, 'ACTIVE'),
  (2, 1, 2, CURRENT_TIMESTAMP, 'ACTIVE'),
  (3, 1, 3, CURRENT_TIMESTAMP, 'ACTIVE'),
  (4, 1, 4, CURRENT_TIMESTAMP, 'ACTIVE'),
  (5, 2, 1, CURRENT_TIMESTAMP, 'ACTIVE'),
  (6, 2, 2, CURRENT_TIMESTAMP, 'ACTIVE'),
  (7, 2, 3, CURRENT_TIMESTAMP, 'ACTIVE'),
  (8, 3, 1, CURRENT_TIMESTAMP, 'ACTIVE'),
  (9, 3, 2, CURRENT_TIMESTAMP, 'ACTIVE'),
  (10, 3, 4, CURRENT_TIMESTAMP, 'ACTIVE'),
  (11, 4, 1, CURRENT_TIMESTAMP, 'ACTIVE'),
  (12, 4, 3, CURRENT_TIMESTAMP, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  status = VALUES(status);

INSERT INTO check_in_tasks (
  task_id,
  course_id,
  title,
  password,
  start_time,
  end_time,
  status,
  created_by
)
VALUES
  (
    1,
    1,
    'Android应用开发 今日课堂签到',
    '246810',
    CURRENT_TIMESTAMP - INTERVAL 10 MINUTE,
    CURRENT_TIMESTAMP + INTERVAL 30 MINUTE,
    'ACTIVE',
    1
  ),
  (
    2,
    1,
    'Android应用开发 上次课堂签到',
    '112233',
    CURRENT_TIMESTAMP - INTERVAL 1 DAY - INTERVAL 50 MINUTE,
    CURRENT_TIMESTAMP - INTERVAL 1 DAY,
    'ENDED',
    1
  ),
  (
    3,
    2,
    'Java程序设计 课前签到',
    '135790',
    CURRENT_TIMESTAMP + INTERVAL 1 HOUR,
    CURRENT_TIMESTAMP + INTERVAL 2 HOUR,
    'NOT_STARTED',
    1
  )
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  password = VALUES(password),
  start_time = VALUES(start_time),
  end_time = VALUES(end_time),
  status = VALUES(status),
  created_by = VALUES(created_by);

INSERT INTO check_in_records (
  record_id,
  task_id,
  course_id,
  student_id,
  check_in_time,
  status,
  remark
)
VALUES
  (1, 1, 1, 1, CURRENT_TIMESTAMP - INTERVAL 5 MINUTE, 'SIGNED', '当前课堂已签到'),
  (2, 2, 1, 1, CURRENT_TIMESTAMP - INTERVAL 1 DAY - INTERVAL 45 MINUTE, 'SIGNED', '按时签到'),
  (3, 2, 1, 2, CURRENT_TIMESTAMP - INTERVAL 1 DAY + INTERVAL 5 MINUTE, 'LATE', '迟到签到'),
  (4, 2, 1, 3, NULL, 'ABSENT', '未提交签到'),
  (5, 2, 1, 4, CURRENT_TIMESTAMP - INTERVAL 1 DAY - INTERVAL 30 MINUTE, 'SIGNED', '按时签到')
ON DUPLICATE KEY UPDATE
  check_in_time = VALUES(check_in_time),
  status = VALUES(status),
  remark = VALUES(remark);
