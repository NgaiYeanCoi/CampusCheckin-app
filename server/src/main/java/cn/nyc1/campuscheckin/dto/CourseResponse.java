package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "课程信息")
public class CourseResponse {

    @Schema(description = "课程 ID", example = "1")
    private Long courseId;

    @Schema(description = "课程编号", example = "CSE101")
    private String courseCode;

    @Schema(description = "课程名称", example = "Android应用开发")
    private String courseName;

    @Schema(description = "授课教师 ID", example = "1")
    private Long teacherId;

    @Schema(description = "授课教师姓名", example = "李老师")
    private String teacherName;

    @Schema(description = "上课地点", example = "教学楼A-301")
    private String location;

    @Schema(description = "星期几，1 表示周一", example = "1")
    private Integer weekDay;

    @Schema(description = "上课节次", example = "1-2节")
    private String section;

    @Schema(description = "上课开始时间", example = "08:00:00")
    private LocalTime startTime;

    @Schema(description = "上课结束时间", example = "09:40:00")
    private LocalTime endTime;

    @Schema(description = "学期", example = "2025-2026-2")
    private String semester;

    @Schema(description = "课程状态", example = "ACTIVE")
    private String status;

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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
