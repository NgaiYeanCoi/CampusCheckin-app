package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.dto.CheckInTaskResponse;
import cn.nyc1.campuscheckin.entity.CheckInTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckInTaskMapper {

    @Select("""
            SELECT task_id, course_id, title, password, start_time, end_time, status, created_by
            FROM check_in_tasks
            WHERE task_id = #{taskId}
            LIMIT 1
            """)
    CheckInTask findById(@Param("taskId") Long taskId);

    @Select("""
            SELECT
              task_id AS taskId,
              course_id AS courseId,
              title,
              start_time AS startTime,
              end_time AS endTime,
              status
            FROM check_in_tasks
            WHERE course_id = #{courseId}
              AND status = 'ACTIVE'
              AND CURRENT_TIMESTAMP BETWEEN start_time AND end_time
            ORDER BY start_time DESC
            LIMIT 1
            """)
    CheckInTaskResponse findActiveResponseByCourseId(@Param("courseId") Long courseId);

    @Insert("""
            INSERT INTO check_in_tasks (course_id, title, password, start_time, end_time, status, created_by)
            VALUES (#{courseId}, #{title}, #{password}, #{startTime}, #{endTime}, #{status}, #{createdBy})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insert(CheckInTask task);
}
