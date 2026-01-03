package com.taodev.zhouyi.domain;

import java.util.List;
import java.util.Map;

/**
 * 对应 bazi_rules.json 的数据结构
 */
public class BaziRules {
    //一甲子，60年循环
    // 对应 JSON 中的 "sexagenaryCycle": ["甲子", "乙丑"...]
    private List<String> sexagenaryCycle;
    // 五虎遁，由年干决定一年月干的起始
    private Map<String,String> monthStartRule;
    // 五鼠遁，由日干决定时干的起始
    private Map<String,String> hourStartRule;


    // 存 ["立春", "惊蛰"...]
    private List<String> monthValidator;
    // 存 ["寅", "卯"...]
    private List<String> monthBranches;
    // 10天干
    private List<String> heavenlyStems;
    // 12地支
    private List<String> earthlyBranches;

    // 对应 JSON 中的 "hiddenStems": { "子": ["癸"], ... }
    // Key是地支，Value是藏干列表
    private Map<String, List<String>> hiddenStems;



    // Getter 和 Setter 方法
    public Map<String, String> getMonthStartRule() {
        return monthStartRule;
    }

    public void setMonthStartRule(Map<String, String> monthStartRule) {
        this.monthStartRule = monthStartRule;
    }
    public List<String> getMonthValidator() {
        return monthValidator;
    }

    public void setMonthValidator(List<String> monthValidator) {
        this.monthValidator = monthValidator;
    }

    public List<String> getMonthBranches() {
        return monthBranches;
    }

    public void setMonthBranches(List<String> monthBranches) {
        this.monthBranches = monthBranches;
    }

    public Map<String, String> getHourStartRule() {
        return hourStartRule;
    }

    public void setHourStartRule(Map<String, String> hourStartRule) {
        this.hourStartRule = hourStartRule;
    }
    public List<String> getHeavenlyStems() {
        return heavenlyStems;
    }

    public void setHeavenlyStems(List<String> heavenlyStems) {
        this.heavenlyStems = heavenlyStems;
    }

    public List<String> getEarthlyBranches() {
        return earthlyBranches;
    }

    public void setEarthlyBranches(List<String> earthlyBranches) {
        this.earthlyBranches = earthlyBranches;
    }
    public List<String> getSexagenaryCycle() { return sexagenaryCycle; }
    public void setSexagenaryCycle(List<String> sexagenaryCycle) { this.sexagenaryCycle = sexagenaryCycle; }
    public Map<String, List<String>> getHiddenStems() { return hiddenStems; }
    public void setHiddenStems(Map<String, List<String>> hiddenStems) { this.hiddenStems = hiddenStems; }
}
