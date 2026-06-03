package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.ActiveCheckInTaskAdapter;
import cn.nyc1.myapplication.adapter.CourseAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.ActiveCheckInTask;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class StudentHomeActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private CourseAdapter courseAdapter;
    private ActiveCheckInTaskAdapter activeTaskAdapter;
    private TextView textSummary;
    private TextView textHomeStatus;
    private int courseCount;
    private int activeTaskCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        sessionManager = new SessionManager(this);
        TextView textGreeting = findViewById(R.id.textGreeting);
        textSummary = findViewById(R.id.textSummary);
        textHomeStatus = findViewById(R.id.textHomeStatus);
        MaterialButton buttonSchedule = findViewById(R.id.buttonSchedule);
        MaterialButton buttonRecords = findViewById(R.id.buttonRecords);
        TextView buttonProfile = findViewById(R.id.buttonProfile);
        RecyclerView recyclerActiveTasks = findViewById(R.id.recyclerActiveTasks);
        RecyclerView recyclerCourses = findViewById(R.id.recyclerCourses);

        textGreeting.setText("你好，" + sessionManager.getDisplayName());
        buttonProfile.setText(profileText());
        recyclerActiveTasks.setLayoutManager(new LinearLayoutManager(this));
        activeTaskAdapter = new ActiveCheckInTaskAdapter(this::openActiveTask);
        recyclerActiveTasks.setAdapter(activeTaskAdapter);

        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(course -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.courseId);
            intent.putExtra("courseName", course.courseName);
            startActivity(intent);
        });
        recyclerCourses.setAdapter(courseAdapter);

        buttonSchedule.setOnClickListener(v -> startActivity(new Intent(this, ScheduleActivity.class)));
        buttonRecords.setOnClickListener(v -> startActivity(new Intent(this, CheckInRecordActivity.class)));
        buttonProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        loadDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseAdapter != null) {
            loadDashboard();
        }
    }

    private void loadDashboard() {
        loadActiveTasks();
        loadCourses();
    }

    private void loadCourses() {
        RetrofitClient.api().getStudentCourses(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        courseAdapter.submitList(data);
                        courseCount = data == null ? 0 : data.size();
                        updateSummary();
                    }

                    @Override
                    public void onError(String message) {
                        List<Course> preview = PreviewData.courses();
                        courseAdapter.submitList(preview);
                        courseCount = preview.size();
                        textSummary.setText("离线预览课程");
                    }
                });
    }

    private void loadActiveTasks() {
        textHomeStatus.setText("正在加载当前签到任务...");
        RetrofitClient.api().getActiveStudentCheckInTasks(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<ActiveCheckInTask>>() {
                    @Override
                    public void onSuccess(List<ActiveCheckInTask> data) {
                        activeTaskAdapter.submitList(data);
                        activeTaskCount = data == null ? 0 : data.size();
                        updateSummary();
                        textHomeStatus.setText(activeTaskCount == 0
                                ? "暂无可签到任务，教师发布后会显示在这里"
                                : "已聚合所有已选课程的签到任务");
                    }

                    @Override
                    public void onError(String message) {
                        activeTaskAdapter.submitList(null);
                        activeTaskCount = 0;
                        textHomeStatus.setText(message + "，当前无法加载签到任务");
                    }
                });
    }

    private void updateSummary() {
        textSummary.setText("当前共有 " + courseCount + " 门课程 · " + activeTaskCount + " 个签到任务");
    }

    private void openActiveTask(ActiveCheckInTask task) {
        if (task == null) {
            return;
        }
        if (!task.canSubmit()) {
            Toast.makeText(this, "当前任务" + statusForToast(task), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CheckInConfirmActivity.class);
        intent.putExtra("taskId", task.taskId == null ? -1 : task.taskId);
        intent.putExtra("courseName", task.courseName);
        startActivity(intent);
    }

    private String statusForToast(ActiveCheckInTask task) {
        if (task.signed) {
            return "已完成";
        }
        if ("NOT_STARTED".equals(task.taskStatus)) {
            return "未开始";
        }
        return "不可签到";
    }

    private String profileText() {
        String name = sessionManager.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            return "我";
        }
        return name.trim().substring(0, 1);
    }
}
