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
import com.robod.attendancesystem.entity.Student;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author Robod
 * @date 2020/10/7 21:15
 * 主界面显示签到签退记录的ListView的adapter
 */
public class TodayRecordsAdapter extends ArrayAdapter<Student> {

    private List<Student> students;
    private int resourceId; //布局的id R.layout.today_record_item.xml
    private int signInOutMode;       //1:签到   2:签退

    public TodayRecordsAdapter(@NonNull Context context, int resourceId,
                               List<Student> students, int signInOutMode) {
        super(context, resourceId, students);
        this.students = students;
        this.resourceId = resourceId;
        this.signInOutMode = signInOutMode;
    }

    class ViewHolder {
        TextView statusCircle;  //显示签到/签退状态的圆圈
        TextView studentName;   //学生姓名
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
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.studentName.setText(student.getName());
        List<Record> records = null;
        if (signInOutMode == 1) {
            //签到的时候判断签到/签退状态是否为0或1，是则说明已经签到过了，改成绿色
             records = LitePal.where("student_num = ? and " +
                    "date(date_string) == date('now') and " +
                    "status < ?",student.getNumber(),"2").find(Record.class);
            if (records != null && records.size()>0) {
                viewHolder.statusCircle.setTextColor(Color.GREEN);
            }
        } else if (signInOutMode == 2)  {
            //签退的时候判断签到/签退状态是否为1，是则说明已经签退过了，改成绿色
            records = LitePal.where("student_num = ? and " +
                    "date(date_string) == date('now') and " +
                    "status = ?",student.getNumber(),"1").find(Record.class);
            if (records != null && records.size()>0) {
                viewHolder.statusCircle.setTextColor(Color.GREEN);
            }
        }
        //没有签到或者签退的记录则判断是否请假了，请假则改为橙色
        if (records == null || records.size()==0) {
            records = LitePal.where("student_num = ? and " +
                    "date(date_string) == date('now') and " +
                    "status = ?",student.getNumber(),"2").find(Record.class);
            if (records != null && records.size()>0) {
                viewHolder.statusCircle.setTextColor(Color.parseColor("#FF9900"));
            }
        }
        return view;
    }
}
