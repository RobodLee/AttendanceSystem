package com.robod.attendancesystem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.robod.attendancesystem.R;

/**
 * @author Robod
 * @date 2020/9/29 9:39
 * 签到 / 签退界面的Fragment
 */
public class SignInOutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_out_fragment, container, false);
        return view;
    }
}
