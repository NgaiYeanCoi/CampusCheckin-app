package cn.nyc1.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.nyc1.myapplication.R;
import cn.nyc1.myapplication.model.CheckInStudentRecord;
import cn.nyc1.myapplication.utils.FormatUtils;

public class CheckInStudentRecordAdapter extends RecyclerView.Adapter<CheckInStudentRecordAdapter.StudentViewHolder> {
    private final List<CheckInStudentRecord> records = new ArrayList<>();

    public void submitList(List<CheckInStudentRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        CheckInStudentRecord item = records.get(position);
        holder.name.setText(FormatUtils.safe(item.studentName));
        holder.status.setText(FormatUtils.statusText(item.status));
        holder.meta.setText(FormatUtils.safe(item.studentNo) + " · " + FormatUtils.safe(item.className));
        holder.time.setText(timeText(item));
        if ("SIGNED".equals(item.status)) {
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.vc_blue));
        } else if ("LATE".equals(item.status)) {
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.vc_warning));
        } else if ("ABSENT".equals(item.status) || "EXCEPTION".equals(item.status)) {
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.vc_error));
        } else {
            holder.status.setTextColor(holder.itemView.getContext().getColor(R.color.vc_mute));
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private String timeText(CheckInStudentRecord item) {
        if (item.checkInTime == null || item.checkInTime.trim().isEmpty()) {
            return FormatUtils.safe(item.remark);
        }
        return FormatUtils.safe(item.checkInTime) + " · " + FormatUtils.safe(item.remark);
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        TextView meta;
        TextView time;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textStudentName);
            status = itemView.findViewById(R.id.textStudentStatus);
            meta = itemView.findViewById(R.id.textStudentMeta);
            time = itemView.findViewById(R.id.textStudentTime);
        }
    }
}
