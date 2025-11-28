package com.taodev.zhouyi.calendar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.engine.ICalendarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class CommonCalendarService implements ICalendarService {
    //输入DTO
    private FourPillarsInput fourPillarsInput;
    //获取UTC时间
    private TimeConverter timeConverter;
    private final Context context;


    //用于EOT 数据，避免重复读取文件
    private Map<String, Double> eotCache;


    // 1. 快捷入口添加构造函数，强制要求传入 Context
    public CommonCalendarService(Context context) {
        // 使用 getApplicationContext() 防止内存泄漏
        this.context = context.getApplicationContext();
        new TimeConverter();
        // 初始化时加载数据（只跑一次）
        loadEotData();
    }
    // 2. 【主入口】全参数
    // 场景：依赖注入、单元测试、或者需要自定义转换器逻辑时用这个
    public CommonCalendarService(Context context, TimeConverter timeConverter) {
        this.context = context;
        this.timeConverter = timeConverter;
        loadEotData();
    }
    // 读取文件
    private void loadEotData() {
        // context 从 Repository 传
        try (InputStream is = context.getAssets().open("eot.json")) {
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, Double>>() {}.getType();
            eotCache = gson.fromJson(new InputStreamReader(is), mapType);
        } catch (IOException e) {
            e.printStackTrace();
            // 失败时防止空指针
            eotCache = new HashMap<>();
        }
    }

    /**
     *
     * @param input  输入DTO,本地时间（用户填的生日+时辰），时区（出生地点），经度（真太阳时必备）
     * @return
     */
    @Override
    public Date getTrueSolarTime(FourPillarsInput input) {

        //本地出生时间，用户出生时期的钟表时间
        LocalDateTime localDateTime = input.getLocalDateTime();
        //经度
        double longitude = input.getLongitude();
        //1、Coordinated Universal Time （协调世界时）
        Date zonedDateTime = timeConverter.convertToUtc(input);
        Date utcDate = Date.from(zonedDateTime.toInstant());
        //2、计算经度时差（分钟）
        double longitudeDiffMinutes = (longitude - 120.0) * 4;
        //3、查EOT表
        double eotMinutes = getEotFromJson(localDateTime);
        //4、总矫正(毫秒)
        long totalCorrectionMS = Math.round((longitudeDiffMinutes+eotMinutes) * 60 * 1000);
        //5、返回真太阳时
        return new Date(utcDate.getTime() + totalCorrectionMS);
    }

    private double getEotFromJson(LocalDateTime localDateTime) {
        if (eotCache == null || eotCache.isEmpty()) {
            return 0.0;
        }

        // 将时间格式化为 key，例如 "2025-11-27"
        // 注意：截图里你直接用了 date.toString()，这可能包含时间，key 会匹配不上
        // 建议转为 LocalDate 再 toString
        String dateKey = localDateTime.toLocalDate().toString();

        return eotCache.getOrDefault(dateKey, 0.0);
    }


    /**
     *
     * @param input 输入DTO,真太阳时
     * @return
     */
    @Override
    public Pillar getYearPillar(FourPillarsInput input) {

        return null;
    }

    /**
     *
     * @param input 输入DTO,真太阳时
     * @return
     */
    @Override
    public Pillar getDayPillar(FourPillarsInput input) {
        return null;
    }

    /**
     *
     * @param input 输入DTO,真太阳时
     * @return
     */
    @Override
    public Pillar getMonthPillar(FourPillarsInput input) {
        return null;
    }

    /**
     *
     * @param input   输入DTO,真太阳时，时辰（小时），分钟
     * @return
     */
    @Override
    public Pillar getHourPillar(FourPillarsInput input) {
        return null;
    }

    /**
     *
     * @param input 输入 DTO
     * @return
     */
    @Override
    public Pillar[] getFourPillars(FourPillarsInput input) {
        return ICalendarService.super.getFourPillars(input);
    }
}
