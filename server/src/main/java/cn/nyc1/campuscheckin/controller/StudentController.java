package cn.nyc1.campuscheckin.controller;

import cn.nyc1.campuscheckin.common.ApiResponse;
import cn.nyc1.campuscheckin.config.OpenApiConfig;
import cn.nyc1.campuscheckin.dto.ActiveCheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CheckInRecordResponse;
import cn.nyc1.campuscheckin.dto.CheckInResultResponse;
import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.dto.SubmitCheckInRequest;
import cn.nyc1.campuscheckin.service.CheckInService;
import cn.nyc1.campuscheckin.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@Tag(name = "学生端接口", description = "学生课程、课表、签到和签到记录")
@SecurityRequirement(name = OpenApiConfig.AUTHORIZATION_SCHEME)
public class StudentController {

    private final CourseService courseService;
    private final CheckInService checkInService;

    public StudentController(CourseService courseService, CheckInService checkInService) {
        this.courseService = courseService;
        this.checkInService = checkInService;
    }

    @GetMapping("/courses")
    @Operation(summary = "学生课程列表", description = "查询当前学生已选课程列表。")
    public ApiResponse<List<CourseResponse>> courses() {
        return ApiResponse.success(courseService.studentCourses());
    }

    @GetMapping("/schedule")
    @Operation(summary = "学生周课表", description = "查询当前学生周课表数据，Android 端可按 weekDay 和 section 渲染。")
    public ApiResponse<List<CourseResponse>> schedule() {
        return ApiResponse.success(courseService.studentSchedule());
    }

    @GetMapping("/check-in-tasks/active")
    @Operation(
            summary = "学生当前可签到任务",
            description = "聚合查询当前学生所有已选课程中未截止的签到任务，用于学生首页展示，避免只展示固定课程。"
    )
    public ApiResponse<List<ActiveCheckInTaskResponse>> activeCheckInTasks() {
        return ApiResponse.success(checkInService.activeTasksForCurrentStudent());
    }

    @PostMapping("/check-in")
    @Operation(summary = "学生提交签到", description = "学生输入签到任务 ID 和课堂口令完成签到。")
    public ApiResponse<CheckInResultResponse> submitCheckIn(@Valid @RequestBody SubmitCheckInRequest request) {
        return ApiResponse.success(checkInService.submit(request));
    }

    @GetMapping("/check-in-records")
    @Operation(summary = "学生签到记录", description = "查询当前学生历史签到记录。")
    public ApiResponse<List<CheckInRecordResponse>> checkInRecords(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ApiResponse.success(checkInService.studentRecords(courseId, status, startDate, endDate));
    }
}
