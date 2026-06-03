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
import cn.nyc1.myapplication.model.ActiveCheckInTask;
import cn.nyc1.myapplication.utils.FormatUtils;

public class ActiveCheckInTaskAdapter extends RecyclerView.Adapter<ActiveCheckInTaskAdapter.ActiveTaskViewHolder> {
    public interface OnTaskClickListener {
        void onTaskClick(ActiveCheckInTask task);
    }

    private final List<ActiveCheckInTask> tasks = new ArrayList<>();
    private final OnTaskClickListener taskClickListener;

    public ActiveCheckInTaskAdapter(OnTaskClickListener taskClickListener) {
        this.taskClickListener = taskClickListener;
    }

    public void submitList(List<ActiveCheckInTask> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActiveTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_check_in_task, parent, false);
        return new ActiveTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveTaskViewHolder holder, int position) {
        ActiveCheckInTask task = tasks.get(position);
        holder.course.setText(FormatUtils.safe(task.courseName));
        holder.title.setText(FormatUtils.safe(task.title) + " · " + FormatUtils.safe(task.teacherName));
        holder.time.setText(FormatUtils.safe(task.startTime) + " - " + FormatUtils.safe(task.endTime));

        String displayStatus = task.signed ? task.recordStatus : task.taskStatus;
        holder.status.setText(FormatUtils.statusText(displayStatus));
        holder.button.setText(buttonText(task));
        holder.button.setEnabled(task.canSubmit());
        holder.button.setAlpha(task.canSubmit() ? 1.0f : 0.55f);
        holder.button.setOnClickListener(v -> {
            if (taskClickListener != null) {
                taskClickListener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String buttonText(ActiveCheckInTask task) {
        if (task.signed) {
            return "已完成签到";
        }
        if ("NOT_STARTED".equals(task.taskStatus)) {
            return "未开始";
        }
        if ("ACTIVE".equals(task.taskStatus)) {
            return "输入口令签到";
        }
        return "不可签到";
    }

    static class ActiveTaskViewHolder extends RecyclerView.ViewHolder {
        TextView course;
        TextView status;
        TextView title;
        TextView time;
        MaterialButton button;

        ActiveTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.textTaskCourse);
            status = itemView.findViewById(R.id.textTaskStatus);
            title = itemView.findViewById(R.id.textTaskTitle);
            time = itemView.findViewById(R.id.textTaskTime);
            button = itemView.findViewById(R.id.buttonCheckIn);
        }
    }
}
