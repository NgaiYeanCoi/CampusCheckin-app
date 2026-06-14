package cn.nyc1.myapplication.model;

public class CreateCheckInTaskRequest {
    public Long courseId;
    public String title;
    public String checkInType;
    public String password;
    public String startTime;
    public String endTime;

    public CreateCheckInTaskRequest(Long courseId, String title, String checkInType, String password, String startTime, String endTime) {
        this.courseId = courseId;
        this.title = title;
        this.checkInType = checkInType;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
