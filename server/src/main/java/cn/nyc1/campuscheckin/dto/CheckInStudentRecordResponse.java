package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "签到任务学生明细")
public class CheckInStudentRecordResponse {

    @Schema(description = "学生 ID", example = "1")
    private Long studentId;

    @Schema(description = "学号", example = "20260001")
    private String studentNo;

    @Schema(description = "学生姓名", example = "林一凡")
    private String studentName;

    @Schema(description = "班级", example = "软件工程2301班")
    private String className;

    @Schema(description = "签到时间，未签到时为空", example = "2026-05-20T19:30:00")
    private LocalDateTime checkInTime;

    @Schema(description = "签到状态", example = "SIGNED", allowableValues = {"UNSIGNED", "SIGNED", "LATE", "ABSENT", "EXCEPTION"})
    private String status;

    @Schema(description = "备注", example = "按时签到")
    private String remark;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
