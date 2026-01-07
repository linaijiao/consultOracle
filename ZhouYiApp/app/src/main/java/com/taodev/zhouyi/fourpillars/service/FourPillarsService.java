package com.taodev.zhouyi.fourpillars.service;

import android.util.Log;

import com.taodev.zhouyi.calendar.BaziAttributesCalculator;
import com.taodev.zhouyi.calendar.LuckPillarCalculator;
import com.taodev.zhouyi.calendar.TenGodCalculator;
import com.taodev.zhouyi.calendar.TimeConverter;
import com.taodev.zhouyi.core.service.IFourPillarsService;
import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.Gender;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.engine.ICalendarService;
import com.taodev.zhouyi.engine.IFourPillarsAnalysisService;
import com.taodev.zhouyi.fourpillars.data.FourPillarsRepository;
import javax.inject.Inject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 四柱八字服务实现类
 * 实现IFourPillarsService接口定义的核心功能
 * 依赖日历服务进行时间转换和节气计算，依赖分析服务进行命理分析
 *
 * @author li bo
 * @since 2025-11-22
 */
public class FourPillarsService implements IFourPillarsService {
    private static final String TAG = "FourPillarsService";
    // 日历服务：提供时间转换、节气计算和干支查询功能
    private final ICalendarService calendarService;
    //大运
    private LuckPillarCalculator luckPillarCalculator;
    // 分析服务：提供命理分析算法
    private final IFourPillarsAnalysisService analysisService;
    // 数据仓库：负责计算结果的持久化存储与查询
    private final FourPillarsRepository repository;

    private final BaziRules baziRules;

    private final TenGodCalculator tenGodCalculator;
    private final BaziAttributesCalculator attributesCalculator;

    /**
     * 构造函数：通过依赖注入初始化服务组件
     *
     * @param calendarService 日历服务实例，提供时间和干支转换
     * @param analysisService 分析服务实例，提供命理算法实现
     * @param repository      数据仓库实例，提供结果存储能力
     */
    @Inject
    public FourPillarsService(ICalendarService calendarService,
                              IFourPillarsAnalysisService analysisService,
                              LuckPillarCalculator luckPillarCalculator,
                              FourPillarsRepository repository,
                              BaziRules baziRules) {
        this.calendarService = calendarService;
        this.analysisService = analysisService;
        this.luckPillarCalculator = luckPillarCalculator;
        this.repository = repository;
        this.baziRules = baziRules;
        this.tenGodCalculator = new TenGodCalculator(baziRules);
        this.attributesCalculator = new BaziAttributesCalculator(baziRules);
    }
     /**
       * 计算完整的四柱八字命盘
     * 包含核心流程：时间标准化→四柱计算→大运排盘→命理分析→结果组装
     * 所有时间计算基于真太阳时和UTC时间，确保跨时区准确性
     *
     * @param input 八字计算输入参数，包含出生日期、性别、时区等必要信息
     * @return 完整的八字命盘结果，包含四柱、大运、五行分析和十神关系
     * @throws IllegalArgumentException 当输入参数无效（如日期格式错误）时抛出
     */
    @Override
    public FourPillarsResult calculateFourPillars(FourPillarsInput input) {
        // 时间标准化：转换为UTC时间消除时区差异
        LocalDateTime inputDateTime = input.getLocalDateTime();
        LocalDateTime localDateTime = LocalDateTime.of(inputDateTime.getYear(), inputDateTime.getMonthValue(),
                inputDateTime.getDayOfMonth(), inputDateTime.getHour(), inputDateTime.getMinute());
        Instant instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();
        Date utcDate = Date.from(instant);
        Log.d(TAG, "Converted input to UTC date: " + utcDate);
        // 真太阳时校正：根据经度调整平太阳时，获取真实天文时间
        Date trueSolarTime = calendarService.getTrueSolarTime(input);

        // 四柱计算：基于真太阳时依次计算年柱、月柱、日柱、时柱
        Pillar yearPillar = calendarService.getYearPillar(input);
        Pillar monthPillar = calendarService.getMonthPillar(input);
        Pillar dayPillar = calendarService.getDayPillar(input);
        Pillar hourPillar = calendarService.getHourPillar(input);
        Log.d(TAG, "Calculated pillars - Year: " + yearPillar.getStem()
                + ", Month: " + monthPillar.getStem()
                + ", Day: " + dayPillar.getStem()
                + ", Hour: " + hourPillar.getStem());

        // 2. 算属性 (十神、藏干、纳音、空亡)
        String dayMaster = dayPillar.getStem();
        // (调用内部的私有方法或者工具类)
        fillAttributes(yearPillar, dayMaster);
        fillAttributes(monthPillar, dayMaster);
        fillAttributes(dayPillar, dayMaster);
        fillAttributes(hourPillar, dayMaster);

        // 大运排盘：根据性别和出生时间推算大运周期（每10年一步运）
        // 1. 准备时间轴
        List<Date> jieQiTimeLine = new ArrayList<>();

        // 获取年份
        int currentYear = input.getLocalDateTime().getYear();

        // 2. 加载数据并【过滤】
        // 我们只关心那 12 个节 (Jie)
        loadAndFilterJieQi(jieQiTimeLine, currentYear - 1);
        loadAndFilterJieQi(jieQiTimeLine, currentYear);
        loadAndFilterJieQi(jieQiTimeLine, currentYear + 1);

        // 3. 排序
        Collections.sort(jieQiTimeLine);

        // 4. 传给计算器
        List<LuckPillar> luckPillars = luckPillarCalculator.calculateLuckPillars(input,
                yearPillar, monthPillar,
                input.getGender() == Gender.FEMALE.MALE,
                dayMaster,jieQiTimeLine);

        // 五行旺衰分析：基于四柱干支组合分析五行力量分布
        String strengthAnalysis = analysisService.analyzeBodyStrength(
                new Pillar[]{yearPillar, monthPillar, dayPillar, hourPillar});

        // 十神关系计算：以日柱天干为日主，分析其他干支的十神属性
        Map<String, Integer> tenGods = analysisService.calculateTenGods(dayPillar, new Pillar[]{yearPillar, monthPillar, dayPillar, hourPillar});

        // 结果组装：整合所有计算结果为统一数据模型
        FourPillarsResult result = new FourPillarsResult();
        result.setBirthDate(utcDate);
        result.setLocalDateTime(inputDateTime);
        result.setYearPillar(yearPillar);
        result.setMonthPillar(monthPillar);
        result.setDayPillar(dayPillar);
        result.setGender(input.getGender());
        try {
            result.setHourPillar(hourPillar);
            result.setLuckPillars(luckPillars);
            result.setStrengthAnalysis(strengthAnalysis);
            result.setTenGods(tenGods);
            result.setBodyStrength(" ");
            System.out.printf("tenGod="+result.getTenGods());
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating four pillars", e);
            throw new RuntimeException("Calculation failed: " + e.getMessage(), e);
        }
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
        List<String> hiddenStemNames = baziRules.getHiddenStems().get(branch);

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
    /**
     * 保存八字计算结果到本地数据库
     * 采用仓库模式隔离数据访问逻辑，支持不同存储实现
     *
     * @param result 已计算完成的八字命盘结果
     * @return true表示保存成功，false表示保存失败（如数据已存在）
     */
    @Override
    public boolean saveFourPillarsResult(FourPillarsResult result) {
        if (result == null || result.getDayPillar() == null) {
            Log.e(TAG, "Cannot save invalid result");
            return false;
        }
        try {
            boolean saved = repository.save(result);
            Log.d(TAG, "Result saved successfully: " + saved);
            return saved;
        } catch (Exception e) {
            Log.e(TAG, "Error saving result", e);
            return false;
        }
    }

    /**
     * 根据出生日期查询历史计算结果
     * 用于避免重复计算和支持结果回溯功能
     *
     * @param birthDate 出生日期（精确到分钟）
     * @return 匹配的八字结果，若不存在则返回null
     */
    @Override
    public FourPillarsResult getHistoricalResult(Date birthDate) {
        Log.d(TAG, "Retrieving historical result for date: " + birthDate);
        if (birthDate == null) {
            Log.e(TAG, "Birth date parameter is null");
            return null;
        }
        try {
            // Convert legacy Date to modern LocalDateTime
            LocalDateTime birthDateTime = LocalDateTime.ofInstant(
                birthDate.toInstant(), ZoneId.systemDefault());
            Log.d(TAG, "Converted birth date to LocalDateTime: " + birthDateTime);
            return repository.getByBirthDate(birthDate);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving historical result", e);
            return null;
        }
    }
    /**
     * 私有辅助方法：读取数据、过滤12节、转换格式
     */
    private void loadAndFilterJieQi(List<Date> timeline, int year) {
        // A. 拿到原始数据 (24个)
        Map<String, String> rawMap = calendarService.getRawJieQiMap(String.valueOf(year));
        if (rawMap == null) return;

        // B. 拿到规则里的 12 个节名 (Whitelist)
        // 假设 rules.getJieNames() 返回 ["Lichun", "Jingzhe", ...]
        List<String> validJieNames = baziRules.getMonthValidator();

        // C. 遍历过滤
        for (String jieName : validJieNames) {
            if (rawMap.containsKey(jieName)) {
                String zuluStr = rawMap.get(jieName);

                // D. ★★★ 复用 TimeConverter ★★★
                Date date = TimeConverter.parseZuluToDate(zuluStr);

                if (date != null) {
                    timeline.add(date);
                }
            }
        }
    }
}
