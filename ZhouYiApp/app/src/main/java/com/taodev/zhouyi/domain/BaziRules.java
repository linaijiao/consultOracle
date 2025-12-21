package com.taodev.zhouyi.domain;

import java.util.List;
import java.util.Map;

/**
 * 对应 bazi_rules.json 的数据结构
 */
public class BaziRules {
    // 对应 JSON 中的 "sexagenaryCycle": ["甲子", "乙丑"...]
    private List<String> sexagenaryCycle;

    // 对应 JSON 中的 "hiddenStems": { "子": ["癸"], ... }
    // Key是地支，Value是藏干列表
    private Map<String, List<String>> hiddenStems;

    // Getter 和 Setter 方法
    public List<String> getSexagenaryCycle() { return sexagenaryCycle; }
    public void setSexagenaryCycle(List<String> sexagenaryCycle) { this.sexagenaryCycle = sexagenaryCycle; }

    public Map<String, List<String>> getHiddenStems() { return hiddenStems; }
    public void setHiddenStems(Map<String, List<String>> hiddenStems) { this.hiddenStems = hiddenStems; }
}
