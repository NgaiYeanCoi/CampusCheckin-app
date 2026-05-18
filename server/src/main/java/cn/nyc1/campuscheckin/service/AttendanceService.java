package cn.nyc1.campuscheckin.service;

import cn.nyc1.campuscheckin.dto.AttendanceStatsResponse;
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
}
