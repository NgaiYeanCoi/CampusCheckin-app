package cn.nyc1.myapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.AttendanceStatsAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.AttendanceStats;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.FormatUtils;
import cn.nyc1.myapplication.utils.SessionManager;

public class AttendanceStatsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private AttendanceStatsAdapter adapter;
    private TextView textTitle;
    private TextView textStatus;
    private long courseId;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_stats);

        sessionManager = new SessionManager(this);
        courseId = getIntent().getLongExtra("courseId", -1);
        courseName = getIntent().getStringExtra("courseName");
        textTitle = findViewById(R.id.textTitle);
        textStatus = findViewById(R.id.textStatus);
        RecyclerView recyclerStats = findViewById(R.id.recyclerStats);

        textTitle.setText("考勤统计.");
        recyclerStats.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceStatsAdapter();
        recyclerStats.setAdapter(adapter);
        loadStats();
    }

    private void loadStats() {
        if (courseId <= 0) {
            textStatus.setText("课程参数异常");
            return;
        }
        textStatus.setText(FormatUtils.safe(courseName) + " · 正在加载统计...");
        RetrofitClient.api().getAttendanceStats(sessionManager.authHeader(), courseId)
                .enqueue(new SimpleCallback<List<AttendanceStats>>() {
                    @Override
                    public void onSuccess(List<AttendanceStats> data) {
                        adapter.submitList(data);
                        int count = data == null ? 0 : data.size();
                        textStatus.setText(FormatUtils.safe(courseName) + " · " + count + " 个签到任务");
                    }

                    @Override
                    public void onError(String message) {
                        adapter.submitList(PreviewData.stats());
                        textStatus.setText(message + "，当前仅展示离线预览数据");
                    }
                });
    }
}
