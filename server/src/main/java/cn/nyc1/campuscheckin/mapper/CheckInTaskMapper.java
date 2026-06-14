package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.ActiveCheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskResponse;
import cn.nyc1.campuscheckin.entity.CheckInTask;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CheckInTaskMapper {

    @Select("""
            SELECT task_id, course_id, title, check_in_type, password, qr_token, start_time, end_time, status, created_by
            FROM check_in_tasks
            WHERE task_id = #{taskId}
            LIMIT 1
            """)
    CheckInTask findById(@Param("taskId") Long taskId);

    @Select("""
            SELECT
              t.task_id AS taskId,
              t.course_id AS courseId,
              c.course_name AS courseName,
              t.title,
              t.check_in_type AS checkInType,
              t.start_time AS startTime,
              t.end_time AS endTime,
              t.status
            FROM check_in_tasks t
            JOIN courses c ON c.course_id = t.course_id
            WHERE t.course_id = #{courseId}
              AND t.status = 'ACTIVE'
              AND CURRENT_TIMESTAMP BETWEEN t.start_time AND t.end_time
            ORDER BY t.start_time DESC
            LIMIT 1
            """)
    CheckInTaskResponse findActiveResponseByCourseId(@Param("courseId") Long courseId);

    @Select("""
            SELECT
              t.task_id AS taskId,
              t.course_id AS courseId,
              c.course_name AS courseName,
              te.name AS teacherName,
              t.title,
              t.check_in_type AS checkInType,
              t.start_time AS startTime,
              t.end_time AS endTime,
              CASE
                WHEN t.status = 'CANCELLED' THEN 'CANCELLED'
                WHEN t.status = 'ENDED' THEN 'ENDED'
                WHEN CURRENT_TIMESTAMP < t.start_time THEN 'NOT_STARTED'
                WHEN CURRENT_TIMESTAMP > t.end_time THEN 'ENDED'
                ELSE 'ACTIVE'
              END AS taskStatus,
              COALESCE(r.status, 'UNSIGNED') AS recordStatus,
              CASE WHEN r.status IN ('SIGNED', 'LATE') THEN TRUE ELSE FALSE END AS signed
            FROM course_enrollments ce
            JOIN courses c ON c.course_id = ce.course_id
            JOIN teachers te ON te.teacher_id = c.teacher_id
            JOIN check_in_tasks t ON t.course_id = c.course_id
            LEFT JOIN check_in_records r
              ON r.task_id = t.task_id
              AND r.student_id = ce.student_id
            WHERE ce.student_id = #{studentId}
              AND ce.status = 'ACTIVE'
              AND c.status = 'ACTIVE'
              AND t.status IN ('NOT_STARTED', 'ACTIVE')
              AND CURRENT_TIMESTAMP <= t.end_time
            ORDER BY
              CASE
                WHEN r.status IN ('SIGNED', 'LATE') THEN 1
                ELSE 0
              END,
              t.end_time ASC,
              t.start_time ASC,
              t.task_id DESC
            """)
    List<ActiveCheckInTaskResponse> findActiveResponsesByStudentId(@Param("studentId") Long studentId);

    @Select("""
            <script>
            SELECT
              t.task_id AS taskId,
              t.course_id AS courseId,
              c.course_name AS courseName,
              t.title,
              t.check_in_type AS checkInType,
              t.start_time AS startTime,
              t.end_time AS endTime,
              CASE
                WHEN t.status = 'CANCELLED' THEN 'CANCELLED'
                WHEN t.status = 'ENDED' THEN 'ENDED'
                WHEN CURRENT_TIMESTAMP &lt; t.start_time THEN 'NOT_STARTED'
                WHEN CURRENT_TIMESTAMP &gt; t.end_time THEN 'ENDED'
                ELSE 'ACTIVE'
              END AS status,
              (
                SELECT COUNT(*)
                FROM course_enrollments ce
                WHERE ce.course_id = t.course_id
                  AND ce.status = 'ACTIVE'
              ) AS totalCount,
              (
                SELECT COUNT(*)
                FROM check_in_records r
                WHERE r.task_id = t.task_id
                  AND r.status IN ('SIGNED', 'LATE')
              ) AS submittedCount
            FROM check_in_tasks t
            JOIN courses c ON c.course_id = t.course_id
            WHERE c.teacher_id = #{teacherId}
            <if test="courseId != null">
              AND t.course_id = #{courseId}
            </if>
            ORDER BY t.start_time DESC, t.task_id DESC
            </script>
            """)
    List<CheckInTaskResponse> findResponsesByTeacherId(
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId
    );

    @Select("""
            SELECT
              t.task_id AS taskId,
              t.course_id AS courseId,
              c.course_name AS courseName,
              t.title,
              t.check_in_type AS checkInType,
              t.start_time AS startTime,
              t.end_time AS endTime,
              CASE
                WHEN t.status = 'CANCELLED' THEN 'CANCELLED'
                WHEN t.status = 'ENDED' THEN 'ENDED'
                WHEN CURRENT_TIMESTAMP < t.start_time THEN 'NOT_STARTED'
                WHEN CURRENT_TIMESTAMP > t.end_time THEN 'ENDED'
                ELSE 'ACTIVE'
              END AS status,
              (
                SELECT COUNT(*)
                FROM course_enrollments ce
                WHERE ce.course_id = t.course_id
                  AND ce.status = 'ACTIVE'
              ) AS totalCount,
              (
                SELECT COUNT(*)
                FROM check_in_records r
                WHERE r.task_id = t.task_id
                  AND r.status IN ('SIGNED', 'LATE')
              ) AS submittedCount
            FROM check_in_tasks t
            JOIN courses c ON c.course_id = t.course_id
            WHERE t.task_id = #{taskId}
              AND c.teacher_id = #{teacherId}
            LIMIT 1
            """)
    CheckInTaskResponse findResponseByIdAndTeacherId(
            @Param("taskId") Long taskId,
            @Param("teacherId") Long teacherId
    );

    @Insert("""
            INSERT INTO check_in_tasks (course_id, title, check_in_type, password, qr_token, start_time, end_time, status, created_by)
            VALUES (#{courseId}, #{title}, #{checkInType}, #{password}, #{qrToken}, #{startTime}, #{endTime}, #{status}, #{createdBy})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insert(CheckInTask task);

    @Update("""
            UPDATE check_in_tasks t
            JOIN courses c ON c.course_id = t.course_id
            SET t.status = 'ENDED',
                t.end_time = #{endTime}
            WHERE t.task_id = #{taskId}
              AND c.teacher_id = #{teacherId}
              AND t.status IN ('NOT_STARTED', 'ACTIVE')
              AND t.start_time < #{endTime}
            """)
    int endTask(
            @Param("taskId") Long taskId,
            @Param("teacherId") Long teacherId,
            @Param("endTime") LocalDateTime endTime
    );
}
