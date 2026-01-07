package com.taodev.zhouyi.engine;

import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.domain.LuckPillar;
import java.util.List;
import java.util.Map;

public interface IFourPillarsAnalysisService {
    /** 总入口 */
     void analyze(FourPillarsResult result);

    /** 分析身旺身弱 */
    String analyzeBodyStrength(Pillar[] pillars);  // 返回 "身旺"、"身弱"、"中和"

    /** 计算十神统计 */
    Map<String, Integer> calculateTenGods(Pillar dayPillar, Pillar[] allPillars);
    /** 计算纳音和空亡 */
    void calculateNaYinAndKongWang(FourPillarsResult result);
}