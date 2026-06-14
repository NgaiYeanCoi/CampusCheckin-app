package cn.nyc1.myapplication.model;

import java.util.List;

public class CheckInTaskDetail {
    public Long taskId;
    public Long courseId;
    public String courseName;
    public String title;
    public String checkInType;
    public String qrPayload;
    public String startTime;
    public String endTime;
    public String status;
    public CheckInTaskSummary summary;
    public List<CheckInStudentRecord> students;
}
