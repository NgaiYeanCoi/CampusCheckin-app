package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        TextView textDisplayName = findViewById(R.id.textDisplayName);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textRole = findViewById(R.id.textRole);
        MaterialButton buttonLogout = findViewById(R.id.buttonLogout);

        textDisplayName.setText(sessionManager.getDisplayName());
        textUsername.setText("账号 " + sessionManager.getUsername());
        textRole.setText("角色 " + sessionManager.getRole());
        buttonLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        RetrofitClient.api().logout(sessionManager.authHeader())
                .enqueue(new SimpleCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        goLogin();
                    }

                    @Override
                    public void onError(String message) {
                        goLogin();
                    }
                });
    }

    private void goLogin() {
        sessionManager.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
