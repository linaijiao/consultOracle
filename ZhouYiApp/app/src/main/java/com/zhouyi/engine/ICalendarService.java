// 文件：com/zhouyi/engine/ICalendarService.java
package com.zhouyi.engine;

import com.zhouyi.domain.Pillar;
import java.util.Date;

public interface ICalendarService {

    /** 1. 核心：真太阳时校正（所有模块都依赖这个） */
    Date getTrueSolarTime(Date localTime, String timezoneId, double longitude);

    /** 2. 原子方法：年柱（立春判定）—— 四柱八字 + 紫微斗数 都要用 */
    Pillar getYearPillar(Date utcTrueSolarTime);

    /** 3. 原子方法：月柱（节气判定）—— 四柱八字 + 紫微斗数 都要用 */
    Pillar getMonthPillar(Date utcTrueSolarTime);

    /** 4. 原子方法：日柱 —— 四柱八字 + 紫微斗数 都要用 */
    Pillar getDayPillar(Date utcTrueSolarTime);

    /** 5. 原子方法：时柱（早子时处理）—— 四柱八字 + 紫微斗数 都要用 */
    Pillar getHourPillar(Date utcTrueSolarTime, int hour, int minute);

    // 四柱八字专属的“组合方法”，其他模块不需要这个
    default Pillar[] getFourPillars(Date localTime, String timezoneId, double longitude, int hour, int minute) {
        Date utc = getTrueSolarTime(localTime, timezoneId, longitude);
        return new Pillar[] {
                getYearPillar(utc),
                getMonthPillar(utc),
                getDayPillar(utc),
                getHourPillar(utc, hour, minute)
        };
    }
}