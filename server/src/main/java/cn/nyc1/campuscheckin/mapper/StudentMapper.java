package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper {

    @Select("""
            SELECT student_id, user_id, student_no, name, class_name, major, phone, avatar_text
            FROM students
            WHERE user_id = #{userId}
            LIMIT 1
            """)
    Student findByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT student_id, user_id, student_no, name, class_name, major, phone, avatar_text
            FROM students
            WHERE student_id = #{studentId}
            LIMIT 1
            """)
    Student findById(@Param("studentId") Long studentId);
}
