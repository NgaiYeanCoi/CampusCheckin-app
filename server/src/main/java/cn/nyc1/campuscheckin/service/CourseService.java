package cn.nyc1.campuscheckin.service;

import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.entity.Student;
import cn.nyc1.campuscheckin.entity.Teacher;
import cn.nyc1.campuscheckin.mapper.CourseMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final AuthService authService;
    private final CourseMapper courseMapper;

    public CourseService(AuthService authService, CourseMapper courseMapper) {
        this.authService = authService;
        this.courseMapper = courseMapper;
    }

    public List<CourseResponse> studentCourses() {
        Student student = authService.currentStudent();
        return courseMapper.findByStudentId(student.getStudentId());
    }

    public List<CourseResponse> studentSchedule() {
        return studentCourses();
    }

    public List<CourseResponse> teacherCourses() {
        Teacher teacher = authService.currentTeacher();
        return courseMapper.findByTeacherId(teacher.getTeacherId());
    }

    public CourseResponse detail(Long courseId) {
        CourseResponse course = courseMapper.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        return course;
    }
}
