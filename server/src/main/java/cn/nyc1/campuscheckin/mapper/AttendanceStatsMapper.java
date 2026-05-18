package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.AttendanceStatsResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AttendanceStatsMapper {

    @Select("""
            SELECT
              course_id AS courseId,
              course_code AS courseCode,
              course_name AS courseName,
              task_id AS taskId,
              task_title AS taskTitle,
              total_count AS totalCount,
              signed_count AS signedCount,
              late_count AS lateCount,
              absent_count AS absentCount,
              exception_count AS exceptionCount,
              attendance_rate AS attendanceRate
            FROM v_course_attendance_stats
            WHERE course_id = #{courseId}
            ORDER BY task_id DESC
            """)
    List<AttendanceStatsResponse> findByCourseId(@Param("courseId") Long courseId);
}
