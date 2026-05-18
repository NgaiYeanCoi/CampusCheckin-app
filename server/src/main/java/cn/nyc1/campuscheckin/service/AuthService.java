package cn.nyc1.campuscheckin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.nyc1.campuscheckin.common.RoleConstants;
import cn.nyc1.campuscheckin.dto.LoginRequest;
import cn.nyc1.campuscheckin.dto.LoginResponse;
import cn.nyc1.campuscheckin.entity.Student;
import cn.nyc1.campuscheckin.entity.Teacher;
import cn.nyc1.campuscheckin.entity.User;
import cn.nyc1.campuscheckin.mapper.StudentMapper;
import cn.nyc1.campuscheckin.mapper.TeacherMapper;
import cn.nyc1.campuscheckin.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserMapper userMapper,
            StudentMapper studentMapper,
            TeacherMapper teacherMapper,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        if (!user.getRole().equals(request.getRole())) {
            throw new IllegalArgumentException("登录角色不匹配");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("账号或密码错误");
        }

        StpUtil.login(user.getUserId());
        userMapper.updateLastLoginAt(user.getUserId());
        return buildLoginResponse(user, StpUtil.getTokenValue());
    }

    public LoginResponse currentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return buildLoginResponse(user, StpUtil.getTokenValue());
    }

    public void logout() {
        StpUtil.logout();
    }

    public Long currentUserId() {
        StpUtil.checkLogin();
        return StpUtil.getLoginIdAsLong();
    }

    public Student currentStudent() {
        User user = requireCurrentUserRole(RoleConstants.STUDENT);
        Student student = studentMapper.findByUserId(user.getUserId());
        if (student == null) {
            throw new IllegalArgumentException("学生资料不存在");
        }
        return student;
    }

    public Teacher currentTeacher() {
        User user = requireCurrentUserRole(RoleConstants.TEACHER);
        Teacher teacher = teacherMapper.findByUserId(user.getUserId());
        if (teacher == null) {
            throw new IllegalArgumentException("教师资料不存在");
        }
        return teacher;
    }

    private User requireCurrentUserRole(String role) {
        Long userId = currentUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!role.equals(user.getRole())) {
            throw new IllegalArgumentException("当前角色无权执行该操作");
        }
        return user;
    }

    private LoginResponse buildLoginResponse(User user, String token) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        if (RoleConstants.STUDENT.equals(user.getRole())) {
            Student student = studentMapper.findByUserId(user.getUserId());
            if (student != null) {
                response.setProfileId(student.getStudentId());
                response.setDisplayName(student.getName());
            }
        } else if (RoleConstants.TEACHER.equals(user.getRole())) {
            Teacher teacher = teacherMapper.findByUserId(user.getUserId());
            if (teacher != null) {
                response.setProfileId(teacher.getTeacherId());
                response.setDisplayName(teacher.getName());
            }
        }

        return response;
    }
}
