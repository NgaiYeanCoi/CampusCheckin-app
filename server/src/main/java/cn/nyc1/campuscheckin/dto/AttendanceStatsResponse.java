package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "课程考勤统计")
public class AttendanceStatsResponse {

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程编号", example = "CSE101")
    private String courseCode;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "签到任务 ID", example = "1")
    private Long taskId;

    @Schema(description = "签到任务标题", example = "Android应用开发 今日课堂签到")
    private String taskTitle;

    @Schema(description = "应到人数", example = "4")
    private Integer totalCount;

    @Schema(description = "已签到人数", example = "1")
    private Integer signedCount;

    @Schema(description = "迟到人数", example = "0")
    private Integer lateCount;

    @Schema(description = "缺勤人数", example = "0")
    private Integer absentCount;

    @Schema(description = "异常人数", example = "0")
    private Integer exceptionCount;

    @Schema(description = "出勤率百分比", example = "25.00")
    private BigDecimal attendanceRate;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSignedCount() {
        return signedCount;
    }

    public void setSignedCount(Integer signedCount) {
        this.signedCount = signedCount;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public Integer getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Integer absentCount) {
        this.absentCount = absentCount;
    }

    public Integer getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(Integer exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
