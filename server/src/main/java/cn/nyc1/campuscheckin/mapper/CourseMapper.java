package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.CourseResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper {

    @Select("""
            SELECT
              c.course_id AS courseId,
              c.course_code AS courseCode,
              c.course_name AS courseName,
              c.teacher_id AS teacherId,
              t.name AS teacherName,
              c.location,
              c.week_day AS weekDay,
              c.section,
              c.start_time AS startTime,
              c.end_time AS endTime,
              c.semester,
              c.status
            FROM course_enrollments ce
            JOIN courses c ON c.course_id = ce.course_id
            JOIN teachers t ON t.teacher_id = c.teacher_id
            WHERE ce.student_id = #{studentId}
              AND ce.status = 'ACTIVE'
              AND c.status = 'ACTIVE'
            ORDER BY c.week_day, c.start_time
            """)
    List<CourseResponse> findByStudentId(@Param("studentId") Long studentId);

    @Select("""
            SELECT
              c.course_id AS courseId,
              c.course_code AS courseCode,
              c.course_name AS courseName,
              c.teacher_id AS teacherId,
              t.name AS teacherName,
              c.location,
              c.week_day AS weekDay,
              c.section,
              c.start_time AS startTime,
              c.end_time AS endTime,
              c.semester,
              c.status
            FROM courses c
            JOIN teachers t ON t.teacher_id = c.teacher_id
            WHERE c.teacher_id = #{teacherId}
              AND c.status = 'ACTIVE'
            ORDER BY c.week_day, c.start_time
            """)
    List<CourseResponse> findByTeacherId(@Param("teacherId") Long teacherId);

    @Select("""
            SELECT
              c.course_id AS courseId,
              c.course_code AS courseCode,
              c.course_name AS courseName,
              c.teacher_id AS teacherId,
              t.name AS teacherName,
              c.location,
              c.week_day AS weekDay,
              c.section,
              c.start_time AS startTime,
              c.end_time AS endTime,
              c.semester,
              c.status
            FROM courses c
            JOIN teachers t ON t.teacher_id = c.teacher_id
            WHERE c.course_id = #{courseId}
            LIMIT 1
            """)
    CourseResponse findById(@Param("courseId") Long courseId);

    @Select("""
            SELECT COUNT(1)
            FROM course_enrollments
            WHERE course_id = #{courseId}
              AND student_id = #{studentId}
              AND status = 'ACTIVE'
            """)
    int countActiveEnrollment(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
