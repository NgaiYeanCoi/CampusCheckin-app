package cn.nyc1.campuscheckin.controller;

import cn.nyc1.campuscheckin.common.ApiResponse;
import cn.nyc1.campuscheckin.config.OpenApiConfig;
import cn.nyc1.campuscheckin.dto.AttendanceStatsResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.dto.CreateCheckInTaskRequest;
import cn.nyc1.campuscheckin.service.AttendanceService;
import cn.nyc1.campuscheckin.service.CheckInService;
import cn.nyc1.campuscheckin.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teacher")
@Tag(name = "教师端接口", description = "教师课程、发起签到和考勤统计")
@SecurityRequirement(name = OpenApiConfig.AUTHORIZATION_SCHEME)
public class TeacherController {

    private final CourseService courseService;
    private final CheckInService checkInService;
    private final AttendanceService attendanceService;

    public TeacherController(
            CourseService courseService,
            CheckInService checkInService,
            AttendanceService attendanceService
    ) {
        this.courseService = courseService;
        this.checkInService = checkInService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/courses")
    @Operation(summary = "教师课程列表", description = "查询当前教师负责的课程列表。")
    public ApiResponse<List<CourseResponse>> courses() {
        return ApiResponse.success(courseService.teacherCourses());
    }

    @PostMapping("/check-in-tasks")
    @Operation(summary = "教师发起签到", description = "教师为自己的课程创建课堂口令签到任务。")
    public ApiResponse<CheckInTaskResponse> createCheckInTask(@Valid @RequestBody CreateCheckInTaskRequest request) {
        return ApiResponse.success(checkInService.createTask(request));
    }

    @GetMapping("/check-in-tasks")
    @Operation(summary = "教师签到任务列表", description = "查询当前教师创建的签到任务列表，可按课程筛选。")
    public ApiResponse<List<CheckInTaskResponse>> checkInTasks(
            @Parameter(description = "课程 ID，可选", example = "1") @RequestParam(required = false) Long courseId
    ) {
        return ApiResponse.success(checkInService.teacherTasks(courseId));
    }

    @PostMapping("/check-in-tasks/{taskId}/end")
    @Operation(summary = "手动截止签到任务", description = "教师手动截止自己课程下正在进行的签到任务。")
    public ApiResponse<CheckInTaskResponse> endCheckInTask(
            @Parameter(description = "签到任务 ID", example = "1") @PathVariable Long taskId
    ) {
        return ApiResponse.success(checkInService.endTask(taskId));
    }

    @GetMapping("/courses/{courseId}/attendance-stats")
    @Operation(summary = "课程考勤统计", description = "教师查看指定课程的签到任务统计数据。")
    public ApiResponse<List<AttendanceStatsResponse>> attendanceStats(
            @Parameter(description = "课程 ID", example = "1") @PathVariable Long courseId
    ) {
        return ApiResponse.success(attendanceService.courseStats(courseId));
    }
}
