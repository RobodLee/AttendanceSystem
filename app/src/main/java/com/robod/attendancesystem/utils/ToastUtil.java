package com.robod.attendancesystem.utils;

import android.widget.Toast;

import com.robod.attendancesystem.MyApplication;

/**
 * @author Robod
 * @date 2020/10/3 8:31
 */
public class ToastUtil {

    public static void Pop(String content) {
        Toast.makeText(MyApplication.getContext(),content,Toast.LENGTH_SHORT).show();
    }

}
