package com.taodev.zhouyi.calendar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.engine.ICalendarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;

public class CommonCalendarService implements ICalendarService {
    //输入DTO
    private FourPillarsInput fourPillarsInput;
    //获取UTC时间
    private TimeConverter timeConverter;
    private final Context context;
    private BaziRules baziRulesCache;

    //用于EOT 数据，避免重复读取文件
    private Map<String, Double> eotCache;
    //将缓存变量改为 Map 类型
    // 结构：Map<年份String, Map<节气名String, 时间String>>
    private Map<String, Map<String, String> >jieQiCache = null;



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
     * @return 修正后的太阳时间
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
        // 注意： date.toString()，这可能包含时间，key 会匹配不上
        // LocalDate 再 toString
        String dateKey = localDateTime.toLocalDate().toString();

        return eotCache.getOrDefault(dateKey, 0.0);
    }


    /**
     *
     * @param input 输入DTO,真太阳时
     * @return 年柱
     */
    @Override
    public Pillar getYearPillar(FourPillarsInput input) {

        try {
            // 1. 获取用户出生时间的 UTC 表示 (利用已有的 convertToUtc 方法)
            Date userUtcTime = timeConverter.convertToUtc(input);

            // 2. 获取公历年份
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTime(userUtcTime);
            int gregorianYear = cal.get(Calendar.YEAR);

            // 3. 获取当年的立春时间 (UTC)
            Date liChunTime = getLiChunUtc(gregorianYear);

            // 4. 核心判断：如果出生在立春之前，算上一年
            int chineseYear = gregorianYear;
            if (userUtcTime.before(liChunTime)) {
                chineseYear = gregorianYear - 1;
            }

            // 5. 计算干支索引 (1984年是甲子年，索引0)
            int offset = (chineseYear - 1984) % 60;
            // 处理负数 (例如 1980年出生)
            if (offset < 0) {
                offset += 60;
            }

            //只加载一次数据
            if (baziRulesCache == null) {
                baziRulesCache = loadJsonFromAsset("bazi_rules.json", BaziRules.class);
            }
            // 防止文件读取失败导致空指针
            if (baziRulesCache == null || baziRulesCache.getSexagenaryCycle() == null) {
                return new Pillar("?", "?");
            }
            // 6. 查表返回 Pillar 对象
            // 直接使用缓存的 baziRulesCache
            String ganZhi = baziRulesCache.getSexagenaryCycle().get(offset);

            String stem = ganZhi.substring(0, 1);  // 天干
            String branch = ganZhi.substring(1, 2); // 地支

            return new Pillar(stem, branch);

        } catch (Exception e) {
            e.printStackTrace();
            return new Pillar("?", "?"); // 错误兜底
        }
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

    /**
     * 辅助方法：读取 JSON 获取立春时间 (Gson版)
     */
    private Date getLiChunUtc(int year) throws Exception {
        if (jieQiCache == null) {
            loadJieQiData();
        }

        String yearKey = String.valueOf(year);

        // 【修改 2】使用 Map 的方法获取数据
        if (!jieQiCache.containsKey(yearKey)) {
            throw new Exception("缺少节气数据: " + year);
        }

        // 获取当年的所有节气 Map
        Map<String, String> yearData = jieQiCache.get(yearKey);

        // 获取 "Lichun"
        if (yearData == null || !yearData.containsKey("Lichun")) {
            throw new Exception("年份 " + year + " 缺少立春数据");
        }
        String liChunStr = yearData.get("Lichun");

        // 解析 UTC 时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(liChunStr);
    }
    /**
     * 辅助方法：加载 JSON 文件
     */
    private void loadJieQiData() {
        try {
            // 打开文件流
            InputStream is = context.getAssets().open("jieqi_data.json");

            // 【修改 3】使用 Gson 解析流
            Gson gson = new Gson();
            // 定义复杂的泛型类型: Map<String, Map<String, String>>
            Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();

            // 直接从流中解析，无需手动读取 byte[]，效率更高
            jieQiCache = gson.fromJson(new InputStreamReader(is), type);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            // 防止空指针，初始化一个空 Map
            jieQiCache = new HashMap<>();
        }
    }
    /**
     * 【万能读取器】从 Assets 读取并解析任意类型的 JSON 文件
     * @param fileName 文件名 (例如 "bazi_rules.json")
     * @param typeOfT  Gson 的类型令牌 (告诉 Gson 要转成什么对象)
     * @param <T>      泛型标记
     * @return 解析后的数据对象
     */
    private <T> T loadJsonFromAsset(String fileName, Type typeOfT) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Gson gson = new Gson();
            // 直接流式解析，高效且代码少
            T result = gson.fromJson(new InputStreamReader(is), typeOfT);
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 或者抛出异常，看您喜好
        }
    }
}
