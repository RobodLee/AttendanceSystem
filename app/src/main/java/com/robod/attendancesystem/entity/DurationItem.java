package com.robod.attendancesystem.entity;

/**
 * @author Robod
 * @date 2020/10/29 16:15
 */
public class DurationItem {

    private String studentName;
    private int durationDay;
    private int durationMonth;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(int durationDay) {
        this.durationDay = durationDay;
    }

    public int getDurationMonth() {
        return durationMonth;
    }

    public void setDurationMonth(int durationMonth) {
        this.durationMonth = durationMonth;
    }

    @Override
    public String toString() {
        return "DurationItem{" +
                "studentName='" + studentName + '\'' +
                ", durationDay=" + durationDay +
                ", durationMonth=" + durationMonth +
                '}';
    }
}
