package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import cn.nyc1.myapplication.R;
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
        loadActiveTask();
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
                            buttonCheckIn.setEnabled(false);
                            return;
                        }
                        textTaskTitle.setText(FormatUtils.safe(data.title));
                        textTaskStatus.setText(FormatUtils.statusText(data.status));
                        textTaskStatus.setBackgroundResource("ACTIVE".equals(data.status)
                                ? R.drawable.bg_status_success
                                : R.drawable.bg_status_neutral);
                        textTaskTime.setText(FormatUtils.safe(data.startTime) + " 至 " + FormatUtils.safe(data.endTime));
                        buttonCheckIn.setEnabled("ACTIVE".equals(data.status));
                    }

                    @Override
                    public void onError(String message) {
                        textTaskTitle.setText("签到任务加载失败");
                        textTaskStatus.setText("异常");
                        textTaskStatus.setBackgroundResource(R.drawable.bg_status_error);
                        textTaskTime.setText(message);
                        buttonCheckIn.setEnabled(false);
                    }
                });
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
