package com.robod.attendancesystem;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.fragment.AttendanceDetailsFragment;
import com.robod.attendancesystem.fragment.MarkFragment;
import com.robod.attendancesystem.service.MyService;
import com.robod.attendancesystem.utils.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.nav_view)
    private NavigationView navView;
    private AlertDialog adminPasswordDialog;            //输入管理员密码的对话框

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        replaceFragment(new MarkFragment());

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
                        popPasswordDialog(1);
                        break;
                    case R.id.nav_students_management:
                        popPasswordDialog(2);
                        break;
                    default:
                }
                return true;
            }
        });

        preferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);

        //如果管理员密码为空则初始化管理员密码为 “123456”
        String adminPassword = preferences.getString(Constants.ADMIN_PASSWORD_KEY, "");
        if (TextUtils.isEmpty(adminPassword)) {
            adminPassword = "123456";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.ADMIN_PASSWORD_KEY, adminPassword);
            editor.apply();
        }

        //如果支持蓝牙而且没有打开蓝牙功能就打开蓝牙
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }

        startService( new Intent(this, MyService.class));
    }

    /**
     * 弹出输入管理员密码的对话框并根据传入的operation进行相应的操作
     *
     * @param operation 1.设置；2.学生管理
     */
    private void popPasswordDialog(final int operation) {
        final View view = LayoutInflater.from(this).inflate(R.layout.admin_password_dialog, null);
        view.findViewById(R.id.dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEt = view.findViewById(R.id.dialog_password);
                String inputPassword = passwordEt.getText().toString();    //输入框中输入的密码
                String adminPassword = preferences.getString(Constants.ADMIN_PASSWORD_KEY, "");             //SharePreferences中存储的密码
                if (TextUtils.isEmpty(inputPassword)) {
                    ToastUtil.Pop("请输入密码");
                } else if (adminPassword.equals(inputPassword)) {
                    adminPasswordDialog.dismiss();
                    switch (operation) {
                        case 1:
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, StudentsManagementActivity.class));
                            break;
                        default:
                    }
                } else {
                    passwordEt.setText("");
                    ToastUtil.Pop("密码错误");
                }
            }
        });
        adminPasswordDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        adminPasswordDialog.show();
    }

    @Event(value = {R.id.mark_fragment_btn, R.id.attendance_details_btn})
    private void myClick(View v) {
        switch (v.getId()) {
            case R.id.mark_fragment_btn:
                replaceFragment(new MarkFragment());
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