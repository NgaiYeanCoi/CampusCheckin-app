package cn.nyc1.campuscheckin.controller;

import cn.nyc1.campuscheckin.common.ApiResponse;
import cn.nyc1.campuscheckin.config.OpenApiConfig;
import cn.nyc1.campuscheckin.dto.CheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.service.CheckInService;
import cn.nyc1.campuscheckin.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "课程通用接口", description = "课程详情和课程当前签到任务")
@SecurityRequirement(name = OpenApiConfig.AUTHORIZATION_SCHEME)
public class CourseController {

    private final CourseService courseService;
    private final CheckInService checkInService;

    public CourseController(CourseService courseService, CheckInService checkInService) {
        this.courseService = courseService;
        this.checkInService = checkInService;
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "课程详情", description = "查询指定课程的基础信息。")
    public ApiResponse<CourseResponse> detail(
            @Parameter(description = "课程 ID", example = "1") @PathVariable Long courseId
    ) {
        return ApiResponse.success(courseService.detail(courseId));
    }

    @GetMapping("/{courseId}/active-check-in-task")
    @Operation(summary = "当前可签到任务", description = "查询指定课程当前处于 ACTIVE 且在有效时间范围内的签到任务。")
    public ApiResponse<CheckInTaskResponse> activeCheckInTask(
            @Parameter(description = "课程 ID", example = "1") @PathVariable Long courseId
    ) {
        return ApiResponse.success(checkInService.activeTask(courseId));
    }
}
