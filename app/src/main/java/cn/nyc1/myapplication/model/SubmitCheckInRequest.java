package cn.nyc1.myapplication.model;

public class SubmitCheckInRequest {
    public Long taskId;
    public String password;
    public String qrToken;

    public SubmitCheckInRequest(Long taskId, String password) {
        this.taskId = taskId;
        this.password = password;
    }

    public SubmitCheckInRequest(Long taskId, String password, String qrToken) {
        this.taskId = taskId;
        this.password = password;
        this.qrToken = qrToken;
    }
}
