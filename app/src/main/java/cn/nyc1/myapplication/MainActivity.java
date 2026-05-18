package cn.nyc1.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.nyc1.myapplication.activity.LoginActivity;
import cn.nyc1.myapplication.activity.StudentHomeActivity;
import cn.nyc1.myapplication.activity.TeacherHomeActivity;
import cn.nyc1.myapplication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SessionManager sessionManager = new SessionManager(this);
        Class<?> target = LoginActivity.class;
        if (sessionManager.isLoggedIn()) {
            target = "TEACHER".equals(sessionManager.getRole()) ? TeacherHomeActivity.class : StudentHomeActivity.class;
        }
        startActivity(new Intent(this, target));
        finish();
    }
}
