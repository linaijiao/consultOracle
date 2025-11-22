package com.zhouyi.calendar;

import com.zhouyi.domain.Pillar;
import com.zhouyi.engine.ICalendarService;

import java.util.Date;

public class CommonCalendarService implements ICalendarService {
    @Override
    public Date getTrueSolarTime(Date localTime, String timezoneId, double longitude) {
        return null;
    }

    @Override
    public Pillar getYearPillar(Date utcTime) {
        return null;
    }

    @Override
    public Pillar getDayPillar(Date utcTrueSolarTime) {
        return null;
    }

    @Override
    public Pillar getMonthPillar(Date utcTrueSolarTime) {
        return null;
    }

    @Override
    public Pillar getHourPillar(Date utcTrueSolarTime, int hour, int minute) {
        return null;
    }

    @Override
    public Pillar[] getFourPillars(Date localTime, String timezoneId, double longitude, int hour, int minute) {
        return ICalendarService.super.getFourPillars(localTime, timezoneId, longitude, hour, minute);
    }
}
