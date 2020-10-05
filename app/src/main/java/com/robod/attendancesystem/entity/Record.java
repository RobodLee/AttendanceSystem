package com.robod.attendancesystem.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Robod
 * @date 2020/10/3 9:55
 */
public class Record extends LitePalSupport implements Serializable {

    private int id; //主键
    private String student_num; //学号，和user表关联
    private Date date;  //日期
    private String sign_in_time;    //签到时间
    private String sign_out_time;   //签退时间
    private String status;          //状态，1表示正常签到签退，2表示请假

}
