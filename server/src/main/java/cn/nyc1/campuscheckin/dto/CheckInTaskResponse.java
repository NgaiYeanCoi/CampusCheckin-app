package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "签到任务信息")
public class CheckInTaskResponse {

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "签到任务标题", example = "Android应用开发 今日课堂签到")
    private String title;

    @Schema(description = "签到方式", example = "PASSWORD", allowableValues = {"PASSWORD", "QR_CODE"})
    private String checkInType;

    @Schema(description = "签到开始时间", example = "2026-05-17T14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "签到截止时间", example = "2026-05-17T15:00:00")
    private LocalDateTime endTime;

    @Schema(description = "签到任务状态", example = "ACTIVE", allowableValues = {"NOT_STARTED", "ACTIVE", "ENDED", "CANCELLED"})
    private String status;

    @Schema(description = "应到人数", example = "4")
    private Integer totalCount;

    @Schema(description = "已提交人数，包含正常签到和迟到", example = "2")
    private Integer submittedCount;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(Integer submittedCount) {
        this.submittedCount = submittedCount;
    }
}
