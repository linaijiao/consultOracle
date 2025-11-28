// 文件：com/zhouyi/engine/ICalendarService.java
package com.taodev.zhouyi.engine;

import com.taodev.zhouyi.calendar.TimeConverter;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.Pillar;
import java.util.Date;

/**
 * The interface Calendar service.
 */
public interface ICalendarService {

    /**
     * 计算真太阳时
     * 根据输入的本地时间、时区和经度校正为真太阳时（经度时差 + EOT 方程）。
     * @param input  输入DTO,本地时间（用户填的生日+时辰），时区（出生地点），经度（真太阳时必备）
     * @return 真太阳时
     */
//    Date getTrueSolarTime(Date localTime, String timezoneId, double longitude);
//    Date getTrueSolarTime(Date localTime, String timezoneId, double longitude);
    Date getTrueSolarTime(FourPillarsInput input);

    /**
     * 年柱（立春判定）—— 四柱八字 + 紫微斗数 都要用
     * @param input 输入DTO,真太阳时
     * @return 年柱
     */
    Pillar getYearPillar(FourPillarsInput input);

    /**
     * 月柱（节气判定）—— 四柱八字 + 紫微斗数 都要用
     * @param input 输入DTO,真太阳时
     * @return 月柱
     */
    Pillar getMonthPillar(FourPillarsInput input);

    /**
     * 日柱 —— 四柱八字 + 紫微斗数 都要用
     * @param input 输入DTO,真太阳时
     * @return 日柱
     */
    Pillar getDayPillar(FourPillarsInput input);

    /**
     * 时柱（早子时处理）—— 四柱八字 + 紫微斗数 都要用
     * @param input   输入DTO,真太阳时，时辰（小时），分钟（）
     * @return 时柱
     */
    Pillar getHourPillar(FourPillarsInput input);

    /**
     * 组合方法：获取完整四柱（四柱八字专属，其他模块忽略）
     * @param input 输入 DTO
     * @return 四柱数组
     */
    default Pillar[] getFourPillars(FourPillarsInput input) {
        Date utc = getTrueSolarTime(input);
        return new Pillar[] {
                getYearPillar(input),
                getMonthPillar(input),
                getDayPillar(input),
                getHourPillar(input)
        };
    }
}