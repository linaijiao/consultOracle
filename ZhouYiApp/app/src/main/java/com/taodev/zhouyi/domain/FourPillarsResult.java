package com.taodev.zhouyi.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 四柱八字命盘结果数据模型
 * 封装完整的八字计算结果，包括四柱干支、大运排盘、五行分析和十神关系等核心命理信息
 * 所有时间相关字段均采用UTC标准时间，确保跨时区计算一致性
 */
public class FourPillarsResult {
    // ==========================================
    // 原始输入信息 (用于 UI 回显，给用户看)
    // ==========================================
    private int inputYear;
    private int inputMonth;
    private int inputDay;
    private int inputHour;

    /** 出生日期 */
    private LocalDateTime localDateTime;

    /** 出生日期（UTC标准时间） */
    private Date birthDate;
    /** 年柱干支信息 */
    private Pillar yearPillar;
    /** 月柱干支信息 */
    private Pillar monthPillar;
    /** 日柱干支信息 */
    private Pillar dayPillar;
    /** 时柱干支信息 */
    private Pillar hourPillar;
    /** 大运排盘列表（按时间顺序排列） */
    private List<LuckPillar> luckPillars;

    /** 五行旺衰分析结果 */
    private String strengthAnalysis;
    /** 十神关系统计（以日主为中心） */
    private Map<String, Integer> tenGods;
    /**性別 乾造 坤造 */
    private Gender gender;

    /**起运年龄  */
    private int startAge;
    /**身强身弱  */
    private String bodyStrength;

    public String getBodyStrength() {
        return bodyStrength;
    }

    public void setBodyStrength(String bodyStrength) {
        this.bodyStrength = bodyStrength;
    }

    public int getStartAge() {
        return startAge;
    }
    public void setStartAge(int startAge) {
        this.startAge = startAge;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public void setYearPillar(Pillar yearPillar) {
        this.yearPillar = yearPillar;
    }

    public void setMonthPillar(Pillar monthPillar) {
        this.monthPillar = monthPillar;
    }

    public void setDayPillar(Pillar dayPillar) {
        this.dayPillar = dayPillar;
    }

    public void setHourPillar(Pillar hourPillar) {
        this.hourPillar = hourPillar;
    }

    public void setLuckPillars(List<LuckPillar> luckPillars) {
        this.luckPillars = luckPillars;
    }

    public void setStrengthAnalysis(String strengthAnalysis) {
        this.strengthAnalysis = strengthAnalysis;
    }

    public void setTenGods(Map<String, Integer> tenGods) {
        this.tenGods = tenGods;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Pillar getYearPillar() {
        return yearPillar;
    }

    public Pillar getMonthPillar() {
        return monthPillar;
    }

    public Pillar getDayPillar() {
        return dayPillar;
    }

    public Pillar getHourPillar() {
        return hourPillar;
    }

    public List<LuckPillar> getLuckPillars() {
        return luckPillars;
    }

    public String getStrengthAnalysis() {
        return strengthAnalysis;
    }

    public Map<String, Integer> getTenGods() {
        return tenGods;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
