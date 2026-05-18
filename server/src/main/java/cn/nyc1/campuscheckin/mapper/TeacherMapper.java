package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeacherMapper {

    @Select("""
            SELECT teacher_id, user_id, teacher_no, name, department, title, phone, avatar_text
            FROM teachers
            WHERE user_id = #{userId}
            LIMIT 1
            """)
    Teacher findByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT teacher_id, user_id, teacher_no, name, department, title, phone, avatar_text
            FROM teachers
            WHERE teacher_id = #{teacherId}
            LIMIT 1
            """)
    Teacher findById(@Param("teacherId") Long teacherId);
}
