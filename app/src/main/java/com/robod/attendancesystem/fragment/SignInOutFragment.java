package com.robod.attendancesystem.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.robod.attendancesystem.adapter.TodayRecordsAdapter;
import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.entity.MessageEvent;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.BaiduFaceUtils;
import com.robod.attendancesystem.utils.Base64Util;
import com.robod.attendancesystem.utils.EventBusUtil;
import com.robod.attendancesystem.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
 * @date 2020/9/29 9:39
 * 签到 / 签退界面的Fragment
 */
public class SignInOutFragment extends Fragment {

    private ListView todayRecordsLv;    //展示当日签到或者签退记录的ListView
    private Button signInOutBtn;        //签到签退的按钮

    private static final String TAG = "SignInOutFragment";

    private SharedPreferences preferences;
    public static final int TAKE_PHOTO = 1;
    private int signInOutMode;   //0:未开始签到/签退    1:签到   2:签退
    private File imageFile;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_out_fragment, container, false);
        todayRecordsLv = view.findViewById(R.id.today_records_list_view);
        signInOutBtn = view.findViewById(R.id.sign_in_out_btn);

        EventBus.getDefault().register(this);   //注册事件
        preferences = getActivity().getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);

        initView();
        return view;
    }

    //初始化界面及添加控件的点击事件
    private void initView() {
        signInOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int signInTime = preferences.getInt(Constants.SIGN_IN_TIME_KEY, 0);
                int signOutTime = preferences.getInt(Constants.SIGN_OUT_TIME_KEY, 0);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour < signInTime - 1) {    //未到签到时间
                    ToastUtil.Pop("签到将于 " + (signInTime - 1) + ":00 开始");
                } else if (hour >= signInTime - 1 && hour < signOutTime) {    //签到
                    takePhoto();
                } else {    //签退
                    takePhoto();
                }
            }
        });

        //初始化 签到/签退 列表
        Calendar calendar = Calendar.getInstance();
        int signInTime = preferences.getInt(Constants.SIGN_IN_TIME_KEY, 0);
        int signOutTime = preferences.getInt(Constants.SIGN_OUT_TIME_KEY, 0);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < signInTime - 1) {    //未开始 签到/签退
            signInOutMode = 0;
            signInOutBtn.setText("未开始");
            signInOutBtn.setClickable(false);
        }
        if (hour >= signInTime - 1 && hour < signOutTime) { //显示签到信息
            signInOutMode = 1;
            signInOutBtn.setText("签到");
            signInOutBtn.setClickable(true);
        } else if (hour >= signOutTime) { //显示签退信息
            signInOutMode = 2;
            signInOutBtn.setText("签退");
            signInOutBtn.setClickable(true);
        }
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
                            EventBusUtil.post(Constants.MESSAGE_TO_SERVICE,
                                    signInOutMode == 1 ? Constants.SIGN_IN_FAIL : Constants.SIGN_OUT_FAIL);
                            ToastUtil.Pop("~人脸认证失败~");
                        } else {
                            String[] resultArray = result.split("_");
                            String number = resultArray[0];
                            String name = resultArray[1];

                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf = new SimpleDateFormat();
                            Calendar calendar = Calendar.getInstance();
                            List<Record> records = LitePal.where("student_num = ? and " +
                                    "date(date_string) == date('now')", number)
                                    .find(Record.class);

                            if (signInOutMode == 1) {
                                if (records == null || records.size() == 0) {
                                    Record record = new Record();
                                    record.setStudent_name(name);
                                    record.setStudent_num(number);
                                    sdf.applyPattern("yyyy-MM-dd");
                                    record.setDate_string(sdf.format(calendar.getTime()));
                                    sdf.applyPattern("HH:mm");
                                    record.setSign_in_time(sdf.format(calendar.getTime()));
                                    record.setStatus("0");
                                    record.save();
                                    ToastUtil.Pop(name + " 签到成功");
                                    EventBusUtil.post(Constants.MESSAGE_TO_SERVICE, Constants.SIGN_IN_SUCCESS);
                                    checkSignNum();
                                } else {
                                    Record record = records.get(0);
                                    if ("0".equals(record.getStatus()) || "1".equals(record.getStatus())) {
                                        ToastUtil.Pop(name + " 不能重复签到");
                                    } else if ("2".equals(record.getStatus())) {
                                        ToastUtil.Pop(name + " 请假了,不能签到");
                                    }
                                }
                            } else if (signInOutMode == 2) {
                                if (records != null && records.size() > 0) {
                                    Record record = records.get(0);
                                    if ("0".equals(record.getStatus())) {
                                        sdf.applyPattern("HH:mm");
                                        record.setSign_out_time(sdf.format(calendar.getTime()));
                                        record.setStatus("1");
                                        record.save();
                                        ToastUtil.Pop(name + " 签退成功");
                                        EventBusUtil.post(Constants.MESSAGE_TO_SERVICE, Constants.SIGN_OUT_SUCCESS);
                                        checkSignNum();
                                    } else if ("1".equals(record.getStatus())) {
                                        ToastUtil.Pop(name + " 不能重复签退");
                                    } else if ("2".equals(record.getStatus())) {
                                        ToastUtil.Pop(name + " 请假了,不能签退");
                                    }
                                } else {    //未查询出记录则说明未进行签到
                                    ToastUtil.Pop(name + " 未签到，不能签退");
                                }
                            }
                        }
                        refreshListView();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 刷新展示 签到/签退 的ListView
     */
    private void refreshListView() {
        if (signInOutMode != 0) {
            TodayRecordsAdapter adapter = new TodayRecordsAdapter(getActivity(), R.layout.today_record_item,
                    LitePal.findAll(Student.class), signInOutMode);
            todayRecordsLv.setAdapter(adapter);
        } else { //如果未到签到时间，则页面不显示内容
            todayRecordsLv.setAdapter(null);
        }
    }

    //检查 签到/签退 的人齐了没有，齐了就通知下位机关闭蓝牙
    private void checkSignNum() {
        int studentNum = LitePal.findAll(Student.class).size();
        int recordNum = 0;
        if (signInOutMode == 1) {
            //status=0,1 是签到了，2是请假了，没签到则没数据，所以只要判断今天的record数据量是否是人数一致就可以判断出人是否齐了
            recordNum = LitePal.where("date(date_string) == date('now')").find(Record.class).size();
        } else if (signInOutMode == 2) {
            //status 为1，2的数据量和人数一致则说明人齐了
            recordNum = LitePal.where("date(date_string) == date('now') " +
                    " and status > ?", "0").find(Record.class).size();
        }
        if (studentNum == recordNum) {
            EventBusUtil.post(Constants.MESSAGE_TO_SERVICE, Constants.CLOSE_VOICE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == 1) {
            initView();
        }
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        Log.d(TAG, "onAttach: ");
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: ");
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Log.d(TAG, "onActivityCreated: ");
//    }

    /**
     * 从设置或者学生管理界面返回到SignInOutFragment中时会调用onStart方法，重新刷新列表内容
     */
    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume: ");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause: ");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop: ");
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d(TAG, "onDestroyView: ");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy: ");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d(TAG, "onDetach: ");
//    }
}
