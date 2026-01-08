package com.taodev.zhouyi.calendar;

import android.icu.util.Calendar;

import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.fourpillars.analysis.FourPillarsAnalysisService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LuckPillarCalculator {

    private BaziRules rules;
    private BaziAttributesCalculator attributesCalculator;
    private TenGodCalculator tenGodCalculator;

    public LuckPillarCalculator(BaziRules rules,
                                BaziAttributesCalculator attributesCalculator,
                                TenGodCalculator tenGodCalculator) {
        this.rules = rules;
        this.attributesCalculator = attributesCalculator;
        this.tenGodCalculator = tenGodCalculator;
    }

    // =========================================================================
    // 计算大运
    // =========================================================================
   public List<LuckPillar> calculateLuckPillars(
           FourPillarsInput input,
           Pillar yearPillar,
           Pillar monthPillar,
           boolean isMale,
           String dayMaster,
           List<Date> jieQiMap){
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
       // 起始岁数占位，具体需要结合节气计算
       // 虚岁
       Date birthTimeUtc = TimeConverter.convertToUtc(input);

       StartAgeInfo ageInfo = calculatePreciseStartAge(birthTimeUtc, isForward, jieQiMap);
       // 使用 Calendar 实例
       Calendar cal = Calendar.getInstance();
       cal.setTime(ageInfo.getTransitionDate());
       // 安全地获取年份
       int baseStartYear = cal.get(Calendar.YEAR);
       for (int i = 0; i < 10; i++) { // 从 1 开始，因为大运从月柱的下一个开始
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
           // 生成大运的干支柱子

           // 计算这步大运的起始年 (2011, 2021...)
           int currentStartYear = baseStartYear + (i * 10);

           //每个大运起始岁数  虚岁
           int startNominalAge = currentStartYear -input.getLocalDateTime().getYear() +1;

           // 计算大运天干的十神
           String tenGod = tenGodCalculator.calculate(dayMaster, nextStem);
           // 计算大运长生（衰旺）
           String luckLifeStage = attributesCalculator.calculateTwelveLongevities(dayMaster, nextBranch);
           LuckPillar luck = new LuckPillar(tenGod, nextStem, nextBranch, luckLifeStage, ageInfo.getAge(),ageInfo.getDesc(),startNominalAge, currentStartYear,ageInfo.getTransitionDate());
           //计算当前大运未来十年的干支
           int birthYear = input.getLocalDateTime().getYear();
           List<String> yearlyLuckList = new ArrayList<>();
           for(int k = 0; k < 10; k++){
               //从当前大运起始年开始算
               int currentYear = currentStartYear + k;
               // 用公历年份查干支表；公元4年是甲子年
               int offset = currentYear - 4;
               int index = offset % 60;
               if (index < 0) index += 60;
               yearlyLuckList.add(rules.getSexagenaryCycle().get(index));
           }
           luck.setYearlyLuckList(yearlyLuckList);

           // 计算大运的纳音
           // String tenGod = tenGodCalculator.calculate(yearPillar.getStem(), nextStem); // 这里的参照点看需求

           luckList.add(luck);
       }

       return luckList;
   }
    private StartAgeInfo calculatePreciseStartAge(Date birthTimeUtc, boolean isForward, List<Date> jieQiMap) {
        Date targetJieQi;

        // 策略：
        // 顺排 -> 找出生后的下一个"节" (注意是节，不是气，不过通常混用，需根据具体流派)
        // 逆排 -> 找出生前的上一个"节"
        if (isForward) {
            targetJieQi = findNextJieQi(birthTimeUtc, jieQiMap);
        } else {
            targetJieQi = findPrevJieQi(birthTimeUtc, jieQiMap);
        }

        // 兜底：如果找不到节气数据，默认1岁
        if (targetJieQi == null){
            // 1. 计算兜底的交运时间：直接在出生时间上 + 1年
            Calendar c = Calendar.getInstance();
            c.setTime(birthTimeUtc);
            // 年份 + 1
            c.add(Calendar.YEAR, 1);
            Date fallbackStartTime = c.getTime();
            return new StartAgeInfo(1, "1岁0个月0天",fallbackStartTime);
        }

        // 1. 获取毫秒差值
        long diffMillis = Math.abs(targetJieQi.getTime() - birthTimeUtc.getTime());

//        // ★★★ 计算具体的交运日期 ★★★
//        // 原理：真实时间差 * 120 = 大运推迟的时间
//        // 直接用毫秒算精度更高，不用转分钟
//        long luckAddMillis = diffMillis * 120;
//
//        // 出生时间 + 推迟时间 = 交运时间



        // 2. 转换为实际天数 (用 double 保持精度)
        double totalRealDays = diffMillis / (1000.0 * 60 * 60 * 24);

        // 3. 核心折算逻辑 (命理时间)
        // 3天 = 1年
        int years = (int) (totalRealDays / 3);

        // 取余数天数，继续折算
        double remainRealDays = totalRealDays % 3;

        // 1天 = 4个月 (也就是 3天=12个月)
        double totalLuckMonths = remainRealDays * 4;
        int months = (int) totalLuckMonths;

        // 取余数月数，继续折算天
        double remainLuckMonths = totalLuckMonths - months;

        // 1个月 = 30天 (命理通常按30天一月计算，或者说 1小时=5天)
        int days = (int) (remainLuckMonths * 30);

        Calendar cal = Calendar.getInstance();
        cal.setTime(birthTimeUtc);
        cal.add(Calendar.YEAR, years);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DAY_OF_MONTH, days);
        Date jiaoYunTimestamp = cal.getTime();
        // 4. 格式化输出
        // 细节优化：如果算出来是 0岁，通常习惯显示 "出生后X个月起运" 或者 "0岁..."
        // 如果算出来 months 是 12 (极少情况由于精度问题)，应该进位
        if (months >= 12) {
            years += 1;
            months = 0;
        }

        // 最终岁数 = 虚岁还是周岁
        // 命理中通常是指“实岁”或者“加1之后的岁数”，这里我们按“距离出生后X年”计算
        // 实际排盘时，通常会把这个 years 加上出生年份。
        // 为了防止计算出 0岁，有人习惯 +1，这里按纯数学差值返回，由 UI 决定是否 +1
        int finalAge = (years < 1) ? 1 : years;

        String desc = finalAge + "岁" + months + "个月" + days + "天";

        return new StartAgeInfo(finalAge, desc, jiaoYunTimestamp);
    }
    /**
     * 【私有辅助】计算起运岁数 (核心算法：3天=1年)
     */

    public static class StartAgeInfo {
        private int age;           // 整岁 (用于推算年份)
        private int nominalAge;
        private String desc;       // 描述文本 (如 "4岁2个月5天")
        private Date startLuckTime; // 精确的起运时刻 (可选，用于高级排盘)
        private Date transitionDate; // 精确的交运时刻 (可选，用于高级排盘)
        public int getNominalAge() {
            return nominalAge;
        }

        public void setNominalAge(int nominalAge) {
            this.nominalAge = nominalAge;
        }

        public Date getTransitionDate() {
            return transitionDate;
        }

        public void setTransitionDate(Date transitionDate) {
            this.transitionDate = transitionDate;
        }

        public int getAge() {
            return age;
        }

        public String getDesc() {
            return desc;
        }

        public StartAgeInfo(int age, String desc,Date transitionDate) {
            this.age = age;
            this.desc = desc;
            this.transitionDate = transitionDate;
        }
    }
    /**
     * 顺查：在时间轴里找出生后的【下一个】节气
     */
    private Date findNextJieQi(Date birthTime, List<Date> jieQiTimeLine) {
        // 遍历时间轴
        for (Date jieQiDate : jieQiTimeLine) {
            // 因为 List 已经按时间升序排列 (Collections.sort)
            // 所以只要找到第一个比生日【晚】的时间，就是它！
            if (jieQiDate.after(birthTime)) {
                return jieQiDate;
            }
        }
        return null; // 没找到（可能是列表数据不够长，没覆盖到后面）
    }
    /**
     * 逆查：在时间轴里找出生前的【上一个】节气
     */
    private Date findPrevJieQi(Date birthTime, List<Date> jieQiTimeLine) {
        // ★ 技巧：倒着遍历效率最高
        // 从列表最后一天往前找
        for (int i = jieQiTimeLine.size() - 1; i >= 0; i--) {
            Date jieQiDate = jieQiTimeLine.get(i);

            // 找到第一个比生日【早】的时间，就是它！
            if (jieQiDate.before(birthTime)) {
                return jieQiDate;
            }
        }
        return null; // 没找到（可能是列表数据不够长，没覆盖到前面）
    }
}
