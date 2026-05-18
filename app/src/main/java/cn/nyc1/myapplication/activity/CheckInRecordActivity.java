package cn.nyc1.myapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CheckInRecordAdapter;
import cn.nyc1.myapplication.data.PreviewData;
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class CheckInRecordActivity extends AppCompatActivity {
    private TextView textStatus;
    private CheckInRecordAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_record);

        sessionManager = new SessionManager(this);
        textStatus = findViewById(R.id.textStatus);
        RecyclerView recyclerRecords = findViewById(R.id.recyclerRecords);
        recyclerRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInRecordAdapter();
        recyclerRecords.setAdapter(adapter);
        loadRecords();
    }

    private void loadRecords() {
        textStatus.setText("正在加载签到记录...");
        RetrofitClient.api().getStudentCheckInRecords(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<CheckInRecord>>() {
                    @Override
                    public void onSuccess(List<CheckInRecord> data) {
                        adapter.submitList(data);
                        int count = data == null ? 0 : data.size();
                        textStatus.setText("共 " + count + " 条签到记录");
                    }

                    @Override
                    public void onError(String message) {
                        adapter.submitList(PreviewData.records());
                        textStatus.setText(message + "，当前仅展示离线预览数据");
                    }
                });
    }
}
