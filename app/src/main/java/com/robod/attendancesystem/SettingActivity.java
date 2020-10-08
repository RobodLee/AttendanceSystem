package com.robod.attendancesystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    @ViewInject(R.id.sign_in_time_spi)
    private Spinner signInSpinner;

    @ViewInject(R.id.sign_out_time_spi)
    private Spinner signOutSpinner;

    @ViewInject(R.id.admin_password)
    private EditText adminPasswordEt;

    private SharedPreferences preferences;
    private int signInTime;
    private int signOutTime;
    private String adminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        init();

    }

    private void init() {
        preferences = getSharedPreferences(Constants.SP_NAME,MODE_PRIVATE);

        signInTime = preferences.getInt(Constants.SIGN_IN_TIME_KEY, 0);
        signOutTime = preferences.getInt(Constants.SIGN_OUT_TIME_KEY, 0);
        adminPassword = preferences.getString(Constants.ADMIN_PASSWORD_KEY,"");

        ArrayAdapter signInAdapter = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        signInAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signInSpinner.setAdapter(signInAdapter);
        signInSpinner.setSelection(signInTime);

        ArrayAdapter signOutAdapter = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
        signOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signOutSpinner.setAdapter(signOutAdapter);
        signOutSpinner.setSelection(signOutTime);

        adminPasswordEt.setText(adminPassword);
    }

    @Event(value = {R.id.back,R.id.save_setting})
    private void myClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save_setting:
                signInTime = signInSpinner.getSelectedItemPosition();
                signOutTime = signOutSpinner.getSelectedItemPosition();
                adminPassword = adminPasswordEt.getText().toString();
                if (signInTime >= signOutTime) {
                    ToastUtil.Pop("签到时间必须早于签退时间");
                } else if (TextUtils.isEmpty(adminPassword)) {
                    ToastUtil.Pop("管理员密码不能为空");
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(Constants.SIGN_IN_TIME_KEY, signInSpinner.getSelectedItemPosition());
                    editor.putInt(Constants.SIGN_OUT_TIME_KEY, signOutSpinner.getSelectedItemPosition());
                    editor.putString(Constants.ADMIN_PASSWORD_KEY, adminPassword);
                    editor.apply();
                    ToastUtil.Pop("保存成功");
                }
                break;
            default:
        }
    }
}