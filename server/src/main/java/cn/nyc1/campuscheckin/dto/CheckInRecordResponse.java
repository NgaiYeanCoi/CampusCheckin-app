package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "学生签到记录")
public class CheckInRecordResponse {

    @Schema(description = "签到记录 ID", example = "1")
    private Long recordId;

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "签到任务标题", example = "Android应用开发 今日课堂签到")
    private String taskTitle;

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "签到时间", example = "2026-05-17T14:30:00")
    private LocalDateTime checkInTime;

    @Schema(description = "签到状态", example = "SIGNED", allowableValues = {"SIGNED", "LATE", "ABSENT", "EXCEPTION"})
    private String status;

    @Schema(description = "备注", example = "按时签到")
    private String remark;

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

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
