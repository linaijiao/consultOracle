package com.taodev.zhouyi.fourpillars.ui;

import com.taodev.zhouyi.domain.Gender;

import java.io.Serializable;

public class FourPillarsInputUiModel implements Serializable

{
    // 基础信息
    public String name;
    public Gender gender;

    // 时间信息
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    // 地点信息 (用于后续算真太阳时)
    public String province;
    public String city;

    // 构造函数：对应 Activity 里的传参顺序
    public FourPillarsInputUiModel(String name, Gender gender,
                                   int year, int month, int day, int hour, int minute,
                                   String province, String city) {
        this.name = name;
        this.gender=gender;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.province = province;
        this.city = city;
    }
}