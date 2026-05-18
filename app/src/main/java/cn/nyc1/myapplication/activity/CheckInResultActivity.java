package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.utils.FormatUtils;

public class CheckInResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_result);

        String status = getIntent().getStringExtra("status");
        String message = getIntent().getStringExtra("message");
        String courseName = getIntent().getStringExtra("courseName");
        String checkInTime = getIntent().getStringExtra("checkInTime");

        TextView textStatus = findViewById(R.id.textStatus);
        TextView textMessage = findViewById(R.id.textMessage);
        TextView textMeta = findViewById(R.id.textMeta);
        MaterialButton buttonBackHome = findViewById(R.id.buttonBackHome);

        textStatus.setText(FormatUtils.statusText(status));
        textMessage.setText(FormatUtils.safe(message));
        textMeta.setText(FormatUtils.safe(courseName) + "\n" + FormatUtils.safe(checkInTime));
        buttonBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
