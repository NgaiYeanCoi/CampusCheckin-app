package cn.nyc1.myapplication.model;

public class SubmitCheckInRequest {
    public Long taskId;
    public String password;

    public SubmitCheckInRequest(Long taskId, String password) {
        this.taskId = taskId;
        this.password = password;
    }
}
