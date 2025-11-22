package com.zhouyi.engine;

import com.zhouyi.domain.Pillar;
import com.zhouyi.domain.SolarDate;
import java.util.Date;

public interface ICalendarService {

    /** 计算真太阳时（核心算法） */
    Date getTrueSolarTime(Date localTime, String timezoneId, double longitude);

    /** 根据年份获取年柱（立春判定） */
    Pillar getYearPillar(Date utcTime);

    /** 根据年月日时获取完整四柱 */
    Pillar[] getFourPillars(Date utcTime, int hour, int minute);
}