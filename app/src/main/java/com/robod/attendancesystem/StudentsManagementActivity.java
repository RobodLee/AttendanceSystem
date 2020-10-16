package com.robod.attendancesystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.robod.attendancesystem.adapter.StudentAdapter;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.BaiduFaceUtils;
import com.robod.attendancesystem.utils.ToastUtil;

import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            R.layout.student_item, students);
                    studentsListView.setAdapter(adapter);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        studentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Student student = LitePal.findAll(Student.class).get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(StudentsManagementActivity.
                        this);
                dialog.setMessage("确定删除吗？将会删除该学生所有的 签到/签退 信息");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //1.删除百度数据库中的人脸数据
                        Map<String,String> paramMap = new HashMap<>();
                        paramMap.put("user_id",student.getNumber());
                        boolean isSuccess = BaiduFaceUtils.delete(paramMap);
                        //2.从record表中删除签到签退记录
                        List<Record> records = LitePal.where("student_num = ?", student.getNumber()).find(Record.class);
                        if (isSuccess) {
                            for (Record record : records) {
                                record.delete();
                            }
                            records = LitePal.where("student_num = ?", student.getNumber()).find(Record.class);
                        }
                        //3.从student表中删除学生信息
                        if (records == null || records.size() == 0) {
                            student.delete();
                        }
                        ToastUtil.Pop("删除成功");
                        displayStudents();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    //展示学生数据
    private void displayStudents() {
        List<Student> students = LitePal.findAll(Student.class);
        if (!students.isEmpty()) {
            StudentAdapter adapter = new StudentAdapter(StudentsManagementActivity.this,
                    R.layout.student_item, students);
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