package com.robod.attendancesystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author Robod
 * @date 2020/10/21 11:09
 * 今日打卡列表的适配器
 */
public class TodayMarkAdapter extends ArrayAdapter<Student> {

    private int resourceId;
    private List<Student> students;

    public TodayMarkAdapter(@NonNull Context context, int resourceId, @NonNull List<Student> students) {
        super(context, resourceId, students);
        this.resourceId = resourceId;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Student student = students.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.statusCircle = view.findViewById(R.id.status_circle);
            viewHolder.studentName = view.findViewById(R.id.student_name);
            viewHolder.lastSignInTime = view.findViewById(R.id.last_sign_in_time);
            viewHolder.lastSignOutTime = view.findViewById(R.id.last_sign_out_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.studentName.setText(student.getName());
        List<Record> records = LitePal.where("student_num = ? and " +
                "date(date_string) == date('now') ", student.getNumber())
                .find(Record.class);
        if (records == null || records.size() == 0) {     //没有签到记录，今天没来过
            viewHolder.statusCircle.setTextColor(Color.RED);
            viewHolder.lastSignInTime.setText("-");
            viewHolder.lastSignOutTime.setText("-");
        } else {
            Record record = LitePal.where("student_num = ? and " +
                    "date(date_string) == date('now') and " +
                    "sign_in_time = ? ", student.getNumber(),
                    LitePal.max(Record.class,"sign_in_time",String.class))
                    .find(Record.class).get(0);
            String signOutTime = TextUtils.isEmpty(record.getSign_out_time()) ?
                    "-" : record.getSign_out_time();
            viewHolder.lastSignInTime.setText(record.getSign_in_time());
            viewHolder.lastSignOutTime.setText(signOutTime);
            if ("-".equals(signOutTime)) {
                viewHolder.statusCircle.setTextColor(Color.GREEN);
            }
        }
        return view;
    }

    static class ViewHolder {
        TextView statusCircle;  //显示签到/签退状态的圆圈
        TextView studentName;   //学生姓名
        TextView lastSignInTime;    //今日最后签到时间
        TextView lastSignOutTime;   //今日最后签退时间
    }
}
