package cn.nyc1.myapplication.model;

public class CreateCheckInTaskRequest {
    public Long courseId;
    public String title;
    public String password;
    public String startTime;
    public String endTime;

    public CreateCheckInTaskRequest(Long courseId, String title, String password, String startTime, String endTime) {
        this.courseId = courseId;
        this.title = title;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
