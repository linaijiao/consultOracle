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

    public FourPillarsInput(String name,int year, int month, int day, int hour,int minute ,Gender gender,String city) {
        this.name = name;
        this.localDateTime = LocalDateTime.of(year, month, day, hour, minute);
        this.gender=gender;
        this.city = city;

    }
    public FourPillarsInput() {

    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getTimezoneIdStr(String city) {
        String cityArrayName = city;
        if (cityArrayName == null || !cityArrayName.startsWith("cities_")) {
            return "Asia/" + timezoneIdStr;
        }
        // 提取 "shanghai"（去掉前缀 "cities_"）
        String cityPinyin = city.substring("cities_".length());

        // 首字母大写 → "Shanghai"
        if (!cityPinyin.isEmpty()) {
            cityPinyin = cityPinyin.substring(0, 1).toUpperCase() +
                    (cityPinyin.length() > 1 ? cityPinyin.substring(1) : "");
        }

        // 特殊处理：只有上海/北京时间是中国大陆标准时区，其他省份其实也都是同一个时区
        // 所以这里其实所有情况都可以返回 "Shanghai"，但如果你想保持逻辑一致，可以返回处理后的
        return "Asia/" + cityPinyin; // 返回 "Shanghai"、"Beijing" 等（实际都会映射到 Asia/Shanghai）
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
