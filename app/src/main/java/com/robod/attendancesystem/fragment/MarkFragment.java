package com.robod.attendancesystem.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.adapter.TodayMarkAdapter;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.BaiduFaceUtils;
import com.robod.attendancesystem.utils.Base64Util;
import com.robod.attendancesystem.utils.ToastUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * @author Robod
 * @date 2020/10/21 10:54
 * 打卡签到签退的Fragment
 */
public class MarkFragment extends Fragment {

    private ListView todayMarkLv;   //记录今日打卡时间的ListView
    private Button markBtn;         //打卡的按钮

    public static final int TAKE_PHOTO = 1;
    private File imageFile;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark, container, false);
        todayMarkLv = view.findViewById(R.id.today_mark_list_view);
        markBtn = view.findViewById(R.id.mark_btn);
//        EventBus.getDefault().register(this);   //注册事件

        initView();

        return view;
    }

    //初始化界面及添加点击事件
    private void initView() {
        //打开按钮的点击事件
        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        refreshListView();
    }

    //调用相机进行人脸识别
    private void takePhoto() {
        // 创建File对象，用于存储拍照后的图片
        imageFile = new File(getActivity().getExternalCacheDir(),
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
            imageUri = FileProvider.getUriForFile(getActivity(),
                    "com.robod.attendancesystem.fileprovider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        // 启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);//前置摄像头
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {

                    String imageBase64String = Base64Util.encode(imageFile);
                    if (TextUtils.isEmpty(imageBase64String)) {
                        ToastUtil.Pop("数据异常");
                    } else {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("image", imageBase64String);
                        String result = BaiduFaceUtils.search(paramMap);
                        if (TextUtils.isEmpty(result)) {
                            ToastUtil.Pop("~人脸认证失败~");
                        } else {
                            String[] resultArray = result.split("_");
                            String number = resultArray[0];
                            String name = resultArray[1];

                            SimpleDateFormat sdf = new SimpleDateFormat();
                            Calendar calendar = Calendar.getInstance();

                            List<Record> records = LitePal.where("date(date_string) == date('now') " +
                                    " and student_num = ? " +
                                    " and status = ?", number, "0")
                                    .find(Record.class);
                            Record record = (records == null || records.size() == 0) ? null : records.get(0);
                            if (record == null) {
                                record = new Record();
                                record.setStudent_name(name);
                                record.setStudent_num(number);
                                sdf.applyPattern("yyyy-MM-dd");
                                record.setDate_string(sdf.format(calendar.getTime()));
                                sdf.applyPattern("HH:mm");
                                record.setSign_in_time(sdf.format(calendar.getTime()));
                                record.setStatus("0");
                                ToastUtil.Pop(name + " 签到成功");
                            } else {
                                sdf.applyPattern("HH:mm");
                                record.setSign_out_time(sdf.format(calendar.getTime()));
                                record.setDuration(getDuration(record));
                                record.setStatus("1");
                                ToastUtil.Pop(name + " 签退成功");
                            }
                            record.save();
                            refreshListView();
                        }
                    }
                }
                break;
            default:
        }
    }

    /**
     * 刷新ListView
     */
    private void refreshListView() {
        TodayMarkAdapter adapter = new TodayMarkAdapter(getActivity(), R.layout.today_mark_item,
                LitePal.findAll(Student.class));
        todayMarkLv.setAdapter(adapter);
    }

    //获取签退时间与签到时间相减后的结果
    private int getDuration(Record record) {
        String signInTime = record.getSign_in_time();
        String[] signInArray = signInTime.split(":");
        int signInHour = Integer.parseInt(signInArray[0]);
        int signInMinute = Integer.parseInt(signInArray[1]);

        String signOutTime = record.getSign_out_time();
        String[] signOutArray = signOutTime.split(":");
        int signOutHour = Integer.parseInt(signOutArray[0]);
        int signOutMinute = Integer.parseInt(signOutArray[1]);
        return (signOutHour * 60 + signOutMinute) - (signInHour * 60 + signInMinute);
    }

}
