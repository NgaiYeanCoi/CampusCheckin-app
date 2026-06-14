package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "签到任务详情")
public class CheckInTaskDetailResponse {

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "签到标题", example = "Android应用开发 今日课堂签到")
    private String title;

    @Schema(description = "签到方式", example = "QR_CODE", allowableValues = {"PASSWORD", "QR_CODE"})
    private String checkInType;

    @Schema(description = "教师端二维码内容，仅 QR_CODE 任务返回", example = "campuscheckin://check-in?taskId=1&token=abc")
    private String qrPayload;

    @Schema(description = "签到开始时间", example = "2026-05-20T19:00:00")
    private LocalDateTime startTime;

    @Schema(description = "签到截止时间", example = "2026-05-20T20:00:00")
    private LocalDateTime endTime;

    @Schema(description = "任务状态", example = "ACTIVE", allowableValues = {"NOT_STARTED", "ACTIVE", "ENDED", "CANCELLED"})
    private String status;

    @Schema(description = "统计汇总")
    private CheckInTaskSummaryResponse summary;

    @Schema(description = "学生签到明细")
    private List<CheckInStudentRecordResponse> students;

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

    public String getQrPayload() {
        return qrPayload;
    }

    public void setQrPayload(String qrPayload) {
        this.qrPayload = qrPayload;
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

    public CheckInTaskSummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(CheckInTaskSummaryResponse summary) {
        this.summary = summary;
    }

    public List<CheckInStudentRecordResponse> getStudents() {
        return students;
    }

    public void setStudents(List<CheckInStudentRecordResponse> students) {
        this.students = students;
    }
}
