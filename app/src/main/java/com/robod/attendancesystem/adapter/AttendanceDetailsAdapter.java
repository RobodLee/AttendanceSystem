package com.robod.attendancesystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.entity.Record;

import java.util.List;

/**
 * @author Robod
 * @date 2020/10/8 11:04
 * 详情页面ListView的适配器
 */
public class AttendanceDetailsAdapter extends ArrayAdapter<Record> {

    private int resourceId;     //R.layout.attendance_details_item.xml
    private List<Record> records;

    public AttendanceDetailsAdapter(@NonNull Context context, int resourceId, @NonNull List<Record> records) {
        super(context, resourceId, records);
        this.resourceId = resourceId;
        this.records = records;
    }

    class ViewHolder {
        TextView studentName;
        TextView signInTime;
        TextView signOutTime;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = records.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.studentName = view.findViewById(R.id.details_student_name);
            viewHolder.signInTime = view.findViewById(R.id.details_sign_in_time);
            viewHolder.signOutTime = view.findViewById(R.id.details_sign_out_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.studentName.setText(record.getStudent_name());
        if ("1".equals(record.getStatus())) {   //正常签到签退，绿色
            viewHolder.studentName.setTextColor(Color.GREEN);
        } else if ("2".equals(record.getStatus())) {    //请假，橙色
            viewHolder.studentName.setTextColor(Color.parseColor("#FF9900"));
        }
        viewHolder.signInTime.setText(record.getSign_in_time() != null ? record.getSign_in_time() : "-");
        viewHolder.signOutTime.setText(record.getSign_out_time() != null ? record.getSign_out_time() : "-");
        return view;
    }
}
