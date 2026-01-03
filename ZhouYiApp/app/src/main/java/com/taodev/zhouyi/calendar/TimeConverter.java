package com.taodev.zhouyi.calendar;

import com.taodev.zhouyi.domain.FourPillarsInput;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeConverter {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static LocalDateTime parse(String dateStr) {
        if (dateStr == null) return null;
        try {
            // 使用现代 API 解析
            return LocalDateTime.parse(dateStr, FORMATTER);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
