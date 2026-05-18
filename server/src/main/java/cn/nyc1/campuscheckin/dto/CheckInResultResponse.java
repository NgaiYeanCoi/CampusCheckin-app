package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "签到结果")
public class CheckInResultResponse {

    @Schema(description = "签到记录 ID", example = "10")
    private Long recordId;

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "签到时间", example = "2026-05-17T14:30:00")
    private LocalDateTime checkInTime;

    @Schema(description = "签到状态", example = "SIGNED", allowableValues = {"SIGNED", "LATE"})
    private String status;

    @Schema(description = "结果提示", example = "签到成功")
    private String message;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
