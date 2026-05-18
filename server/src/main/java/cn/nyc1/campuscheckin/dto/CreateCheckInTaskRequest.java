package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "教师发起签到请求")
public class CreateCheckInTaskRequest {

    @Schema(description = "课程 ID", example = "1")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Schema(description = "签到任务标题", example = "Android应用开发 临时签到")
    @NotBlank(message = "签到标题不能为空")
    private String title;

    @Schema(description = "课堂签到口令", example = "888888")
    @NotBlank(message = "签到口令不能为空")
    private String password;

    @Schema(description = "签到开始时间，ISO 日期时间", example = "2026-05-17T14:00:00")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "签到截止时间，ISO 日期时间", example = "2026-05-17T15:00:00")
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime endTime;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
