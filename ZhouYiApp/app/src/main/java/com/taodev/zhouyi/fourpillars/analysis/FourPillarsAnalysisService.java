package com.taodev.zhouyi.fourpillars.analysis;

import com.taodev.zhouyi.calendar.BaziAttributesCalculator;
import com.taodev.zhouyi.calendar.TenGodCalculator;
import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.engine.IFourPillarsAnalysisService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 八字排盘核心分析服务
 * 职责：协调各个计算器，组装完整的排盘结果
 */
public class FourPillarsAnalysisService implements IFourPillarsAnalysisService {
    private final BaziRules rules;
    private final TenGodCalculator tenGodCalculator;
    private final BaziAttributesCalculator attributesCalculator;

    public FourPillarsAnalysisService(BaziRules rules) {
        this.rules = rules;
        // 初始化计算器工具
        this.tenGodCalculator = new TenGodCalculator(rules);
        this.attributesCalculator = new BaziAttributesCalculator(rules);
    }
    /**
     * 【总入口】 执行完整的排盘分析
     */
    @Override
    public void analyze(FourPillarsResult result) {
        // 1. 拿到基础四柱 (假设外面已经填好了 stem/branch)
        Pillar yearP = result.getYearPillar();
        Pillar monthP = result.getMonthPillar();
        Pillar dayP = result.getDayPillar();
        Pillar hourP = result.getHourPillar();

        String dayMaster = dayP.getStem();

        // 2. 算属性 (十神、藏干、纳音、空亡)
        // (调用内部的私有方法或者工具类)
        fillAttributes(yearP, dayMaster);
        fillAttributes(monthP, dayMaster);
        fillAttributes(dayP, dayMaster);
        fillAttributes(hourP, dayMaster);

        // 3. 将柱子放入结果
        result.setYearPillar(yearP);
        result.setMonthPillar(monthP);
        result.setDayPillar(dayP);
        result.setHourPillar(hourP);

        // 3. 计算大运 (使用新逻辑)
        boolean isMale = "乾造".equals(result.getGender().getLabel());

        // 从 Result 里取出之前算好的起运岁数 (例如 4)
        int startAge = result.getStartAge();

        // 调用带 startAge
        List<LuckPillar> lucks = calculateLuckPillars(yearP, monthP, isMale, startAge);
        result.setLuckPillars(lucks);

        //4. (可选) 身强身弱
         String strength = analyzeBodyStrength(new Pillar[]{yearP, monthP, dayP, hourP});
         result.setBodyStrength(strength);

    }
    /**
     * 辅助方法：填充单根柱子的所有详细属性
     * @param pillar    需要填充的柱子 (例如年柱)
     * @param dayMaster 日主 (作为计算十神和长生的基准)
     */
    private void fillAttributes(Pillar pillar, String dayMaster) {
        // 0. 防御性检查
        if (pillar == null) return;

        String stem = pillar.getStem();   // 该柱的天干
        String branch = pillar.getBranch(); // 该柱的地支

        // --- 1. 计算天干十神 (Stem Ten God) ---
        // 逻辑：日主 vs 该柱天干 (例如：甲 vs 丙 -> 食神)
        String stemTenGod = tenGodCalculator.calculate(dayMaster, stem);
        pillar.setStemTenGod(stemTenGod);

        // --- 2. 计算十二长生 (Life Stage) ---
        // 逻辑：日主 vs 该柱地支 (例如：甲 vs 寅 -> 临官)
        String lifeStage = attributesCalculator.calculateTwelveLongevities(dayMaster, branch);
        pillar.setLifeStage(lifeStage);

        // --- 3. 计算纳音 (Na Yin) ---
        // 逻辑：干支组合查表 (例如：甲子 -> 海中金)
        String naYin = attributesCalculator.calculateNaYin(stem, branch);
        pillar.setNaYin(naYin);

        // --- 4. 计算空亡 (Void / Kong Wang) ---
        // 逻辑：根据干支计算所在旬的空亡地支 (例如：甲子旬 -> 戌亥空)
        String kongWang = attributesCalculator.calculateKongWang(stem, branch);
        pillar.setKongWang(kongWang);

        // --- 5. 计算地支藏干 & 藏干十神 (Hidden Stems) ---
        // A. 从规则中获取该地支藏了哪些天干 (例如：寅 -> ["甲", "丙", "戊"])
        List<String> hiddenStemNames = rules.getHiddenStems().get(branch);

        List<Pillar.HiddenStemInfo> hiddenStemInfos = new ArrayList<>();

        if (hiddenStemNames != null) {
            for (String hStem : hiddenStemNames) {
                // B. 对每一个藏干，计算它是日主的什么十神
                String hTenGod = tenGodCalculator.calculate(dayMaster, hStem);

                // C. 打包成 Info 对象 (存：藏干名 + 十神名)
                Pillar.HiddenStemInfo info = new Pillar.HiddenStemInfo(hStem, hTenGod);
                hiddenStemInfos.add(info);
            }
        }

        // D. 将算好的藏干列表存回柱子
        pillar.setHiddenStemInfos(hiddenStemInfos);
    }
    // =========================================================================
    // 1. 实现：计算大运 (核心逻辑)
    // =========================================================================
    @Override
    public List<LuckPillar> calculateLuckPillars(Pillar yearPillar, Pillar monthPillar, boolean isMale, int startAge) {
        List<LuckPillar> luckList = new ArrayList<>();
        // 1.1 从 Rules 里提取有序的天干和地支列表
        // 注意：new ArrayList(...) 会按照 Map 的 Key 顺序生成 List
        List<String> stems = new ArrayList<>(rules.getFiveElements().getHeavenlyStems().keySet());
        List<String> branches = new ArrayList<>(rules.getFiveElements().getEarthlyBranches().keySet());

        // 1.2 判断年干阴阳
        String yearStem = yearPillar.getStem();
        // 假设 rules.getYinYang() 返回 {"甲":"阳", "乙":"阴"...}
        String yearPolarity = rules.getYinYang().get(yearStem);

        // 1.3 确定顺排还是逆排
        // 阳男阴女 -> 顺排 (Forward)
        // 阴男阳女 -> 逆排 (Backward)
        boolean isYang = "阳".equals(yearPolarity);
        boolean isForward;

        if (isMale) {
            isForward = isYang; // 男：阳顺阴逆
        } else {
            isForward = !isYang; // 女：阴顺阳逆
        }

        // 1.4 找到月柱在 60 甲子中的位置，作为起点
        String startStem = monthPillar.getStem();
        String startBranch = monthPillar.getBranch();

        stems = new ArrayList<>(rules.getFiveElements().getHeavenlyStems().keySet());
        branches = new ArrayList<>(rules.getFiveElements().getEarthlyBranches().keySet());

        int stemIndex = stems.indexOf(startStem);
        int branchIndex = branches.indexOf(startBranch);
        // 1.4 循环推算 8 步大运 (通常排 8 步，或者 10 步)
        // 注意：大运是不算起运岁数的，起运岁数(比如 4岁上运)需要另外的算法，这里先默认每10年一运
        int currentAge = 0; // 起始岁数占位，具体需要结合节气计算

        for (int i = 1; i <= 8; i++) { // 从 1 开始，因为大运从月柱的下一个开始
            // 移动一步
            if (isForward) {
                stemIndex = (stemIndex + 1) % 10;
                branchIndex = (branchIndex + 1) % 12;
            } else {
                stemIndex = (stemIndex - 1 + 10) % 10;
                branchIndex = (branchIndex - 1 + 12) % 12;
            }

            String nextStem = stems.get(stemIndex);
            String nextBranch = branches.get(branchIndex);
            // 简单模拟起运时间：假设第一步运从 10 岁开始 (实际需精确计算)
            // 这里仅仅是生成大运的干支柱子
            int startYear = i * 10;

            LuckPillar luck = new LuckPillar(nextStem, nextBranch, startAge, startYear);

            // 计算大运的纳音
            // String tenGod = tenGodCalculator.calculate(yearPillar.getStem(), nextStem); // 这里的参照点看需求

            luckList.add(luck);
        }

        return luckList;
    }
    // 分析身旺身弱
    @Override
    public String analyzeBodyStrength(Pillar[] pillars) {
        if (pillars == null || pillars.length < 3) return "未知";

        Pillar dayPillar = pillars[2];
        Pillar monthPillar = pillars[1];

        String dayMaster = dayPillar.getStem();
        String monthBranch = monthPillar.getBranch();

        String lifeStage = attributesCalculator.calculateTwelveLongevities(dayMaster, monthBranch);

        if ("帝旺".equals(lifeStage) || "临官".equals(lifeStage) ||
                "长生".equals(lifeStage) || "冠带".equals(lifeStage)) {
            return "身旺";
        } else if ("衰".equals(lifeStage) || "病".equals(lifeStage) ||
                "死".equals(lifeStage) || "绝".equals(lifeStage)) {
            return "身弱";
        } else {
            return "中和";
        }
    }
    // 计算十神统计
    @Override
    public Map<String, Integer> calculateTenGods(Pillar dayPillar, Pillar[] allPillars) {
            Map<String, Integer> stats = new HashMap<>();

            // 1. 拿到日主（比如“甲”），这是比较的基准
            String dayMaster = dayPillar.getStem();

            // 2. 遍历四根柱子
            for (Pillar pillar : allPillars) {
                if (pillar == null) continue;

                // --- A. 统计天干的十神 ---
                // 注意：通常不统计日主自己（日主看自己是比肩，但在统计图中通常忽略或视为自己）
                if (pillar != dayPillar) {
                    // 拿着工具(tenGodCalculator)去算：日主 vs 当前柱天干
                    String stemTenGod = tenGodCalculator.calculate(dayMaster, pillar.getStem());

                    // 放入统计 Map：如果没有就记1，有就+1
                    if (stemTenGod != null && !stemTenGod.equals("未知")) {
                        stats.put(stemTenGod, stats.getOrDefault(stemTenGod, 0) + 1);
                    }
                }

                // --- B. 统计地支藏干的十神 ---
                // 从规则里查出这个地支藏了哪些天干
                List<String> hiddenStems = rules.getHiddenStems().get(pillar.getBranch());

                if (hiddenStems != null) {
                    for (String hiddenStem : hiddenStems) {
                        // 再次拿着工具去算：日主 vs 藏干
                        String hiddenTenGod = tenGodCalculator.calculate(dayMaster, hiddenStem);

                        // 放入统计 Map
                        if (hiddenTenGod != null && !hiddenTenGod.equals("未知")) {
                            stats.put(hiddenTenGod, stats.getOrDefault(hiddenTenGod, 0) + 1);
                        }
                    }
                }
            }

            return stats;

    }
    // 计算纳音和空亡
    @Override
    public void calculateNaYinAndKongWang(FourPillarsResult result) {
    // 依次处理四柱
        fillNaYinAndKongWang(result.getYearPillar());
        fillNaYinAndKongWang(result.getMonthPillar());
        fillNaYinAndKongWang(result.getDayPillar());
        fillNaYinAndKongWang(result.getHourPillar());
        // 处理大运 (大运也有纳音和空亡)
        if (result.getLuckPillars() != null) {
            for (LuckPillar luck : result.getLuckPillars()) {
                // LuckPillar 继承自 Pillar 或有相应字段，这里假设它能存
                // luck.setNaYin(...)
            }
        }
    }
    // 辅助方法：填充单柱
    private void fillNaYinAndKongWang(Pillar pillar) {
        if (pillar == null) return;

        // 1. 算纳音
        String naYin = attributesCalculator.calculateNaYin(pillar.getStem(), pillar.getBranch());
        pillar.setNaYin(naYin);

        // 2. 算空亡 (该柱所属的旬，对应的空亡字)
        // 例如：日柱是 甲子，算出来的空亡是 "戌亥"。这意味着如果年支是戌，就是年支空亡。
        String kongWang = attributesCalculator.calculateKongWang(pillar.getStem(), pillar.getBranch());
        pillar.setKongWang(kongWang);
    }


}
