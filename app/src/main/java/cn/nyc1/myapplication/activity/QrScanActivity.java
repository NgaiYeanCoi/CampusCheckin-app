package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInResult;
import cn.nyc1.myapplication.model.SubmitCheckInRequest;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class QrScanActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private TextView textStatus;
    private MaterialButton buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        sessionManager = new SessionManager(this);
        textStatus = findViewById(R.id.textStatus);
        buttonScan = findViewById(R.id.buttonScan);
        TextView buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());
        buttonScan.setOnClickListener(v -> startScan());
        startScan();
    }

    private void startScan() {
        textStatus.setText("请扫描教师端展示的签到二维码");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("扫描智课签二维码");
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                textStatus.setText("已取消扫码");
                return;
            }
            handleQrPayload(result.getContents());
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleQrPayload(String payload) {
        try {
            Uri uri = Uri.parse(payload);
            if (!"campuscheckin".equals(uri.getScheme()) || !"check-in".equals(uri.getHost())) {
                textStatus.setText("不是有效的智课签二维码");
                return;
            }
            String taskIdText = uri.getQueryParameter("taskId");
            String token = uri.getQueryParameter("token");
            if (taskIdText == null || token == null || token.trim().isEmpty()) {
                textStatus.setText("二维码缺少签到参数");
                return;
            }
            submit(Long.parseLong(taskIdText), token);
        } catch (RuntimeException exception) {
            textStatus.setText("二维码内容无法识别");
        }
    }

    private void submit(long taskId, String token) {
        buttonScan.setEnabled(false);
        textStatus.setText("正在提交二维码签到...");
        RetrofitClient.api().submitCheckIn(sessionManager.authHeader(), new SubmitCheckInRequest(taskId, null, token))
                .enqueue(new SimpleCallback<CheckInResult>() {
                    @Override
                    public void onSuccess(CheckInResult data) {
                        Intent intent = new Intent(QrScanActivity.this, CheckInResultActivity.class);
                        intent.putExtra("status", data == null ? "SIGNED" : data.status);
                        intent.putExtra("message", data == null ? "签到成功" : data.message);
                        intent.putExtra("courseName", data == null ? "" : data.courseName);
                        intent.putExtra("checkInTime", data == null ? "" : data.checkInTime);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        buttonScan.setEnabled(true);
                        textStatus.setText(message);
                    }
                });
    }
}
