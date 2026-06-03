package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.FormatUtils;
import cn.nyc1.myapplication.utils.SessionManager;

public class CourseDetailActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private TextView textCourseName;
    private TextView textCourseMeta;
    private TextView textTaskTitle;
    private TextView textTaskStatus;
    private TextView textTaskTime;
    private MaterialButton buttonCheckIn;
    private long courseId;
    private String courseName;
    private CheckInTask activeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        sessionManager = new SessionManager(this);
        courseId = getIntent().getLongExtra("courseId", -1);
        courseName = getIntent().getStringExtra("courseName");

        textCourseName = findViewById(R.id.textCourseName);
        textCourseMeta = findViewById(R.id.textCourseMeta);
        textTaskTitle = findViewById(R.id.textTaskTitle);
        textTaskStatus = findViewById(R.id.textTaskStatus);
        textTaskTime = findViewById(R.id.textTaskTime);
        buttonCheckIn = findViewById(R.id.buttonCheckIn);
        buttonCheckIn.setEnabled(false);
        buttonCheckIn.setOnClickListener(v -> openCheckIn());

        textCourseName.setText(FormatUtils.safe(courseName));
        if (courseId <= 0) {
            textTaskStatus.setText("课程参数异常");
            return;
        }
        loadCourse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseId > 0) {
            loadActiveTask();
        }
    }

    private void loadCourse() {
        RetrofitClient.api().getCourseDetail(sessionManager.authHeader(), courseId)
                .enqueue(new SimpleCallback<Course>() {
                    @Override
                    public void onSuccess(Course data) {
                        if (data == null) {
                            return;
                        }
                        courseName = data.courseName;
                        textCourseName.setText(FormatUtils.safe(data.courseName));
                        textCourseMeta.setText(FormatUtils.safe(data.teacherName) + " · "
                                + FormatUtils.safe(data.location) + "\n"
                                + FormatUtils.courseTime(data.weekDay, data.section, data.startTime, data.endTime));
                    }

                    @Override
                    public void onError(String message) {
                        textCourseMeta.setText(message);
                    }
                });
    }

    private void loadActiveTask() {
        textTaskStatus.setText("正在查询签到任务...");
        RetrofitClient.api().getActiveCheckInTask(sessionManager.authHeader(), courseId)
                .enqueue(new SimpleCallback<CheckInTask>() {
                    @Override
                    public void onSuccess(CheckInTask data) {
                        activeTask = data;
                        if (data == null) {
                            textTaskTitle.setText("当前没有可签到任务");
                            textTaskStatus.setText("未开始");
                            textTaskStatus.setBackgroundResource(R.drawable.bg_status_neutral);
                            textTaskTime.setText("教师发起签到后，这里会显示课堂口令签到入口。");
                            buttonCheckIn.setText("输入口令签到");
                            buttonCheckIn.setEnabled(false);
                            return;
                        }
                        textTaskTitle.setText(FormatUtils.safe(data.title));
                        textTaskTime.setText(FormatUtils.safe(data.startTime) + " 至 " + FormatUtils.safe(data.endTime));
                        applyTaskStatus(data.status);
                        if ("ACTIVE".equals(data.status)) {
                            checkCurrentStudentRecord(data.taskId);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        textTaskTitle.setText("签到任务加载失败");
                        textTaskStatus.setText("异常");
                        textTaskStatus.setBackgroundResource(R.drawable.bg_status_error);
                        textTaskTime.setText(message);
                        buttonCheckIn.setText("输入口令签到");
                        buttonCheckIn.setEnabled(false);
                    }
                });
    }

    private void checkCurrentStudentRecord(Long taskId) {
        if (taskId == null) {
            return;
        }
        RetrofitClient.api().getStudentCheckInRecords(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<CheckInRecord>>() {
                    @Override
                    public void onSuccess(List<CheckInRecord> data) {
                        CheckInRecord matchedRecord = findRecord(data, taskId);
                        if (matchedRecord != null) {
                            textTaskStatus.setText(FormatUtils.statusText(matchedRecord.status));
                            textTaskStatus.setBackgroundResource(R.drawable.bg_status_success);
                            String checkInTime = FormatUtils.safe(matchedRecord.checkInTime);
                            textTaskTime.setText("已提交签到：" + checkInTime + "\n" + FormatUtils.safe(matchedRecord.remark));
                            buttonCheckIn.setText("已完成签到");
                            buttonCheckIn.setEnabled(false);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        textTaskTime.setText(textTaskTime.getText() + "\n签到记录校验失败：" + message);
                    }
                });
    }

    private CheckInRecord findRecord(List<CheckInRecord> records, Long taskId) {
        if (records == null) {
            return null;
        }
        for (CheckInRecord record : records) {
            if (record != null && taskId.equals(record.taskId)) {
                return record;
            }
        }
        return null;
    }

    private void applyTaskStatus(String status) {
        textTaskStatus.setText(FormatUtils.statusText(status));
        textTaskStatus.setBackgroundResource("ACTIVE".equals(status)
                ? R.drawable.bg_status_success
                : R.drawable.bg_status_neutral);
        buttonCheckIn.setText("输入口令签到");
        buttonCheckIn.setEnabled("ACTIVE".equals(status));
    }

    private void openCheckIn() {
        if (activeTask == null || activeTask.taskId == null) {
            return;
        }
        Intent intent = new Intent(this, CheckInConfirmActivity.class);
        intent.putExtra("taskId", activeTask.taskId);
        intent.putExtra("courseName", courseName);
        startActivity(intent);
    }
}
