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
import com.robod.attendancesystem.entity.Student;

import java.util.List;

/**
 * @author Robod
 * @date 2020/9/30 10:31
 * 学生列表的适配器
 */
public class StudentAdapter extends ArrayAdapter<Student> {

    private List<Student> students;
    private int resourceId; //布局的id R.layout.student_item.xml

    public StudentAdapter(@NonNull Context context, int resourceId, @NonNull List<Student> students) {
        super(context, resourceId, students);
        this.resourceId = resourceId;
        this.students = students;
    }

    class ViewHolder {
        TextView studentName;
        TextView studentNumber;
        TextView studentClass;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Student student = students.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.studentName = view.findViewById(R.id.student_item_name);
            viewHolder.studentNumber = view.findViewById(R.id.student_item_number);
            viewHolder.studentClass = view.findViewById(R.id.student_item_class);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.studentName.setText(student.getName());
        viewHolder.studentNumber.setText(student.getNumber());
        viewHolder.studentClass.setText(student.getClass_());
        return view;
    }

}
