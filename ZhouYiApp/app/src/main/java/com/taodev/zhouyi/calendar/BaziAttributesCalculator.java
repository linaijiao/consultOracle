package com.taodev.zhouyi.calendar;

import com.taodev.zhouyi.domain.BaziRules;

import java.util.List;
import java.util.Map;

/**
 * 八字属性计算器
 * 职责：计算十二长生、纳音、空亡等属性
 */
public class BaziAttributesCalculator {
    private final BaziRules rules;

    public BaziAttributesCalculator(BaziRules rules) {
        this.rules = rules;
    }

    /**
     * 计算十二长生状态
     * @param dayMaster 日干 (例如 "甲")
     * @param branch    地支 (例如 "亥")
     * @return 状态名称 (例如 "长生")
     */
    public String calculateTwelveLongevities(String dayMaster, String branch) {
        // 1. 获取天干列表，找到日主的索引 (甲=0, 乙=1...)
        List<String> stems = rules.getHeavenlyStems();
        int stemIndex = stems.indexOf(dayMaster);

        // 防御性检查：如果天干不对，或者地支为空
        if (stemIndex == -1 || branch == null) {
            return "未知";
        }

        // 2. 获取长生规则表
        // JSON 结构: "长生": ["亥", "午"...]
        Map<String, List<String>> strengthMap = rules.getStrengthAnalysis().getBranchStrength();

        // 3. 遍历 Map 寻找匹配项
        for (Map.Entry<String, List<String>> entry : strengthMap.entrySet()) {
            // 状态名 (如 "长生")
            String stateName = entry.getKey();
            List<String> branchList = entry.getValue(); // 对应的地支列表

            // 确保列表长度足够 (防止 JSON 数据缺失)
            if (branchList != null && branchList.size() > stemIndex) {
                // 取出该日主对应的地支
                String targetBranch = branchList.get(stemIndex);

                // 比对：如果匹配，就是这个状态！
                if (targetBranch.equals(branch)) {
                    return stateName;
                }
            }
        }

        return "未知";
    }
}
