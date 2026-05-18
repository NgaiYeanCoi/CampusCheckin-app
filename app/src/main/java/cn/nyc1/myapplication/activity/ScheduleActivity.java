package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CourseAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class ScheduleActivity extends AppCompatActivity {
    private TextView textStatus;
    private CourseAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sessionManager = new SessionManager(this);
        textStatus = findViewById(R.id.textStatus);
        RecyclerView recyclerCourses = findViewById(R.id.recyclerCourses);
        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(course -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.courseId);
            intent.putExtra("courseName", course.courseName);
            startActivity(intent);
        });
        recyclerCourses.setAdapter(adapter);
        loadSchedule();
    }

    private void loadSchedule() {
        textStatus.setText("正在加载周课表...");
        RetrofitClient.api().getStudentSchedule(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        adapter.submitList(data);
                        textStatus.setText("按课程时间展示，点击课程可进入详情");
                    }

                    @Override
                    public void onError(String message) {
                        adapter.submitList(PreviewData.courses());
                        textStatus.setText(message + "，当前仅展示离线预览数据");
                    }
                });
    }
}
