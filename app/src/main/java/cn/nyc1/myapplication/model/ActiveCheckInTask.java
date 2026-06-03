package cn.nyc1.myapplication.model;

public class ActiveCheckInTask {
    public Long taskId;
    public Long courseId;
    public String courseName;
    public String teacherName;
    public String title;
    public String checkInType;
    public String startTime;
    public String endTime;
    public String taskStatus;
    public String recordStatus;
    public boolean signed;

    public boolean canSubmit() {
        return "ACTIVE".equals(taskStatus) && !signed;
    }
}
