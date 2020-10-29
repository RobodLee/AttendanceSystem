package com.robod.attendancesystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.entity.DurationItem;

import java.util.List;

/**
 * @author Robod
 * @date 2020/10/29 17:04
 * 时长Fragment界面的ListView的适配器
 */
public class DurationAdapter extends ArrayAdapter<DurationItem> {

    private List<DurationItem> durationItems;
    private int resourceId;

    public DurationAdapter(@NonNull Context context, int resourceId, @NonNull List<DurationItem> durationItems) {
        super(context, resourceId, durationItems);
        this.durationItems = durationItems;
        this.resourceId = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DurationItem durationItem = durationItems.get(position);
        ViewHolder viewHolder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.studentName = view.findViewById(R.id.duration_student_name);
            viewHolder.durationDay = view.findViewById(R.id.duration_today);
            viewHolder.durationMonth = view.findViewById(R.id.duration_this_month);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.studentName.setText(durationItem.getStudentName());
        viewHolder.durationDay.setText(durationItem.getDurationDay()+"");
        viewHolder.durationMonth.setText(durationItem.getDurationMonth()+"");
        return view;
    }

    static class ViewHolder {
        private TextView studentName;
        private TextView durationDay;
        private TextView durationMonth;
    }

}
