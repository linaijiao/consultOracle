package com.taodev.zhouyi.domain;

import com.google.gson.annotations.SerializedName;

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

    // 阴阳 (JSON: "甲": "阳")
    private Map<String,String> yinYang;
    // 五行 (JSON: nested object)
    private FiveElements fiveElements;

    // 强弱与长生 (JSON: nested object)
    private StrengthAnalysis strengthAnalysis;

    //纳音
    @SerializedName("naYin")
    private Map<String, String> naYin;

    public Map<String, String> getNaYin() {
        return naYin;
    }

    public void setNaYin(Map<String, String> naYin) {
        this.naYin = naYin;
    }

    public Map<String, String> getYinYang() {
        return yinYang;
    }

    public void setYinYang(Map<String, String> yinYang) {
        this.yinYang = yinYang;
    }


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
    public FiveElements getFiveElements() { return fiveElements; }
    public List<String> getSexagenaryCycle() { return sexagenaryCycle; }
    public void setSexagenaryCycle(List<String> sexagenaryCycle) { this.sexagenaryCycle = sexagenaryCycle; }
    public Map<String, List<String>> getHiddenStems() { return hiddenStems; }
    public void setHiddenStems(Map<String, List<String>> hiddenStems) { this.hiddenStems = hiddenStems; }
    @SerializedName("tenGods")
    private TenGods tenGods;

    public TenGods getTenGods() { return tenGods; }

    // --- 新的内部类 ---

    public static class TenGods {
        // Key是十神名字("比肩"), Value是规则条件
        @SerializedName("relations")
        private Map<String, RelationRule> relations;

        public Map<String, RelationRule> getRelations() { return relations; }
    }

    public static class RelationRule {
        // 使用 Boolean 允许为 null (代表不关心此条件)
        private Boolean sameStem;     // 是否同天干
        private Boolean sameElement;  // 是否同五行
        private Boolean sameYinYang;  // 是否同阴阳

        // 下面这几个看您JSON完整版里有没有，通常会有：
        private Boolean generates;    // 是否是我生
        private Boolean generatedBy;  // 是否是生我
        private Boolean controls;    // 是否是我克 (controls)
        private Boolean restricts;
        private Boolean restrictedBy; // 是否是克我

        // Getters ...
        public Boolean getControls() {
            return controls;
        }

        public Boolean getRestrictedBy() {
            return restrictedBy;
        }
        public Boolean getSameStem() { return sameStem; }
        public Boolean getSameElement() { return sameElement; }
        public Boolean getSameYinYang() { return sameYinYang; }
        public Boolean getGenerates() { return generates; }
        public Boolean getRestricts() { return restricts; } // 对应 JSON 的 "controls" ?
        public Boolean getGeneratedBy() { return generatedBy; }
    }
    // 内部类定义 五行嵌套
    public static class FiveElements {
        @SerializedName("heavenlyStems")
        private Map<String, String> heavenlyStems;

        private Map<String, String> earthlyBranches;

        public Map<String, String> getHeavenlyStems() {
            return heavenlyStems;
        }

        public void setHeavenlyStems(Map<String, String> heavenlyStems) {
            this.heavenlyStems = heavenlyStems;
        }

        public Map<String, String> getEarthlyBranches() {
            return earthlyBranches;
        }

        public void setEarthlyBranches(Map<String, String> earthlyBranches) {
            this.earthlyBranches = earthlyBranches;
        }


    }
    // 内部类定义 五行嵌套
    public static class StrengthAnalysis {
        // "长生": ["亥", "午"...] (按天干顺序排列)
        private Map<String, List<String>> branchStrength;

        public Map<String, List<String>> getBranchStrength() {
            return branchStrength;
        }

        public void setBranchStrength(Map<String, List<String>> branchStrength) {
            this.branchStrength = branchStrength;
        }

    }


    public StrengthAnalysis getStrengthAnalysis() { return strengthAnalysis;}
}

