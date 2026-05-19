package cn.nyc1.myapplication.data;

import java.util.ArrayList;
import java.util.List;

import cn.nyc1.myapplication.model.AttendanceStats;
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.Course;

public final class PreviewData {
    private PreviewData() {
    }

    public static List<Course> courses() {
        List<Course> courses = new ArrayList<>();
        Course android = new Course();
        android.courseId = 1L;
        android.courseCode = "CSE101";
        android.courseName = "Android应用开发";
        android.teacherName = "李老师";
        android.location = "教学楼A-301";
        android.weekDay = 1;
        android.section = "1-2节";
        android.startTime = "08:00:00";
        android.endTime = "09:40:00";
        android.semester = "2025-2026-2";
        android.status = "ACTIVE";
        courses.add(android);

        Course database = new Course();
        database.courseId = 2L;
        database.courseCode = "CSE202";
        database.courseName = "数据库系统";
        database.teacherName = "王老师";
        database.location = "教学楼B-204";
        database.weekDay = 3;
        database.section = "3-4节";
        database.startTime = "10:10:00";
        database.endTime = "11:50:00";
        database.semester = "2025-2026-2";
        database.status = "ACTIVE";
        courses.add(database);
        return courses;
    }

    public static List<CheckInRecord> records() {
        List<CheckInRecord> records = new ArrayList<>();
        CheckInRecord record = new CheckInRecord();
        record.recordId = 1L;
        record.taskId = 1L;
        record.taskTitle = "Android应用开发 今日课堂签到";
        record.courseId = 1L;
        record.courseName = "Android应用开发";
        record.checkInTime = "2026-05-17 14:30:00";
        record.status = "SIGNED";
        record.remark = "离线预览记录";
        records.add(record);
        return records;
    }

    public static List<CheckInTask> tasks() {
        List<CheckInTask> tasks = new ArrayList<>();
        CheckInTask task = new CheckInTask();
        task.taskId = 1L;
        task.courseId = 1L;
        task.courseName = "Android应用开发";
        task.title = "Android应用开发 今日课堂签到";
        task.startTime = "2026-05-17 14:00:00";
        task.endTime = "2026-05-17 15:00:00";
        task.status = "ACTIVE";
        tasks.add(task);
        return tasks;
    }

    public static List<AttendanceStats> stats() {
        List<AttendanceStats> stats = new ArrayList<>();
        AttendanceStats item = new AttendanceStats();
        item.courseId = 1L;
        item.courseCode = "CSE101";
        item.courseName = "Android应用开发";
        item.taskId = 1L;
        item.taskTitle = "Android应用开发 今日课堂签到";
        item.totalCount = 4;
        item.signedCount = 1;
        item.lateCount = 0;
        item.absentCount = 3;
        item.exceptionCount = 0;
        item.attendanceRate = 25.00;
        stats.add(item);
        return stats;
    }
}
