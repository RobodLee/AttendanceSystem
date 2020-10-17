package com.robod.attendancesystem.utils;

import com.robod.attendancesystem.entity.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Robod
 * @date 2020/10/17 15:06
 */
public class EventBusUtil {

    /**
     * 向EventBus中发送消息
     * @param type      数据类型，1：SignInOutFragment  2：MyService
     * @param message   消息的内容
     */
    public static void post(int type,String message) {
        MessageEvent messageEvent = new MessageEvent(type,message);
        EventBus.getDefault().post(messageEvent);
    }
}
