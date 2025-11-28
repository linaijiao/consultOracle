package com.taodev.zhouyi.domain;

import java.time.LocalDateTime;

public class FourPillarsInput {
    /** 本地时间（用户填的生日+时辰） */
    private LocalDateTime localDateTime;

    /**
     * 时区（默认上海）用户选择的地点/时区
     * 对应前端的下拉框或城市输入
     * */
    private String timezoneIdStr = "Shanghai";

    /** 经度（真太阳时必备） */
    private double longitude = 116.4;  // 默认北京

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getTimezoneIdStr() {
        return timezoneIdStr;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
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

    /** 性别 */
    private Gender gender;

    /** 是否夏令时（未来可能用） */
    private boolean daylightSaving = false;

    // ================== 枚举 ==================
    public enum Gender {
        MALE, FEMALE
    }

}
