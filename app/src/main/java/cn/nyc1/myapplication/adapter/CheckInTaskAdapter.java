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

    private final List<CheckInTask> tasks = new ArrayList<>();
    private final OnEndTaskClickListener endTaskClickListener;

    public CheckInTaskAdapter(OnEndTaskClickListener endTaskClickListener) {
        this.endTaskClickListener = endTaskClickListener;
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
        holder.course.setText(FormatUtils.safe(task.courseName));
        holder.time.setText(FormatUtils.safe(task.startTime) + " - " + FormatUtils.safe(task.endTime));

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

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView status;
        TextView course;
        TextView time;
        MaterialButton endButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTaskTitle);
            status = itemView.findViewById(R.id.textTaskStatus);
            course = itemView.findViewById(R.id.textTaskCourse);
            time = itemView.findViewById(R.id.textTaskTime);
            endButton = itemView.findViewById(R.id.buttonEndTask);
        }
    }
}
