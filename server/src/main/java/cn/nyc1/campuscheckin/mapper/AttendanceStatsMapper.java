package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.AttendanceStatsResponse;
import cn.nyc1.campuscheckin.dto.CheckInStudentRecordResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskDetailResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskSummaryResponse;
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

    @Select("""
            SELECT
              t.task_id AS taskId,
              t.course_id AS courseId,
              c.course_name AS courseName,
              t.title,
              t.check_in_type AS checkInType,
              CASE
                WHEN t.check_in_type = 'QR_CODE'
                  THEN CONCAT('campuscheckin://check-in?taskId=', t.task_id, '&token=', t.qr_token)
                ELSE NULL
              END AS qrPayload,
              t.start_time AS startTime,
              t.end_time AS endTime,
              CASE
                WHEN t.status = 'CANCELLED' THEN 'CANCELLED'
                WHEN t.status = 'ENDED' THEN 'ENDED'
                WHEN CURRENT_TIMESTAMP < t.start_time THEN 'NOT_STARTED'
                WHEN CURRENT_TIMESTAMP > t.end_time THEN 'ENDED'
                ELSE 'ACTIVE'
              END AS status
            FROM check_in_tasks t
            JOIN courses c ON c.course_id = t.course_id
            WHERE t.task_id = #{taskId}
              AND c.teacher_id = #{teacherId}
            LIMIT 1
            """)
    CheckInTaskDetailResponse findTaskDetailHeader(
            @Param("taskId") Long taskId,
            @Param("teacherId") Long teacherId
    );

    @Select("""
            SELECT
              COUNT(ce.student_id) AS totalCount,
              COALESCE(SUM(CASE WHEN r.status = 'SIGNED' THEN 1 ELSE 0 END), 0) AS signedCount,
              COALESCE(SUM(CASE WHEN r.status = 'LATE' THEN 1 ELSE 0 END), 0) AS lateCount,
              COALESCE(SUM(
                CASE
                  WHEN r.record_id IS NULL
                    AND t.status NOT IN ('ENDED', 'CANCELLED')
                    AND CURRENT_TIMESTAMP <= t.end_time THEN 1
                  ELSE 0
                END
              ), 0) AS unsignedCount,
              COALESCE(SUM(
                CASE
                  WHEN r.status = 'ABSENT' THEN 1
                  WHEN r.record_id IS NULL
                    AND (t.status = 'ENDED' OR CURRENT_TIMESTAMP > t.end_time) THEN 1
                  ELSE 0
                END
              ), 0) AS absentCount,
              COALESCE(SUM(CASE WHEN r.status = 'EXCEPTION' THEN 1 ELSE 0 END), 0) AS exceptionCount,
              ROUND(
                CASE
                  WHEN COUNT(ce.student_id) = 0 THEN 0
                  ELSE (
                    COALESCE(SUM(CASE WHEN r.status IN ('SIGNED', 'LATE') THEN 1 ELSE 0 END), 0)
                    / COUNT(ce.student_id)
                  ) * 100
                END,
                2
              ) AS attendanceRate
            FROM check_in_tasks t
            JOIN course_enrollments ce
              ON ce.course_id = t.course_id
              AND ce.status = 'ACTIVE'
            LEFT JOIN check_in_records r
              ON r.task_id = t.task_id
              AND r.student_id = ce.student_id
            WHERE t.task_id = #{taskId}
            GROUP BY t.task_id
            """)
    CheckInTaskSummaryResponse findTaskSummary(@Param("taskId") Long taskId);

    @Select("""
            SELECT
              s.student_id AS studentId,
              s.student_no AS studentNo,
              s.name AS studentName,
              s.class_name AS className,
              r.check_in_time AS checkInTime,
              CASE
                WHEN r.status IS NOT NULL THEN r.status
                WHEN t.status = 'ENDED' OR CURRENT_TIMESTAMP > t.end_time THEN 'ABSENT'
                ELSE 'UNSIGNED'
              END AS status,
              CASE
                WHEN r.remark IS NOT NULL THEN r.remark
                WHEN t.status = 'ENDED' OR CURRENT_TIMESTAMP > t.end_time THEN '未提交签到'
                ELSE '等待签到'
              END AS remark
            FROM check_in_tasks t
            JOIN course_enrollments ce
              ON ce.course_id = t.course_id
              AND ce.status = 'ACTIVE'
            JOIN students s ON s.student_id = ce.student_id
            LEFT JOIN check_in_records r
              ON r.task_id = t.task_id
              AND r.student_id = s.student_id
            WHERE t.task_id = #{taskId}
            ORDER BY
              CASE
                WHEN r.status = 'SIGNED' THEN 0
                WHEN r.status = 'LATE' THEN 1
                WHEN r.status = 'EXCEPTION' THEN 2
                WHEN r.record_id IS NULL
                  AND NOT (t.status = 'ENDED' OR CURRENT_TIMESTAMP > t.end_time) THEN 3
                ELSE 4
              END,
              r.check_in_time ASC,
              s.student_no ASC
            """)
    List<CheckInStudentRecordResponse> findStudentRecordsByTaskId(@Param("taskId") Long taskId);
}
