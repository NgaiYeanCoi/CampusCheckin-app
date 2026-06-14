package cn.nyc1.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.model.CreateCheckInTaskRequest;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class CreateCheckInActivity extends AppCompatActivity {
    private final List<Course> courses = new ArrayList<>();
    private final Calendar selectedStartTime = Calendar.getInstance();
    private final Calendar selectedEndTime = Calendar.getInstance();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private final SimpleDateFormat requestFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
    private SessionManager sessionManager;
    private Spinner spinnerCourses;
    private Spinner spinnerCheckInType;
    private TextInputEditText editTitle;
    private TextInputEditText editPassword;
    private TextView textPasswordLabel;
    private TextView editStartTime;
    private TextView editEndTime;
    private TextView textStatus;
    private MaterialButton buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_check_in);

        sessionManager = new SessionManager(this);
        spinnerCourses = findViewById(R.id.spinnerCourses);
        spinnerCheckInType = findViewById(R.id.spinnerCheckInType);
        editTitle = findViewById(R.id.editTitle);
        editPassword = findViewById(R.id.editPassword);
        textPasswordLabel = findViewById(R.id.textPasswordLabel);
        editStartTime = findViewById(R.id.editStartTime);
        editEndTime = findViewById(R.id.editEndTime);
        textStatus = findViewById(R.id.textStatus);
        buttonCreate = findViewById(R.id.buttonCreate);
        TextView textBack = findViewById(R.id.textBack);

        editTitle.setText("Android应用开发 临时签到");
        editPassword.setText("888888");
        setupCheckInTypes();
        initDefaultTimes();
        updateTimeViews();
        textBack.setOnClickListener(v -> finish());
        editStartTime.setOnClickListener(v -> showDateTimePicker(true));
        editEndTime.setOnClickListener(v -> showDateTimePicker(false));
        buttonCreate.setEnabled(false);
        buttonCreate.setOnClickListener(v -> createTask());
        loadCourses();
    }

    private void setupCheckInTypes() {
        List<String> types = new ArrayList<>();
        types.add("口令签到");
        types.add("二维码签到");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCheckInType.setAdapter(adapter);
        spinnerCheckInType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean qr = position == 1;
                editPassword.setEnabled(!qr);
                editPassword.setAlpha(qr ? 0.55f : 1.0f);
                textPasswordLabel.setText(qr ? "QR TOKEN" : "CODE");
                textStatus.setText(qr ? "二维码签到将由后端生成静态二维码 token。" : "口令签到需要填写课堂口令。");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadCourses() {
        textStatus.setText("正在加载教师课程...");
        RetrofitClient.api().getTeacherCourses(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        setCourses(data);
                        buttonCreate.setEnabled(!courses.isEmpty());
                        textStatus.setText(courses.isEmpty()
                                ? "暂无可发起签到的授课课程"
                                : "提交后写入 MySQL 的 check_in_tasks 表。");
                    }

                    @Override
                    public void onError(String message) {
                        setCourses(null);
                        buttonCreate.setEnabled(false);
                        textStatus.setText(message + "，无法加载真实课程，暂不能创建签到任务。");
                    }
                });
    }

    private void setCourses(List<Course> data) {
        courses.clear();
        if (data != null) {
            courses.addAll(data);
        }
        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);
    }

    private void createTask() {
        if (courses.isEmpty() || spinnerCourses.getSelectedItemPosition() < 0) {
            textStatus.setText("请先选择课程");
            return;
        }
        Course course = courses.get(spinnerCourses.getSelectedItemPosition());
        String title = String.valueOf(editTitle.getText()).trim();
        String password = String.valueOf(editPassword.getText()).trim();
        String checkInType = spinnerCheckInType.getSelectedItemPosition() == 1 ? "QR_CODE" : "PASSWORD";
        if (course.courseId == null || title.isEmpty() || ("PASSWORD".equals(checkInType) && password.isEmpty())) {
            textStatus.setText("请完整填写签到任务信息");
            return;
        }
        if (!selectedEndTime.after(selectedStartTime)) {
            textStatus.setText("截止时间必须晚于开始时间");
            return;
        }

        buttonCreate.setEnabled(false);
        textStatus.setText("正在创建签到任务...");
        String startTime = requestFormat.format(selectedStartTime.getTime());
        String endTime = requestFormat.format(selectedEndTime.getTime());
        CreateCheckInTaskRequest request = new CreateCheckInTaskRequest(
                course.courseId,
                title,
                checkInType,
                "PASSWORD".equals(checkInType) ? password : null,
                startTime,
                endTime
        );
        RetrofitClient.api().createCheckInTask(sessionManager.authHeader(), request)
                .enqueue(new SimpleCallback<CheckInTask>() {
                    @Override
                    public void onSuccess(CheckInTask data) {
                        Toast.makeText(CreateCheckInActivity.this, "签到任务已创建，已返回任务列表", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        buttonCreate.setEnabled(true);
                        textStatus.setText(message);
                    }
                });
    }

    private void initDefaultTimes() {
        selectedStartTime.set(Calendar.SECOND, 0);
        selectedStartTime.set(Calendar.MILLISECOND, 0);
        selectedEndTime.setTimeInMillis(selectedStartTime.getTimeInMillis());
        selectedEndTime.add(Calendar.MINUTE, 60);
    }

    private void updateTimeViews() {
        editStartTime.setText(displayFormat.format(selectedStartTime.getTime()));
        editEndTime.setText(displayFormat.format(selectedEndTime.getTime()));
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(isStartTime ? selectedStartTime.getTimeInMillis() : selectedEndTime.getTimeInMillis());

        DatePickerDialog dateDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    target.set(Calendar.YEAR, year);
                    target.set(Calendar.MONTH, month);
                    target.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker(isStartTime, target);
                },
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        );
        dateDialog.show();
    }

    private void showTimePicker(boolean isStartTime, Calendar target) {
        TimePickerDialog timeDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    target.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    target.set(Calendar.MINUTE, minute);
                    target.set(Calendar.SECOND, 0);
                    target.set(Calendar.MILLISECOND, 0);
                    applySelectedTime(isStartTime, target);
                },
                target.get(Calendar.HOUR_OF_DAY),
                target.get(Calendar.MINUTE),
                true
        );
        timeDialog.show();
    }

    private void applySelectedTime(boolean isStartTime, Calendar target) {
        if (isStartTime) {
            selectedStartTime.setTimeInMillis(target.getTimeInMillis());
            if (!selectedEndTime.after(selectedStartTime)) {
                selectedEndTime.setTimeInMillis(selectedStartTime.getTimeInMillis());
                selectedEndTime.add(Calendar.MINUTE, 60);
            }
        } else {
            if (!target.after(selectedStartTime)) {
                textStatus.setText("截止时间必须晚于开始时间");
                return;
            }
            selectedEndTime.setTimeInMillis(target.getTimeInMillis());
        }
        updateTimeViews();
    }
}
