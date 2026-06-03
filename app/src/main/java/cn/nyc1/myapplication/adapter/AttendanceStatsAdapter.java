package cn.nyc1.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.AttendanceStats;
import cn.nyc1.myapplication.utils.FormatUtils;

public class AttendanceStatsAdapter extends RecyclerView.Adapter<AttendanceStatsAdapter.StatsViewHolder> {
    private final List<AttendanceStats> stats = new ArrayList<>();

    public void submitList(List<AttendanceStats> newStats) {
        stats.clear();
        if (newStats != null) {
            stats.addAll(newStats);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_stat, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        AttendanceStats item = stats.get(position);
        holder.taskTitle.setText(FormatUtils.safe(item.taskTitle));
        holder.total.setText(number(item.totalCount) + "\n应到");
        holder.signed.setText(number(item.signedCount) + "\n已签");
        holder.late.setText(number(item.lateCount) + "\n迟到");
        holder.absent.setText(number(item.absentCount) + "\n缺勤");
        holder.rate.setText(String.format(Locale.CHINA, "出勤率 %.2f%%", item.attendanceRate == null ? 0.0 : item.attendanceRate));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    private int number(Integer value) {
        return value == null ? 0 : value;
    }

    static class StatsViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView total;
        TextView signed;
        TextView late;
        TextView absent;
        TextView rate;

        StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.textTaskTitle);
            total = itemView.findViewById(R.id.textTotal);
            signed = itemView.findViewById(R.id.textSigned);
            late = itemView.findViewById(R.id.textLate);
            absent = itemView.findViewById(R.id.textAbsent);
            rate = itemView.findViewById(R.id.textRate);
        }
    }
}
