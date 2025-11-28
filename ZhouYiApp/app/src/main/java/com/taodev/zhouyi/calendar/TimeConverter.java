package com.taodev.zhouyi.calendar;

import com.taodev.zhouyi.domain.FourPillarsInput;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeConverter {
    /**
     * 获取UTC时间
     * @param input DTO输入
     * @return
     */
    public Date convertToUtc(FourPillarsInput input) {
        if (input == null || input.getLocalDateTime() == null) {
            return null; // 或者抛异常
        }
        //获取时区/城市
        String timezoneIdStr = input.getTimezoneIdStr();
        //时区对象
        ZoneId zoneId = ZoneId.of(timezoneIdStr);
        //1、本地时间 → UTC 时间（考虑时区）
        ZonedDateTime zonedDateTime = input.getLocalDateTime().atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }
}
