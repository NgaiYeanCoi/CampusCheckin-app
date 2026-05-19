package cn.nyc1.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.utils.FormatUtils;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    private final List<Course> courses = new ArrayList<>();
    private final OnCourseClickListener listener;

    public CourseAdapter(OnCourseClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Course> newCourses) {
        courses.clear();
        if (newCourses != null) {
            courses.addAll(newCourses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseName.setText(FormatUtils.safe(course.courseName));
        holder.courseCode.setText(FormatUtils.safe(course.courseCode));
        holder.teacher.setText(FormatUtils.safe(course.teacherName));
        holder.location.setText(FormatUtils.safe(course.location));
        holder.time.setText(FormatUtils.courseTime(course.weekDay, course.section, course.startTime, course.endTime));
        holder.status.setText(FormatUtils.safe(course.status));
        int color = "ACTIVE".equals(course.status)
                ? ContextCompat.getColor(holder.itemView.getContext(), R.color.vc_blue)
                : ContextCompat.getColor(holder.itemView.getContext(), R.color.vc_body);
        holder.status.setTextColor(color);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        TextView courseCode;
        TextView teacher;
        TextView location;
        TextView time;
        TextView status;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.textCourseName);
            courseCode = itemView.findViewById(R.id.textCourseCode);
            teacher = itemView.findViewById(R.id.textTeacher);
            location = itemView.findViewById(R.id.textLocation);
            time = itemView.findViewById(R.id.textTime);
            status = itemView.findViewById(R.id.textStatus);
        }
    }
}
