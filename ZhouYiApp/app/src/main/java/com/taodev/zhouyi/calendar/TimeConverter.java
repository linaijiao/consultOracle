package com.taodev.zhouyi.calendar;

import com.taodev.zhouyi.domain.FourPillarsInput;

import java.time.Instant;
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
     * ★★★直接解析 Zulu 字符串为 Date 对象 ★★★
     * 服务于 jieQiCache -> Timeline 的转换
     * @param zuluDateStr 例如 "1900-02-04T05:51:31Z"
     * @return Date (UTC)
     */
    public static Date parseZuluToDate(String zuluDateStr) {
        if (zuluDateStr == null) return null;
        try {
            // Instant.parse 天然支持 "T...Z" 格式
            Instant instant = Instant.parse(zuluDateStr);
            return Date.from(instant);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取UTC时间
     * @param input DTO输入
     * @return zulu UTC
     */
    public static Date convertToUtc(FourPillarsInput input) {
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
