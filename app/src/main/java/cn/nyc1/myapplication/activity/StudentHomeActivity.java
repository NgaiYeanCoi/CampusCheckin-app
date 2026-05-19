package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CourseAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class StudentHomeActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private CourseAdapter courseAdapter;
    private TextView textSummary;
    private TextView textHomeStatus;

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
        RecyclerView recyclerCourses = findViewById(R.id.recyclerCourses);

        textGreeting.setText("你好，" + sessionManager.getDisplayName());
        buttonProfile.setText(profileText());
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
        loadCourses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseAdapter != null) {
            loadCourses();
        }
    }

    private void loadCourses() {
        textHomeStatus.setText("正在加载课程...");
        RetrofitClient.api().getStudentCourses(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        courseAdapter.submitList(data);
                        int count = data == null ? 0 : data.size();
                        textSummary.setText("当前共有 " + count + " 门课程");
                        textHomeStatus.setText(count == 0 ? "暂无课程数据" : "点击课程进入详情并查看签到任务");
                    }

                    @Override
                    public void onError(String message) {
                        List<Course> preview = PreviewData.courses();
                        courseAdapter.submitList(preview);
                        textSummary.setText("离线预览课程");
                        textHomeStatus.setText(message + "，当前仅展示离线预览数据");
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
