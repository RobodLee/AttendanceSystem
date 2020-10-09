package com.robod.attendancesystem.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.adapter.AttendanceDetailsAdapter;
import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;
import com.robod.attendancesystem.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robod
 * @date 2020/9/29 9:42
 * 考勤详情的Fragment
 */
public class AttendanceDetailsFragment extends Fragment {

    private TextView currentDate;   //所选择的需要展示数据的日期
    private ListView detailsLV;     //用来展示数据的ListView
    private AlertDialog itemLongClickDialog;            //长按ListView子项弹出的对话框
    private AlertDialog adminPasswordDialog;            //输入管理员密码的对话框

    private SharedPreferences preferences;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance_details_fragment, container, false);
        currentDate = view.findViewById(R.id.attendance_details_date);
        detailsLV = view.findViewById(R.id.attendance_details_list_view);

        init();

        return view;
    }

    private void init() {
        preferences = getActivity().getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);

        //初始化当前的日期
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = mYear + "年" + mMonth + "月" + mDay + "日";
        currentDate.setText(dateString);
        refreshListView();

        //显示日期TextView的点击事件
        currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dlg = new DatePickerDialog(new ContextThemeWrapper(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar), null, mYear, mMonth - 1, mDay) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        LinearLayout mSpinners = (LinearLayout) findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
                        if (mSpinners != null) {
                            NumberPicker mYearSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                            NumberPicker mMonthSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                            NumberPicker mDaySpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                            mSpinners.removeAllViews();
                            if (mYearSpinner != null) {
                                mSpinners.addView(mYearSpinner);
                            }
                            if (mMonthSpinner != null) {
                                mSpinners.addView(mMonthSpinner);
                            }
                            if (mDaySpinner != null) {
                                mSpinners.addView(mDaySpinner);
                            }
                        }
                    }

                    @Override
                    public void onDateChanged(@NotNull DatePicker view, int year, int month, int day) {
                        super.onDateChanged(view, year, month, day);
                        mYear = year;
                        mMonth = month + 1;
                        mDay = day;
                        String dateString = mYear + "年" + mMonth + "月" + mDay + "日";
                        currentDate.setText(dateString);
                        refreshListView();
                    }
                };
                dlg.show();
            }
        });

    }

    /**
     * 刷新列表
     */
    private void refreshListView() {
        final List<Record> records = LitePal.where("date(date_string) = date(?)",
                mYear + "-" + (mMonth < 10 ? "0" + mMonth : mMonth) + "-" + (mDay < 10 ? "0" + mDay : mDay))
                .find(Record.class);
        Map<String, Record> recordMap = listToMap(records);
        List<Student> students = LitePal.findAll(Student.class);
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (recordMap.get(student.getNumber()) == null) {
                Record record = new Record();
                record.setStudent_name(student.getName());
                record.setStudent_num(student.getNumber());
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd");
                record.setDate_string(sdf.format(calendar.getTime()));
                records.add(record);
            }
        }
        AttendanceDetailsAdapter adapter = new AttendanceDetailsAdapter(getActivity(), R.layout.attendance_details_item, records);
        detailsLV.setAdapter(adapter);

        //签到详情ListView子项长按事件
        detailsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Record record = records.get(position);
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.attendance_details_item_long_click_dialog, null);
                dialogView.findViewById(R.id.normal_sign_in_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popPasswordDialog(record, 1);
                    }
                });
                dialogView.findViewById(R.id.ask_for_leave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popPasswordDialog(record, 2);
                    }
                });
                dialogView.findViewById(R.id.not_sign_in_out).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popPasswordDialog(record, 3);
                    }
                });
                itemLongClickDialog = new AlertDialog.Builder(getActivity())
                        .setView(dialogView)
                        .create();
                itemLongClickDialog.show();
                return true;
            }
        });
    }

    /**
     * 弹出输入管理员密码的对话框并根据传入的operation进行相应的操作
     *
     * @param record
     * @param operation 1.正常签到签退；2.请假；3.未签到签退
     */
    private void popPasswordDialog(final Record record, final int operation) {
        itemLongClickDialog.dismiss();
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.admin_password_dialog, null);
        view.findViewById(R.id.dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEt = view.findViewById(R.id.dialog_password);
                String inputPassword = passwordEt.getText().toString();    //输入框中输入的密码
                String adminPassword = preferences.getString(Constants.ADMIN_PASSWORD_KEY, "");             //SharePreferences中存储的密码
                if (TextUtils.isEmpty(inputPassword)) {
                    ToastUtil.Pop("请输入密码");
                } else if (adminPassword.equals(inputPassword)) {
                    switch (operation) {
                        case 1:
                            int signInTime = preferences.getInt(Constants.SIGN_IN_TIME_KEY, 0);
                            int signOutTime = preferences.getInt(Constants.SIGN_OUT_TIME_KEY, 0);
                            record.setSign_in_time((signInTime < 10 ? "0" + signInTime : signInTime) + ":00");
                            record.setSign_out_time((signOutTime < 10 ? "0" + signOutTime : signOutTime) + ":00");
                            record.setStatus("1");
                            record.save();
                            break;
                        case 2:
                            record.delete();    //当修改db中已存在非空数据为null时仍会保留原有值，所以先delete再重新save
                            record.setId(0);
                            record.setSign_in_time(null);
                            record.setSign_out_time(null);
                            record.setStatus("2");
                            record.save();
                            break;
                        case 3:
                            record.delete();
                            break;
                        default:
                    }
                    adminPasswordDialog.dismiss();
                    refreshListView();
                } else {
                    passwordEt.setText("");
                    ToastUtil.Pop("密码错误");
                }
            }
        });
        adminPasswordDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        adminPasswordDialog.show();
    }

    /**
     * 将List转为Map
     *
     * @param records
     * @return
     */
    private Map<String, Record> listToMap(List<Record> records) {
        Map<String, Record> recordMap = new HashMap<>();
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            recordMap.put(record.getStudent_num(), record);
        }
        return recordMap;
    }
}
