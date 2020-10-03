package com.robod.attendancesystem.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * @author Robod
 * @date 2020/9/29 19:23
 */
public class Student extends LitePalSupport implements Serializable {

    private Integer id;         //主键
    private String name;        //学号
    private String number;      //学号
    private String class_;      //班级

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }
}
