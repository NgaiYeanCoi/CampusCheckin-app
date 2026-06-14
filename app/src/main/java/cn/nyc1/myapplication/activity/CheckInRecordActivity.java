package cn.nyc1.myapplication.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.adapter.CheckInRecordAdapter;
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.network.RetrofitClient;
import cn.nyc1.myapplication.network.SimpleCallback;
import cn.nyc1.myapplication.utils.SessionManager;

public class CheckInRecordActivity extends AppCompatActivity {
    private TextView textStatus;
    private Spinner spinnerCourseFilter;
    private Spinner spinnerStatusFilter;
    private TextView textStartDate;
    private TextView textEndDate;
    private CheckInRecordAdapter adapter;
    private SessionManager sessionManager;
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private boolean hasStartDate;
    private boolean hasEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_record);

        sessionManager = new SessionManager(this);
        textStatus = findViewById(R.id.textStatus);
        spinnerCourseFilter = findViewById(R.id.spinnerCourseFilter);
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter);
        textStartDate = findViewById(R.id.textStartDate);
        textEndDate = findViewById(R.id.textEndDate);
        MaterialButton buttonApplyFilters = findViewById(R.id.buttonApplyFilters);
        MaterialButton buttonResetFilters = findViewById(R.id.buttonResetFilters);
        RecyclerView recyclerRecords = findViewById(R.id.recyclerRecords);
        recyclerRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInRecordAdapter();
        recyclerRecords.setAdapter(adapter);

        setupStatusFilter();
        loadCourseFilter();
        textStartDate.setOnClickListener(v -> pickDate(true));
        textEndDate.setOnClickListener(v -> pickDate(false));
        buttonApplyFilters.setOnClickListener(v -> loadRecords());
        buttonResetFilters.setOnClickListener(v -> resetFilters());
        loadRecords();
    }

    private void setupStatusFilter() {
        List<StatusFilterItem> items = new ArrayList<>();
        items.add(new StatusFilterItem("全部状态", null));
        items.add(new StatusFilterItem("已签到", "SIGNED"));
        items.add(new StatusFilterItem("迟到", "LATE"));
        items.add(new StatusFilterItem("缺勤", "ABSENT"));
        items.add(new StatusFilterItem("异常", "EXCEPTION"));
        ArrayAdapter<StatusFilterItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(adapter);
    }

    private void loadCourseFilter() {
        RetrofitClient.api().getStudentCourses(sessionManager.authHeader())
                .enqueue(new SimpleCallback<List<Course>>() {
                    @Override
                    public void onSuccess(List<Course> data) {
                        List<CourseFilterItem> items = new ArrayList<>();
                        items.add(new CourseFilterItem("全部课程", null));
                        if (data != null) {
                            for (Course course : data) {
                                items.add(new CourseFilterItem(course.courseName, course.courseId));
                            }
                        }
                        ArrayAdapter<CourseFilterItem> adapter = new ArrayAdapter<>(
                                CheckInRecordActivity.this,
                                android.R.layout.simple_spinner_item,
                                items
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCourseFilter.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String message) {
                        List<CourseFilterItem> items = new ArrayList<>();
                        items.add(new CourseFilterItem("全部课程", null));
                        ArrayAdapter<CourseFilterItem> adapter = new ArrayAdapter<>(
                                CheckInRecordActivity.this,
                                android.R.layout.simple_spinner_item,
                                items
                        );
                        spinnerCourseFilter.setAdapter(adapter);
                    }
                });
    }

    private void loadRecords() {
        textStatus.setText("正在加载签到记录...");
        RetrofitClient.api().getStudentCheckInRecords(
                        sessionManager.authHeader(),
                        selectedCourseId(),
                        selectedStatus(),
                        hasStartDate ? dateFormat.format(startDate.getTime()) : null,
                        hasEndDate ? dateFormat.format(endDate.getTime()) : null
                )
                .enqueue(new SimpleCallback<List<CheckInRecord>>() {
                    @Override
                    public void onSuccess(List<CheckInRecord> data) {
                        adapter.submitList(data);
                        int count = data == null ? 0 : data.size();
                        textStatus.setText("共 " + count + " 条签到记录");
                    }

                    @Override
                    public void onError(String message) {
                        adapter.submitList(null);
                        textStatus.setText(message + "，无法加载真实签到记录");
                    }
                });
    }

    private Long selectedCourseId() {
        Object selected = spinnerCourseFilter.getSelectedItem();
        return selected instanceof CourseFilterItem ? ((CourseFilterItem) selected).courseId : null;
    }

    private String selectedStatus() {
        Object selected = spinnerStatusFilter.getSelectedItem();
        return selected instanceof StatusFilterItem ? ((StatusFilterItem) selected).status : null;
    }

    private void pickDate(boolean start) {
        Calendar target = start ? startDate : endDate;
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    target.set(Calendar.YEAR, year);
                    target.set(Calendar.MONTH, month);
                    target.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if (start) {
                        hasStartDate = true;
                        textStartDate.setText(dateFormat.format(target.getTime()));
                    } else {
                        hasEndDate = true;
                        textEndDate.setText(dateFormat.format(target.getTime()));
                    }
                },
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void resetFilters() {
        spinnerCourseFilter.setSelection(0);
        spinnerStatusFilter.setSelection(0);
        hasStartDate = false;
        hasEndDate = false;
        textStartDate.setText("开始日期");
        textEndDate.setText("结束日期");
        loadRecords();
    }

    private static class CourseFilterItem {
        final String label;
        final Long courseId;

        CourseFilterItem(String label, Long courseId) {
            this.label = label == null ? "未命名课程" : label;
            this.courseId = courseId;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class StatusFilterItem {
        final String label;
        final String status;

        StatusFilterItem(String label, String status) {
            this.label = label;
            this.status = status;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
