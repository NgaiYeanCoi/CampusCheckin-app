package cn.nyc1.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.utils.FormatUtils;

public class CheckInTaskAdapter extends RecyclerView.Adapter<CheckInTaskAdapter.TaskViewHolder> {
    public interface OnEndTaskClickListener {
        void onEndTask(CheckInTask task);
    }

    public interface OnTaskClickListener {
        void onTaskClick(CheckInTask task);
    }

    private final List<CheckInTask> tasks = new ArrayList<>();
    private final OnEndTaskClickListener endTaskClickListener;
    private final OnTaskClickListener taskClickListener;

    public CheckInTaskAdapter(OnEndTaskClickListener endTaskClickListener, OnTaskClickListener taskClickListener) {
        this.endTaskClickListener = endTaskClickListener;
        this.taskClickListener = taskClickListener;
    }

    public void submitList(List<CheckInTask> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        CheckInTask task = tasks.get(position);
        holder.title.setText(FormatUtils.safe(task.title));
        holder.status.setText(FormatUtils.statusText(task.status));
        holder.course.setText(FormatUtils.safe(task.courseName) + " · " + typeText(task.checkInType));
        holder.time.setText(FormatUtils.safe(task.startTime) + " - " + FormatUtils.safe(task.endTime));
        holder.summary.setText("已签 " + number(task.submittedCount) + " / 应到 " + number(task.totalCount));
        holder.itemView.setOnClickListener(v -> {
            if (taskClickListener != null) {
                taskClickListener.onTaskClick(task);
            }
        });

        boolean canEnd = "ACTIVE".equals(task.status);
        holder.endButton.setVisibility(canEnd ? View.VISIBLE : View.GONE);
        holder.endButton.setOnClickListener(v -> {
            if (endTaskClickListener != null) {
                endTaskClickListener.onEndTask(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private int number(Integer value) {
        return value == null ? 0 : value;
    }

    private String typeText(String checkInType) {
        return "QR_CODE".equals(checkInType) ? "二维码" : "口令";
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView status;
        TextView course;
        TextView time;
        TextView summary;
        MaterialButton endButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTaskTitle);
            status = itemView.findViewById(R.id.textTaskStatus);
            course = itemView.findViewById(R.id.textTaskCourse);
            time = itemView.findViewById(R.id.textTaskTime);
            summary = itemView.findViewById(R.id.textTaskSummary);
            endButton = itemView.findViewById(R.id.buttonEndTask);
        }
    }
}
