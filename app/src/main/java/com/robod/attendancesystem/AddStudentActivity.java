package com.robod.attendancesystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.BaiduFaceUtils;
import com.robod.attendancesystem.utils.Base64Util;
import com.robod.attendancesystem.utils.ToastUtil;

import org.litepal.LitePal;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_add_student)
public class AddStudentActivity extends AppCompatActivity {

    @ViewInject(R.id.student_name)
    private EditText studentName;  //学生姓名

    @ViewInject(R.id.student_number)
    private EditText studentNumber;    //学号,用户id

    @ViewInject(R.id.student_class)
    private EditText studentClass;   //班级

    public static final int TAKE_PHOTO = 1;
    private File imageFile;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

    @Event(value = {R.id.input_face})
    private void myClick(View v) {
        switch (v.getId()) {
            case R.id.input_face:
                if (TextUtils.isEmpty(studentName.getText())) {
                    Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(studentNumber.getText())) {
                    Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(studentClass.getText())) {
                    Toast.makeText(this, "请输入班级", Toast.LENGTH_SHORT).show();
                } else if (LitePal.where("number = ?", studentNumber.getText().toString())
                        .find(Student.class).size() > 0) {
                    //检测数据库中是否存在该用户，如果存在则弹出提示
                    ToastUtil.Pop("该用户已存在");
                } else {
                    // 创建File对象，用于存储拍照后的图片
                    imageFile = new File(getExternalCacheDir(),
                            "output_image.jpg");
                    try {
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(AddStudentActivity.this,
                                "com.robod.attendancesystem.fileprovider", imageFile);
                    } else {
                        imageUri = Uri.fromFile(imageFile);
                    }
                    // 启动相机程序
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imageBase64String = Base64Util.encode(imageFile);
                    if (TextUtils.isEmpty(imageBase64String)) {
                        Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("image", imageBase64String);   //图片的base64编码
                        paramMap.put("user_id", studentNumber.getText().toString());    //用户id，这里为学号
                        paramMap.put("user_info", studentNumber.getText().toString() + "_" +
                                studentName.getText().toString() + "_" +
                                studentClass.getText().toString());  //用户资料,格式为  123456789_张三_18物联网
                        boolean result = BaiduFaceUtils.register(paramMap);
                        if (result) { //注册成功，将数据添加到本地数据库
                            ToastUtil.Pop("注册成功");
                            Student student = new Student();
                            student.setName(studentName.getText().toString());
                            student.setNumber(studentNumber.getText().toString());
                            student.setClass_(studentClass.getText().toString());
                            student.save();
                            finish();
                        } else {
                            ToastUtil.Pop("~~Error~~");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}