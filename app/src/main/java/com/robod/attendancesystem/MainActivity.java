package com.robod.attendancesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.fragment.AttendanceDetailsFragment;
import com.robod.attendancesystem.fragment.SignInOutFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.nav_view)
    private NavigationView navView;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        replaceFragment(new SignInOutFragment());

        init();

    }

    //初始化数据及注册部分控件的事件
    private void init() {
        //滑动菜单
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                    case R.id.nav_students_management:
                        startActivity(new Intent(MainActivity.this, StudentsManagementActivity.class));
                        break;
                    default:
                }
                return true;
            }
        });

        preferences = getSharedPreferences(Constants.SETTING_INFO,MODE_PRIVATE);

        //如果管理员密码为空则初始化管理员密码为 “123456”
        String adminPassword = preferences.getString(Constants.ADMIN_PASSWORD_KEY,"");
        if (TextUtils.isEmpty(adminPassword)) {
            adminPassword = "123456";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.ADMIN_PASSWORD_KEY,adminPassword);
            editor.apply();
        }
    }

    @Event(value = {R.id.sign_in_out_btn, R.id.attendance_details_btn})
    private void myClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_out_btn:
                replaceFragment(new SignInOutFragment());
                break;
            case R.id.attendance_details_btn:
                replaceFragment(new AttendanceDetailsFragment());
            default:
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commit();
    }
}