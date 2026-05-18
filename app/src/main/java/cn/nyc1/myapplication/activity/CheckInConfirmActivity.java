package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInResult;
import cn.nyc1.myapplication.model.SubmitCheckInRequest;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.FormatUtils;
import cn.nyc1.myapplication.utils.SessionManager;

public class CheckInConfirmActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private long taskId;
    private String courseName;
    private TextInputEditText editPassword;
    private TextView textStatus;
    private MaterialButton buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_confirm);

        sessionManager = new SessionManager(this);
        taskId = getIntent().getLongExtra("taskId", -1);
        courseName = getIntent().getStringExtra("courseName");

        TextView textCourseName = findViewById(R.id.textCourseName);
        TextView textTaskInfo = findViewById(R.id.textTaskInfo);
        editPassword = findViewById(R.id.editPassword);
        textStatus = findViewById(R.id.textStatus);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        textCourseName.setText(FormatUtils.safe(courseName));
        textTaskInfo.setText("签到任务 ID：" + taskId);
        buttonSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        String password = String.valueOf(editPassword.getText()).trim();
        if (password.isEmpty()) {
            textStatus.setText("请输入课堂口令");
            return;
        }
        buttonSubmit.setEnabled(false);
        textStatus.setText("正在提交签到...");
        RetrofitClient.api().submitCheckIn(sessionManager.authHeader(), new SubmitCheckInRequest(taskId, password))
                .enqueue(new SimpleCallback<CheckInResult>() {
                    @Override
                    public void onSuccess(CheckInResult data) {
                        Intent intent = new Intent(CheckInConfirmActivity.this, CheckInResultActivity.class);
                        intent.putExtra("status", data == null ? "SIGNED" : data.status);
                        intent.putExtra("message", data == null ? "签到成功" : data.message);
                        intent.putExtra("courseName", data == null ? courseName : data.courseName);
                        intent.putExtra("checkInTime", data == null ? "" : data.checkInTime);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        buttonSubmit.setEnabled(true);
                        textStatus.setText(message);
                    }
                });
    }
}
