package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CheckInTaskAdapter;
import cn.nyc1.myapplication.adapter.CourseAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class TeacherHomeActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private CourseAdapter courseAdapter;
    private CheckInTaskAdapter taskAdapter;
    private TextView textSummary;
    private TextView textHomeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        sessionManager = new SessionManager(this);
        TextView textGreeting = findViewById(R.id.textGreeting);
        textSummary = findViewById(R.id.textSummary);
        textHomeStatus = findViewById(R.id.textHomeStatus);
        TextView buttonProfile = findViewById(R.id.buttonProfile);
        MaterialButton buttonCreateTask = findViewById(R.id.buttonCreateTask);
        RecyclerView recyclerTasks = findViewById(R.id.recyclerTasks);
        RecyclerView recyclerCourses = findViewById(R.id.recyclerCourses);

        textGreeting.setText("课堂签到管理.");
        textSummary.setText(sessionManager.getDisplayName() + " · 教师端");
        buttonProfile.setText(profileText());

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new CheckInTaskAdapter(this::confirmEndTask);
        recyclerTasks.setAdapter(taskAdapter);

        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(course -> {
            Intent intent = new Intent(this, AttendanceStatsActivity.class);
            intent.putExtra("courseId", course.courseId);
            intent.putExtra("courseName", course.courseName);
            startActivity(intent);
        });
        recyclerCourses.setAdapter(courseAdapter);

        buttonProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        buttonCreateTask.setOnClickListener(v -> startActivity(new Intent(this, CreateCheckInActivity.class)));
        loadCourses();
        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseAdapter != null) {
            loadCourses();
        }
        if (taskAdapter != null) {
            loadTasks();
        }
    }

    private void loadCourses() {
        textHomeStatus.setText("正在加载授课课程...");
        RetrofitClient.api().getTeacherCourses(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        courseAdapter.submitList(data);
                        int count = data == null ? 0 : data.size();
                        textHomeStatus.setText(count == 0 ? "暂无授课课程" : "点击课程查看考勤统计");
                    }

                    @Override
                    public void onError(String message) {
                        courseAdapter.submitList(PreviewData.courses());
                        textHomeStatus.setText(message + "，当前仅展示离线预览数据");
                    }
                });
    }

    private void loadTasks() {
        RetrofitClient.api().getTeacherCheckInTasks(sessionManager.authHeader(), null)
                .enqueue(new SimpleCallback<List<CheckInTask>>() {
                    @Override
                    public void onSuccess(List<CheckInTask> data) {
                        taskAdapter.submitList(data);
                    }

                    @Override
                    public void onError(String message) {
                        taskAdapter.submitList(PreviewData.tasks());
                        textHomeStatus.setText(message + "，任务列表当前仅展示离线预览数据");
                    }
                });
    }

    private void confirmEndTask(CheckInTask task) {
        if (task == null || task.taskId == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("手动截止签到")
                .setMessage("截止后学生将不能继续签到，是否确认？")
                .setNegativeButton("取消", null)
                .setPositiveButton("截止", (dialog, which) -> endTask(task.taskId))
                .show();
    }

    private void endTask(long taskId) {
        textHomeStatus.setText("正在截止签到任务...");
        RetrofitClient.api().endCheckInTask(sessionManager.authHeader(), taskId)
                .enqueue(new SimpleCallback<CheckInTask>() {
                    @Override
                    public void onSuccess(CheckInTask data) {
                        textHomeStatus.setText("签到任务已截止");
                        loadTasks();
                    }

                    @Override
                    public void onError(String message) {
                        textHomeStatus.setText(message);
                    }
                });
    }

    private String profileText() {
        String name = sessionManager.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            return "我";
        }
        return name.trim().substring(0, 1);
    }
}
