package com.robod.attendancesystem.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.robod.attendancesystem.R;
import com.robod.attendancesystem.adapter.DurationAdapter;
import com.robod.attendancesystem.entity.DurationItem;
import com.robod.attendancesystem.entity.Record;
import com.robod.attendancesystem.entity.Student;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Robod
 * @date 2020/10/29 10:50
 */
public class DurationFragment extends Fragment {

    private static final String TAG = "DurationFragment";

    private TextView currentDate;   //所选择的需要展示数据的日期
    private ListView durationLv;     //用来展示数据的ListView

    private int mYear;
    private int mMonth;
    private int mDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duration, container, false);
        currentDate = view.findViewById(R.id.duration_date);
        durationLv = view.findViewById(R.id.duration_list_view);

        init();

        return view;
    }

    private void init() {
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
        List<DurationItem> durationItems = new ArrayList<>();
        List<Student> students = LitePal.findAll(Student.class);
        for (Student student : students) {
            DurationItem durationItem = new DurationItem();
            durationItem.setStudentName(student.getName());
            int durationDay = LitePal.where("student_num = ? and date(date_string) = date(?)", student.getNumber(),
                    mYear + "-" + (mMonth < 10 ? "0" + mMonth : mMonth) + "-" + (mDay < 10 ? "0" + mDay : mDay))
                    .sum(Record.class, "duration", Integer.class);
            durationItem.setDurationDay(durationDay);
            int durationMonth = LitePal.where("student_num = ? and " +
                    "substr(date(date_string),1,7) = ?", student.getNumber(), mYear + "-" + (mMonth < 10 ? "0" + mMonth : mMonth))
                    .sum(Record.class, "duration", Integer.class);
            durationItem.setDurationMonth(durationMonth);
            durationItems.add(durationItem);
            Log.e(TAG,durationItem.toString());
        }
        DurationAdapter adapter = new DurationAdapter(getActivity(), R.layout.duration_item, durationItems);
        durationLv.setAdapter(adapter);
    }

}
