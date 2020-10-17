package com.robod.attendancesystem.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.entity.MessageEvent;
import com.robod.attendancesystem.utils.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private static final String TAG = "MyService";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothGatt mBluetoothGatt;

    private String writeCharacterUuid = "0000ffe1-0000-1000-8000-00805f9b34fb";     //写通道uuid
    private String notifyCharacterUuid = "0000fff7-0000-1000-8000-00805f9b34fb";   //通知通道 uuid
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristicNotify;
    private boolean flag = false;   //是否连接蓝牙

    private SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences(Constants.SP_NAME,MODE_PRIVATE);

        EventBus.getDefault().register(this);   //注册事件
        connectBLE();
        timeMonitor();
        return super.onStartCommand(intent, flags, startId);
    }

    //连接蓝牙
    private void connectBLE() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = mBluetoothAdapter.getRemoteDevice(Constants.BLE_ADDRESS);
        mBluetoothGatt = device.connectGatt(MyService.this, false, mGattCallback);
    }

    // BLE回调操作
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 连接成功
                mBluetoothGatt.discoverServices();
                Log.d(TAG, "连接成功");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 连接断开
                Log.d(TAG,"连接失败");
                flag = false;
                connectBLE();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //发现设备，遍历服务，初始化特征
                initBLE(gatt);
            } else {
                Log.d(TAG, "onServicesDiscovered fail-->" + status);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 收到的数据
                byte[] receiveByte = characteristic.getValue();
            } else {
                Log.d(TAG, "onCharacteristicRead fail-->" + status);
            }
        }

        //收到BLE终端写入数据回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 发送成功
                Log.d(TAG, "发送成功");
            } else {
                // 发送失败
            }
        }
    };

    //初始化特征
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initBLE(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        //遍历所有服务
        for (android.bluetooth.BluetoothGattService BluetoothGattService : gatt.getServices()) {
            //遍历所有特征
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : BluetoothGattService.getCharacteristics()) {
                Log.e(TAG, bluetoothGattCharacteristic.getUuid().toString());
                String str = bluetoothGattCharacteristic.getUuid().toString();
                if (str.equals(writeCharacterUuid)) {
                    //根据写UUID找到写特征
                    mBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                    flag = true;
                } else if (str.equals(notifyCharacterUuid)) {
                    //根据通知UUID找到通知特征
                    mBluetoothGattCharacteristicNotify = bluetoothGattCharacteristic;
                    mBluetoothGatt.setCharacteristicNotification(mBluetoothGattCharacteristicNotify, true);
                }
            }
        }
    }

    //向蓝牙里面发送数据
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean writeToBLE(String data) {
        if (flag) {
            mBluetoothGattCharacteristic.setValue(data);
            //调用蓝牙服务的写特征值方法实现发送数据
            mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
            return true;
        }
        return false;
    }

    //处理EventBus中的消息
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void handelMessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == Constants.MESSAGE_TO_SERVICE){
            writeToBLE(messageEvent.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    //监听时间的变化
    private void timeMonitor() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();

                //如果到了签到或者签退的时间就通知SignInOutFragment修改按钮内容
                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (minute == 0) {
                    int signInTime = preferences.getInt(Constants.SIGN_IN_TIME_KEY, 0);
                    int signOutTime = preferences.getInt(Constants.SIGN_OUT_TIME_KEY, 0);
                    if (hour == signInTime-1) {
                        EventBusUtil.post(Constants.MESSAGE_TO_FRAGMENT,"开始签到");
                        writeToBLE(Constants.START_SIGN_IN);
                    } else if (hour == signOutTime) {
                        EventBusUtil.post(Constants.MESSAGE_TO_SERVICE,"开始签退");
                        writeToBLE(Constants.START_SIGN_OUT);
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,5*1000,50*1000);
    }
}
