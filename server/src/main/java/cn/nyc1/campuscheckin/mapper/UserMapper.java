package cn.nyc1.campuscheckin.mapper;

import cn.nyc1.campuscheckin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT user_id, username, password_hash, role, status, last_login_at, created_at, updated_at
            FROM users
            WHERE username = #{username}
            LIMIT 1
            """)
    User findByUsername(@Param("username") String username);

    @Select("""
            SELECT user_id, username, password_hash, role, status, last_login_at, created_at, updated_at
            FROM users
            WHERE user_id = #{userId}
            LIMIT 1
            """)
    User findById(@Param("userId") Long userId);

    @Update("""
            UPDATE users
            SET last_login_at = CURRENT_TIMESTAMP
            WHERE user_id = #{userId}
            """)
    int updateLastLoginAt(@Param("userId") Long userId);
}
