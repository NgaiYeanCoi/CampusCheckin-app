package cn.nyc1.campuscheckin.service;

import cn.nyc1.campuscheckin.dto.AttendanceStatsResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskDetailResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskSummaryResponse;
import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.entity.Teacher;
import cn.nyc1.campuscheckin.mapper.AttendanceStatsMapper;
import cn.nyc1.campuscheckin.mapper.CourseMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private final AuthService authService;
    private final CourseMapper courseMapper;
    private final AttendanceStatsMapper attendanceStatsMapper;

    public AttendanceService(
            AuthService authService,
            CourseMapper courseMapper,
            AttendanceStatsMapper attendanceStatsMapper
    ) {
        this.authService = authService;
        this.courseMapper = courseMapper;
        this.attendanceStatsMapper = attendanceStatsMapper;
    }

    public List<AttendanceStatsResponse> courseStats(Long courseId) {
        Teacher teacher = authService.currentTeacher();
        CourseResponse course = courseMapper.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        if (!teacher.getTeacherId().equals(course.getTeacherId())) {
            throw new IllegalArgumentException("只能查看自己课程的考勤统计");
        }
        return attendanceStatsMapper.findByCourseId(courseId);
    }

    public String courseStatsCsv(Long courseId, Long taskId) {
        List<AttendanceStatsResponse> rows = courseStats(courseId);
        StringBuilder builder = new StringBuilder();
        builder.append('\ufeff');
        builder.append("课程编号,课程名称,签到任务ID,签到任务标题,应到,已签,迟到,缺勤,异常,出勤率\n");
        for (AttendanceStatsResponse row : rows) {
            if (taskId != null && !taskId.equals(row.getTaskId())) {
                continue;
            }
            appendCsv(builder, row.getCourseCode());
            appendCsv(builder, row.getCourseName());
            appendCsv(builder, row.getTaskId());
            appendCsv(builder, row.getTaskTitle());
            appendCsv(builder, row.getTotalCount());
            appendCsv(builder, row.getSignedCount());
            appendCsv(builder, row.getLateCount());
            appendCsv(builder, row.getAbsentCount());
            appendCsv(builder, row.getExceptionCount());
            builder.append(row.getAttendanceRate() == null ? "" : row.getAttendanceRate().toPlainString()).append("%\n");
        }
        return builder.toString();
    }

    public CheckInTaskDetailResponse taskDetail(Long taskId) {
        Teacher teacher = authService.currentTeacher();
        CheckInTaskDetailResponse detail = attendanceStatsMapper.findTaskDetailHeader(taskId, teacher.getTeacherId());
        if (detail == null) {
            throw new IllegalArgumentException("签到任务不存在，或无权查看该任务");
        }
        CheckInTaskSummaryResponse summary = attendanceStatsMapper.findTaskSummary(taskId);
        if (summary == null) {
            summary = new CheckInTaskSummaryResponse();
            summary.setTotalCount(0);
            summary.setSignedCount(0);
            summary.setLateCount(0);
            summary.setUnsignedCount(0);
            summary.setAbsentCount(0);
            summary.setExceptionCount(0);
        }
        detail.setSummary(summary);
        detail.setStudents(attendanceStatsMapper.findStudentRecordsByTaskId(taskId));
        return detail;
    }

    private void appendCsv(StringBuilder builder, Object value) {
        String text = value == null ? "" : String.valueOf(value);
        builder.append('"').append(text.replace("\"", "\"\"")).append("\",");
    }
}
