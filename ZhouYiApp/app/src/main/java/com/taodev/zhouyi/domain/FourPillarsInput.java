package com.taodev.zhouyi.domain;

import java.time.LocalDateTime;

public class FourPillarsInput {
    // 命主姓名
    private String name;
    /** 本地时间（用户填的生日+时辰） */
    private LocalDateTime localDateTime;

    /**
     * 时区（默认上海）用户选择的地点/时区
     * 对应前端的下拉框或城市输入
     * */
    private String timezoneIdStr = "Shanghai";

    /** 经度（真太阳时必备） */
    private double longitude = 116.4;  // 默认北京

    /** 性别 */
    private Gender gender;

    public boolean isUseTrueSolarTime() {
        return useTrueSolarTime;
    }

    public void setUseTrueSolarTime(boolean useTrueSolarTime) {
        this.useTrueSolarTime = useTrueSolarTime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /** 是否真太阳时 */
    private boolean useTrueSolarTime = false;

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    /**   "GREGORIAN" 或 "LUNAR" */
    private String calendarType = "GREGORIAN";
    private String province;
    private String city;

    public FourPillarsInput(String name,int year, int month, int day, int hour,int minute ,Gender gender) {
        this.name = name;
        this.localDateTime = LocalDateTime.of(year, month, day, hour, minute);
        this.gender=gender;

    }
    public FourPillarsInput() {

    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getTimezoneIdStr() {
        return "Asia/" + timezoneIdStr;
    }

    public void setTimezoneIdStr(String timezoneIdStr) {
        this.timezoneIdStr = timezoneIdStr;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isDaylightSaving() {
        return daylightSaving;
    }

    public void setDaylightSaving(boolean daylightSaving) {
        this.daylightSaving = daylightSaving;
    }

    /** 是否夏令时（未来可能用） */
    private boolean daylightSaving = false;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
