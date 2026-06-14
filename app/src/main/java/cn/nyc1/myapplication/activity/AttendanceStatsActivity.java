package cn.nyc1.myapplication.activity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.AttendanceStatsAdapter;
import cn.nyc1.myapplication.model.AttendanceStats;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.FormatUtils;
import cn.nyc1.myapplication.utils.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        TextView textBack = findViewById(R.id.textBack);
        MaterialButton buttonExportCsv = findViewById(R.id.buttonExportCsv);
        RecyclerView recyclerStats = findViewById(R.id.recyclerStats);

        textTitle.setText("考勤统计.");
        textBack.setOnClickListener(v -> finish());
        buttonExportCsv.setOnClickListener(v -> exportCsv());
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
                        adapter.submitList(null);
                        textStatus.setText(message + "，无法加载真实考勤统计");
                    }
                });
    }

    private void exportCsv() {
        if (courseId <= 0) {
            textStatus.setText("课程参数异常，无法导出");
            return;
        }
        textStatus.setText("正在下载 CSV...");
        RetrofitClient.api().exportAttendanceStats(sessionManager.authHeader(), courseId, null)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            textStatus.setText("导出失败，请稍后重试");
                            return;
                        }
                        try {
                            String location = downloadCsv(response.body().string());
                            textStatus.setText("CSV 已下载：" + location);
                        } catch (IOException exception) {
                            textStatus.setText("CSV 下载失败");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        textStatus.setText("后端不可用或网络连接失败");
                    }
                });
    }

    private String downloadCsv(String csv) throws IOException {
        String fileName = "attendance-course-" + courseId + ".csv";
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/CampusCheckin");
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                throw new IOException("Cannot create download file");
            }
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream == null) {
                    throw new IOException("Cannot open download file");
                }
                outputStream.write(bytes);
            }
            return "Downloads/CampusCheckin/" + fileName;
        }

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create export directory");
        }
        File file = new File(dir, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        }
        return file.getAbsolutePath();
    }
}
