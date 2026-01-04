package com.taodev.zhouyi.facade;

import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.Pillar;

import com.taodev.zhouyi.calendar.CommonCalendarService;
import com.taodev.zhouyi.engine.ICalendarService;
import com.taodev.zhouyi.engine.IFourPillarsAnalysisService;

/**
 * 八字排盘总门面 (The Stitcher)
 * 职责：协调日历服务和分析服务，对外提供统一接口
 */
public class BaziFacade {

    private final ICalendarService calendarService;
    private final IFourPillarsAnalysisService analysisService;

    public BaziFacade(CommonCalendarService calendarService, IFourPillarsAnalysisService analysisService) {
        this.calendarService = calendarService;
        this.analysisService = analysisService;
    }

    /**
     * 对外暴露的唯一方法
     */
    public FourPillarsResult performFullAnalysis(FourPillarsInput input) {
        // 1. 准备空盘
        FourPillarsResult result = new FourPillarsResult();

        // 2. 调用日历服务：算出四柱干支
        Pillar[] fourPillars = calendarService.getFourPillars(input);
        result.setGender(input.getGender());
        //出生日期 + 时间
        result.setLocalDateTime(input.getLocalDateTime());
        
        // 装填半成品
        result.setYearPillar(fourPillars[0]);
        result.setMonthPillar(fourPillars[1]);
        result.setDayPillar(fourPillars[2]);
        result.setHourPillar(fourPillars[3]);

        // 3. 【第二棒】调用分析服务：进行深度加工
        // 这里 analysisService.analyze(result) 会把十神、大运等填进去
        analysisService.analyze(result);

        // 4. 返回最终成品
        return result;
    }
}