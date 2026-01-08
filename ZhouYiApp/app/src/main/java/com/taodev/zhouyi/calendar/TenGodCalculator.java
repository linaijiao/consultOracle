package com.taodev.zhouyi.calendar;

import android.util.Log;

import com.taodev.zhouyi.core.repository.BaziDataLoader;
import com.taodev.zhouyi.domain.BaziRules;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TenGodCalculator {
    private final BaziRules rules;
    // 1. 定义天干的五行
    // 构造函数：给规则数据
    public TenGodCalculator(BaziRules rules) {
        this.rules = rules;
    }
    /**
     * 计算十神
     * @param dayMaster 日主 (日干)
     * @param target  目标天干
     * @return 十神名称 (如 "比肩", "正官")
     */
    public String calculate(String dayMaster, String target) {

        if (dayMaster.equals(target)) return "比肩";

        // 1. 获取五行
        // 1. 获取五行 (现在是中文 "木", "火"...)
        String meElem = rules.getFiveElements().getHeavenlyStems().get(dayMaster);
        String otherElem = rules.getFiveElements().getHeavenlyStems().get(target);

        // 2. 获取阴阳 (现在是中文 "阳", "阴")
        String meYinYang = rules.getYinYang().get(dayMaster);
        String otherYinYang = rules.getYinYang().get(target);

        if (meElem == null || otherElem == null) {
            Log.e("TenGodCalc", "出现未知十神！ dayMaster=" + dayMaster + ", target=" + target
                    + " | meElem=" + meElem + ", otherElem=" + otherElem);
            return "未知";
        }


        // 2. 计算出当前两个字的所有状态 (Current State)
        // -------------------------------------------------
        boolean isSameStem = Objects.equals(dayMaster,target);
        boolean isSameElement = meElem.equals(otherElem);
        boolean isSameYinYang = meYinYang.equals(otherYinYang);

        // 获取生克关系 (0:同, 1:我生, 2:我克, 3:克我, 4:生我)
        // 这里复用之前的逻辑，或者写个简化版只返回 boolean
        int relationCode = getRelation(meElem, otherElem);

        boolean isGenerates = (relationCode == 1);     // 我生
        boolean isControls = (relationCode == 2);      // 我克 → 财
        boolean isRestrictedBy = (relationCode == 3);  // 克我
        boolean isGeneratedBy = (relationCode == 4);   // 生我

        // 3. 核心：遍历 JSON 规则寻找匹配项 (Pattern Matching)
        // -------------------------------------------------
        Map<String, BaziRules.RelationRule> allRules = rules.getTenGods().getRelations();
        Log.d("TenGodCalc", "规则表大小: " + (allRules != null ? allRules.size() : "null"));

        for (Map.Entry<String, BaziRules.RelationRule> entry : allRules.entrySet()) {
            String tenGodName = entry.getKey();     // 例如 "比肩"
            BaziRules.RelationRule rule = entry.getValue(); // 规则对象

            // 调用匹配方法
            if (isMatch(rule, isSameStem, isSameElement, isSameYinYang,
                    isGenerates, isControls, isGeneratedBy, isRestrictedBy)) {
                return tenGodName; // 找到了！直接返回 Key
            }
            Log.d("TenGodCalc", "规则: " + tenGodName +  " |relationCode=" + relationCode
                    + " sameStem=" + isSameStem+ " sameElement=" + isSameElement
                    + " sameYinYang=" + isSameYinYang );
        }
        Log.d("TenGodCalc", "规则: " + " |relationCode=" + relationCode
                + " sameStem=" + isSameStem+ " sameElement=" + isSameElement
                + " sameYinYang=" + isSameYinYang );
        Log.e("TenGodCalc", "出现未知十神！ dayMaster=" + dayMaster + ", target=" + target
                + " | meElem=" + meElem + ", otherElem=" + otherElem);
        Log.e("TenGodCalc", "所有规则未匹配，十神计算出现未知！ dayMaster=" + dayMaster + " target=" + target);
        return "未知";
    }
    /**
     * 匹配逻辑：只有当 JSON 里定义了该条件，且条件不符时，才算失败。
     * 如果 JSON 里 rule.getGenerates() 是 null，说明这条规则不关心生克，算通过。
     */
    private boolean isMatch(BaziRules.RelationRule rule,
                            boolean curSameStem, boolean curSameElement, boolean curSameYinYang,
                            boolean curGenerates, boolean curControls,
                            boolean curGeneratedBy, boolean curRestrictedBy) {

        // 1. 检查 sameStem
        if (rule.getSameStem() != null && rule.getSameStem() != curSameStem) return false;

        // 2. 检查 sameElement
        if (rule.getSameElement() != null && rule.getSameElement() != curSameElement) return false;

        // 3. 检查 sameYinYang
        if (rule.getSameYinYang() != null && rule.getSameYinYang() != curSameYinYang) return false;

        // 4. 检查 generates (我生 / 食伤)
        if (rule.getGenerates() != null && rule.getGenerates() != curGenerates) return false;

        // 5. 检查 restricts (我克 / 财) - 注意 JSON 里可能叫 "controls"
        if (rule.getControls() != null && rule.getControls() != curControls) return false;

        // ...以此类推检查 generatedBy, restrictedBy ...
        if (rule.getRestricts() != null && rule.getRestricts() != curRestrictedBy) return false;   // 克我 官杀
        if (rule.getGeneratedBy() != null && rule.getGeneratedBy() != curGeneratedBy) return false; //生我 印
        // 如果所有非 null 的条件都通过了
        return true;
    }

    // 辅助：判断五行关系
    private static int getRelation(String me, String other) {
        if (me.equals(other)) return 0;

        // 生：木火土金水木 (循环)
        if ((me.equals("木") && other.equals("火")) ||
                (me.equals("火") && other.equals("土")) ||
                (me.equals("土") && other.equals("金")) ||
                (me.equals("金") && other.equals("水")) ||
                (me.equals("水") && other.equals("木"))) return 1; // 我生

        if ((other.equals("木") && me.equals("火")) ||
                (other.equals("火") && me.equals("土")) ||
                (other.equals("土") && me.equals("金")) ||
                (other.equals("金") && me.equals("水")) ||
                (other.equals("水") && me.equals("木"))) return 4; // 生我

        // 克：木土水火金木 (隔一位)
        if ((me.equals("木") && other.equals("土")) ||
                (me.equals("土") && other.equals("水")) ||
                (me.equals("水") && other.equals("火")) ||
                (me.equals("火") && other.equals("金")) ||
                (me.equals("金") && other.equals("木"))) return 2; // 我克

        return 3; // 剩下的就是 克我
    }
}