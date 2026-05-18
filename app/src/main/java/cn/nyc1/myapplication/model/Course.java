package cn.nyc1.myapplication.model;

public class Course {
    public Long courseId;
    public String courseCode;
    public String courseName;
    public Long teacherId;
    public String teacherName;
    public String location;
    public Integer weekDay;
    public String section;
    public String startTime;
    public String endTime;
    public String semester;
    public String status;

    @Override
    public String toString() {
        return courseName == null ? "课程" : courseName;
    }
}
