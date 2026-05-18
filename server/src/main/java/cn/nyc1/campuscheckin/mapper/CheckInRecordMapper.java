package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.CheckInRecordResponse;
import cn.nyc1.campuscheckin.entity.CheckInRecord;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckInRecordMapper {

    @Select("""
            SELECT record_id, task_id, course_id, student_id, check_in_time, status, remark
            FROM check_in_records
            WHERE task_id = #{taskId}
              AND student_id = #{studentId}
            LIMIT 1
            """)
    CheckInRecord findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") Long studentId);

    @Insert("""
            INSERT INTO check_in_records (task_id, course_id, student_id, check_in_time, status, remark)
            VALUES (#{taskId}, #{courseId}, #{studentId}, #{checkInTime}, #{status}, #{remark})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(CheckInRecord record);

    @Select("""
            SELECT
              r.record_id AS recordId,
              r.task_id AS taskId,
              t.title AS taskTitle,
              r.course_id AS courseId,
              c.course_name AS courseName,
              r.check_in_time AS checkInTime,
              r.status,
              r.remark
            FROM check_in_records r
            JOIN check_in_tasks t ON t.task_id = r.task_id
            JOIN courses c ON c.course_id = r.course_id
            WHERE r.student_id = #{studentId}
            ORDER BY t.start_time DESC, r.record_id DESC
            """)
    List<CheckInRecordResponse> findResponsesByStudentId(@Param("studentId") Long studentId);
}
