package cn.nyc1.myapplication.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.LoginRequest;
import cn.nyc1.myapplication.model.LoginResponse;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private MaterialButton buttonStudent;
    private MaterialButton buttonTeacher;
    private MaterialButton buttonLogin;
    private TextView textStatus;
    private String selectedRole = "STUDENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        buttonStudent = findViewById(R.id.buttonStudent);
        buttonTeacher = findViewById(R.id.buttonTeacher);
        buttonLogin = findViewById(R.id.buttonLogin);
        textStatus = findViewById(R.id.textLoginStatus);

        editUsername.setText("s20260001");
        editPassword.setText("123456");

        buttonStudent.setOnClickListener(v -> selectRole("STUDENT"));
        buttonTeacher.setOnClickListener(v -> selectRole("TEACHER"));
        buttonLogin.setOnClickListener(v -> login());
        selectRole("STUDENT");
    }

    private void selectRole(String role) {
        selectedRole = role;
        boolean isStudent = "STUDENT".equals(role);
        int primary = ContextCompat.getColor(this, R.color.cc_primary);
        int surface = ContextCompat.getColor(this, R.color.notion_card);
        int text = ContextCompat.getColor(this, R.color.notion_ink);
        int white = ContextCompat.getColor(this, R.color.white);

        buttonStudent.setBackgroundTintList(ColorStateList.valueOf(isStudent ? primary : surface));
        buttonStudent.setTextColor(isStudent ? white : text);
        buttonTeacher.setBackgroundTintList(ColorStateList.valueOf(isStudent ? surface : primary));
        buttonTeacher.setTextColor(isStudent ? text : white);
        editUsername.setText(isStudent ? "s20260001" : "t20260001");
    }

    private void login() {
        String username = String.valueOf(editUsername.getText()).trim();
        String password = String.valueOf(editPassword.getText()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            textStatus.setText("请输入账号和密码");
            return;
        }
        buttonLogin.setEnabled(false);
        textStatus.setText("正在连接后端...");

        RetrofitClient.api().login(new LoginRequest(username, password, selectedRole))
                .enqueue(new SimpleCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse data) {
                        new SessionManager(LoginActivity.this).saveLogin(data);
                        Class<?> target = "TEACHER".equals(data.role) ? TeacherHomeActivity.class : StudentHomeActivity.class;
                        startActivity(new Intent(LoginActivity.this, target));
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        buttonLogin.setEnabled(true);
                        textStatus.setText(message);
                    }
                });
    }
}
