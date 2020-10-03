package com.robod.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.robod.attendancesystem.adapter.StudentAdapter;
import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.BaiduFaceUtils;

import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

@ContentView(R.layout.activity_students_management)
public class StudentsManagementActivity extends AppCompatActivity {

    @ViewInject(R.id.swipe_refresh)
    private SwipeRefreshLayout swipeRefreshLayout;

    @ViewInject(R.id.students_list_view)
    private ListView studentsListView;  //展示学生列表的listview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayStudents();
    }

    //初始化数据或添加事件
    private void init() {
        displayStudents();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<Student> students = BaiduFaceUtils.getStudents();
                if (students != null && students.size() > 0) {
                    StudentAdapter adapter = new StudentAdapter(StudentsManagementActivity.this,
                            R.layout.student_item,students);
                    studentsListView.setAdapter(adapter);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        studentsListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    //展示学生数据
    private void displayStudents() {
        List<Student> students = LitePal.findAll(Student.class);
        if (!students.isEmpty()) {
            StudentAdapter adapter = new StudentAdapter(StudentsManagementActivity.this,
                    R.layout.student_item,students);
            studentsListView.setAdapter(adapter);
        }
    }

    //页面控件的点击事件
    @Event(value = {R.id.back, R.id.add_student})
    private void myClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_student:
                startActivity(new Intent(this, AddStudentActivity.class));
                break;
            default:
        }
    }

}