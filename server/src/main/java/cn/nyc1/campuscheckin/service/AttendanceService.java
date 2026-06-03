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
}
