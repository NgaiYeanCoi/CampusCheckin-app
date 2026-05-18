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
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.utils.FormatUtils;

public class CheckInRecordAdapter extends RecyclerView.Adapter<CheckInRecordAdapter.RecordViewHolder> {
    private final List<CheckInRecord> records = new ArrayList<>();

    public void submitList(List<CheckInRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        CheckInRecord record = records.get(position);
        holder.courseName.setText(FormatUtils.safe(record.courseName));
        holder.taskTitle.setText(FormatUtils.safe(record.taskTitle));
        holder.time.setText(FormatUtils.safe(record.checkInTime));
        holder.status.setText(FormatUtils.statusText(record.status));
        holder.remark.setText(FormatUtils.safe(record.remark));
        if ("SIGNED".equals(record.status)) {
            holder.status.setBackgroundResource(R.drawable.bg_status_success);
        } else if ("LATE".equals(record.status)) {
            holder.status.setBackgroundResource(R.drawable.bg_status_warning);
        } else if ("ABSENT".equals(record.status) || "EXCEPTION".equals(record.status)) {
            holder.status.setBackgroundResource(R.drawable.bg_status_error);
        } else {
            holder.status.setBackgroundResource(R.drawable.bg_status_neutral);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        TextView taskTitle;
        TextView time;
        TextView status;
        TextView remark;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.textCourseName);
            taskTitle = itemView.findViewById(R.id.textTaskTitle);
            time = itemView.findViewById(R.id.textCheckInTime);
            status = itemView.findViewById(R.id.textStatus);
            remark = itemView.findViewById(R.id.textRemark);
        }
    }
}
