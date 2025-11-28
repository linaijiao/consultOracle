package com.taodev.zhouyi.fourpillars.service;

import com.taodev.zhouyi.engine.ICalendarService;
import com.taodev.zhouyi.engine.IFourPillarsAnalysisService;
import com.taodev.zhouyi.core.service.IFourPillarsService;
import com.taodev.zhouyi.fourpillars.data.FourPillarsRepository;
import com.taodev.zhouyi.fourpillars.model.FourPillarsInput;
import com.taodev.zhouyi.fourpillars.model.FourPillarsResult;
import com.taodev.zhouyi.fourpillars.model.Pillar;
import com.taodev.zhouyi.fourpillars.model.LuckPillar;

import java.util.Date;
import java.util.List;

/**
 * 四柱八字服务实现类
 * 实现IFourPillarsService接口定义的核心功能
 * 依赖日历服务进行时间转换和节气计算，依赖分析服务进行命理分析
 *
 * @author li bo
 * @since 2025-11-22
 */
public class FourPillarsService implements IFourPillarsService {
    // 日历服务：提供时间转换、节气计算和干支查询功能
    private final ICalendarService calendarService;
    // 分析服务：提供命理分析算法（大运、五行、十神等）
    private final IFourPillarsAnalysisService analysisService;
    // 数据仓库：负责计算结果的持久化存储与查询
    private final FourPillarsRepository repository;

    /**
     * 构造函数：通过依赖注入初始化服务组件
     *
     * @param calendarService 日历服务实例，提供时间和干支转换
     * @param analysisService 分析服务实例，提供命理算法实现
     * @param repository      数据仓库实例，提供结果存储能力
     */
    public FourPillarsService(ICalendarService calendarService,
                              IFourPillarsAnalysisService analysisService,
                              FourPillarsRepository repository) {
        this.calendarService = calendarService;
        this.analysisService = analysisService;
        this.repository = repository;
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
        Date utcDate = calendarService.createUTCDate(input.getYear(), input.getMonth(),
                input.getDay(), input.getHour(), input.getMinute(), input.getTimezone());
        // 真太阳时校正：根据经度调整平太阳时，获取真实天文时间
        Date trueSolarTime = calendarService.getTrueSolarTime(utcDate, input.getLongitude());

        // 四柱计算：基于真太阳时依次计算年柱、月柱、日柱、时柱
        Pillar yearPillar = calendarService.getYearPillar(trueSolarTime);
        Pillar monthPillar = calendarService.getMonthPillar(trueSolarTime);
        Pillar dayPillar = calendarService.getDayPillar(trueSolarTime);
        Pillar hourPillar = calendarService.getHourPillar(trueSolarTime);

        // 大运排盘：根据性别和出生时间推算大运周期（每10年一步运）
        List<LuckPillar> luckPillars = analysisService.getLuckPillars(
                yearPillar, monthPillar, trueSolarTime, input.isMale());

        // 五行旺衰分析：基于四柱干支组合分析五行力量分布
        String strengthAnalysis = analysisService.analyzeStrength(
                yearPillar, monthPillar, dayPillar, hourPillar);

        // 十神关系计算：以日柱天干为日主，分析其他干支的十神属性
        var tenGods = analysisService.getTenGods(dayPillar.getHeavenlyStem(),
                yearPillar, monthPillar, dayPillar, hourPillar);

        // 结果组装：整合所有计算结果为统一数据模型
        FourPillarsResult result = new FourPillarsResult();
        result.setBirthDate(utcDate);
        result.setYearPillar(yearPillar);
        result.setMonthPillar(monthPillar);
        result.setDayPillar(dayPillar);
        result.setHourPillar(hourPillar);
        result.setLuckPillars(luckPillars);
        result.setStrengthAnalysis(strengthAnalysis);
        result.setTenGods(tenGods);

        return result;
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
        return repository.save(result);
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
        return repository.getByBirthDate(birthDate);
    }
}
