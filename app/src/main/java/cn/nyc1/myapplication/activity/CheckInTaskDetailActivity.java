package cn.nyc1.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;
import java.util.Locale;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CheckInStudentRecordAdapter;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.CheckInTaskDetail;
import cn.nyc1.myapplication.model.CheckInTaskSummary;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.FormatUtils;
import cn.nyc1.myapplication.utils.SessionManager;

public class CheckInTaskDetailActivity extends AppCompatActivity {
    private static final long REFRESH_INTERVAL_MS = 10_000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadDetail(false);
            handler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    private SessionManager sessionManager;
    private CheckInStudentRecordAdapter adapter;
    private long taskId;
    private TextView textTitle;
    private TextView textMeta;
    private TextView textStatus;
    private TextView textTotal;
    private TextView textSigned;
    private TextView textUnsigned;
    private TextView textLate;
    private TextView textAbsent;
    private TextView textRate;
    private TextView textStudentsTitle;
    private LinearLayout layoutQrCode;
    private ImageView imageQrCode;
    private TextView textQrPayload;
    private MaterialButton buttonRefresh;
    private MaterialButton buttonEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_task_detail);

        sessionManager = new SessionManager(this);
        taskId = getIntent().getLongExtra("taskId", -1);

        TextView buttonBack = findViewById(R.id.buttonBack);
        textTitle = findViewById(R.id.textTitle);
        textMeta = findViewById(R.id.textMeta);
        textStatus = findViewById(R.id.textStatus);
        textTotal = findViewById(R.id.textTotal);
        textSigned = findViewById(R.id.textSigned);
        textUnsigned = findViewById(R.id.textUnsigned);
        textLate = findViewById(R.id.textLate);
        textAbsent = findViewById(R.id.textAbsent);
        textRate = findViewById(R.id.textRate);
        textStudentsTitle = findViewById(R.id.textStudentsTitle);
        layoutQrCode = findViewById(R.id.layoutQrCode);
        imageQrCode = findViewById(R.id.imageQrCode);
        textQrPayload = findViewById(R.id.textQrPayload);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonEnd = findViewById(R.id.buttonEnd);
        RecyclerView recyclerStudents = findViewById(R.id.recyclerStudents);

        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInStudentRecordAdapter();
        recyclerStudents.setAdapter(adapter);

        buttonBack.setOnClickListener(v -> finish());
        buttonRefresh.setOnClickListener(v -> loadDetail(true));
        buttonEnd.setOnClickListener(v -> confirmEndTask());

        showEmptyState();
        loadDetail(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.removeCallbacks(autoRefreshRunnable);
        handler.postDelayed(autoRefreshRunnable, REFRESH_INTERVAL_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(autoRefreshRunnable);
    }

    private void loadDetail(boolean showLoading) {
        if (taskId <= 0) {
            showErrorState("签到任务参数异常");
            return;
        }
        if (showLoading) {
            textStatus.setText("正在刷新签到明细...");
            textStatus.setTextColor(getColor(R.color.notion_purple));
            buttonRefresh.setEnabled(false);
        }
        RetrofitClient.api().getTeacherCheckInTaskDetail(sessionManager.authHeader(), taskId)
                .enqueue(new SimpleCallback<CheckInTaskDetail>() {
                    @Override
                    public void onSuccess(CheckInTaskDetail data) {
                        buttonRefresh.setEnabled(true);
                        bindDetail(data);
                    }

                    @Override
                    public void onError(String message) {
                        buttonRefresh.setEnabled(true);
                        showErrorState(message);
                    }
                });
    }

    private void bindDetail(CheckInTaskDetail detail) {
        if (detail == null) {
            textStatus.setText("未找到签到任务");
            buttonEnd.setEnabled(false);
            return;
        }
        textTitle.setText(FormatUtils.safe(detail.title));
        textMeta.setText(FormatUtils.safe(detail.courseName) + "\n"
                + FormatUtils.safe(detail.startTime) + " - " + FormatUtils.safe(detail.endTime));
        textStatus.setText(FormatUtils.statusText(detail.status));
        textStatus.setTextColor(getColor("ACTIVE".equals(detail.status) ? R.color.notion_purple : R.color.notion_mute));
        bindQrCode(detail);
        bindSummary(detail.summary);

        List<cn.nyc1.myapplication.model.CheckInStudentRecord> students = detail.students;
        adapter.submitList(students);
        int count = students == null ? 0 : students.size();
        textStudentsTitle.setText("学生签到明细 · " + count + " 人");

        boolean active = "ACTIVE".equals(detail.status);
        buttonEnd.setEnabled(active);
        buttonEnd.setAlpha(active ? 1.0f : 0.55f);
        buttonEnd.setText(active ? "手动截止" : "已截止");
    }

    private void showEmptyState() {
        textTitle.setText("签到任务详情");
        textMeta.setText("等待后端返回任务信息");
        textStatus.setText("正在加载...");
        textStatus.setTextColor(getColor(R.color.notion_purple));
        bindSummary(null);
        textStudentsTitle.setText("学生签到明细");
        layoutQrCode.setVisibility(View.GONE);
        buttonEnd.setEnabled(false);
        buttonEnd.setAlpha(0.55f);
    }

    private void showErrorState(String message) {
        textTitle.setText("签到任务详情");
        textMeta.setText("请检查后端服务是否为最新代码，并查看服务日志");
        textStatus.setText("加载失败：" + FormatUtils.safe(message));
        textStatus.setTextColor(getColor(R.color.notion_error));
        bindSummary(null);
        adapter.submitList(null);
        layoutQrCode.setVisibility(View.GONE);
        textStudentsTitle.setText("学生签到明细 · 加载失败");
        buttonEnd.setEnabled(false);
        buttonEnd.setAlpha(0.55f);
    }

    private void bindSummary(CheckInTaskSummary summary) {
        if (summary == null) {
            textTotal.setText("0\n应到");
            textSigned.setText("0\n已签");
            textUnsigned.setText("0\n未签");
            textLate.setText("0\n迟到");
            textAbsent.setText("0\n缺勤");
            textRate.setText("0.00%\n出勤率");
            return;
        }
        textTotal.setText(number(summary.totalCount) + "\n应到");
        textSigned.setText(number(summary.signedCount) + "\n已签");
        textUnsigned.setText(number(summary.unsignedCount) + "\n未签");
        textLate.setText(number(summary.lateCount) + "\n迟到");
        textAbsent.setText(number(summary.absentCount) + "\n缺勤");
        double rate = summary.attendanceRate == null ? 0.0 : summary.attendanceRate;
        textRate.setText(String.format(Locale.CHINA, "%.2f%%\n出勤率", rate));
    }

    private int number(Integer value) {
        return value == null ? 0 : value;
    }

    private void bindQrCode(CheckInTaskDetail detail) {
        if (detail == null || !"QR_CODE".equals(detail.checkInType) || detail.qrPayload == null || detail.qrPayload.trim().isEmpty()) {
            layoutQrCode.setVisibility(View.GONE);
            return;
        }
        try {
            layoutQrCode.setVisibility(View.VISIBLE);
            imageQrCode.setImageBitmap(createQrBitmap(detail.qrPayload, 720));
            textQrPayload.setText("学生扫码后自动提交签到");
        } catch (Exception exception) {
            layoutQrCode.setVisibility(View.GONE);
            textStatus.setText("二维码生成失败");
        }
    }

    private Bitmap createQrBitmap(String payload, int size) throws Exception {
        BitMatrix matrix = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, size, size);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    private void confirmEndTask() {
        new AlertDialog.Builder(this)
                .setTitle("手动截止签到")
                .setMessage("截止后学生将不能继续签到，是否确认？")
                .setNegativeButton("取消", null)
                .setPositiveButton("截止", (dialog, which) -> endTask())
                .show();
    }

    private void endTask() {
        buttonEnd.setEnabled(false);
        textStatus.setText("正在截止签到任务...");
        RetrofitClient.api().endCheckInTask(sessionManager.authHeader(), taskId)
                .enqueue(new SimpleCallback<CheckInTask>() {
                    @Override
                    public void onSuccess(CheckInTask data) {
                        loadDetail(true);
                    }

                    @Override
                    public void onError(String message) {
                        buttonEnd.setEnabled(true);
                        textStatus.setText(message);
                    }
                });
    }
}
