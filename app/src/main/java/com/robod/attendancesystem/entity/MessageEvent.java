package com.robod.attendancesystem.entity;

/**
 * @author Robod
 * @date 2020/10/16 19:40
 * EventBus的消息实体类
 */
public class MessageEvent {

    private int type;           //表示该条消息时发送给谁的，1：SignInOutFragment 2.MyService
    private String message;     //消息内容

    public MessageEvent(){
    }

    public MessageEvent(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
