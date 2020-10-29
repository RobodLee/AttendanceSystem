package com.robod.attendancesystem.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * @author Robod
 * @date 2020/10/3 9:55
 */
public class Record extends LitePalSupport implements Serializable {

    private int id;                 //主键
    private String student_name;    //姓名
    private String student_num;     //学号，和user表关联
    private String date_string;    //日期字符串，yyyy-MM-dd
    private String sign_in_time;    //签到时间
    private String sign_out_time;   //签退时间
    private int duration;           //时长，签退时间-签到时间
    private String status;          //状态，0表示签到了但是没签退,1表示正常签到签退

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_num() {
        return student_num;
    }

    public void setStudent_num(String student_num) {
        this.student_num = student_num;
    }

    public String getDate_string() {
        return date_string;
    }

    public void setDate_string(String date_string) {
        this.date_string = date_string;
    }

    public String getSign_in_time() {
        return sign_in_time;
    }

    public void setSign_in_time(String sign_in_time) {
        this.sign_in_time = sign_in_time;
    }

    public String getSign_out_time() {
        return sign_out_time;
    }

    public void setSign_out_time(String sign_out_time) {
        this.sign_out_time = sign_out_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", student_name='" + student_name + '\'' +
                ", student_num='" + student_num + '\'' +
                ", date_string='" + date_string + '\'' +
                ", sign_in_time='" + sign_in_time + '\'' +
                ", sign_out_time='" + sign_out_time + '\'' +
                ", duration=" + duration +
                ", status='" + status + '\'' +
                '}';
    }
}
