package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "学生首页当前可签到任务")
public class ActiveCheckInTaskResponse {

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "课程 ID", example = "2")
    private Long courseId;

    @Schema(description = "课程名称", example = "Java程序设计")
    private String courseName;

    @Schema(description = "授课教师", example = "李老师")
    private String teacherName;

    @Schema(description = "签到标题", example = "Java程序设计 课堂签到")
    private String title;

    @Schema(description = "签到方式", example = "PASSWORD")
    private String checkInType;

    @Schema(description = "签到开始时间", example = "2026-05-19T20:22:00")
    private LocalDateTime startTime;

    @Schema(description = "签到截止时间", example = "2026-05-19T21:22:00")
    private LocalDateTime endTime;

    @Schema(description = "任务状态", example = "ACTIVE", allowableValues = {"NOT_STARTED", "ACTIVE", "ENDED", "CANCELLED"})
    private String taskStatus;

    @Schema(description = "当前学生签到状态", example = "UNSIGNED", allowableValues = {"UNSIGNED", "SIGNED", "LATE", "ABSENT", "EXCEPTION"})
    private String recordStatus;

    @Schema(description = "当前学生是否已完成有效签到", example = "false")
    private boolean signed;

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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }
}
