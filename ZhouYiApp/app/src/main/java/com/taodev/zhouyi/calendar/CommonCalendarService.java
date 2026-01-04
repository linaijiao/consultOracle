package com.taodev.zhouyi.calendar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.taodev.zhouyi.core.repository.BaziDataLoader;
import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.Pillar;
import com.taodev.zhouyi.engine.ICalendarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import android.content.Context;

public class CommonCalendarService implements ICalendarService {
    //输入DTO
    private FourPillarsInput fourPillarsInput;
    //获取UTC时间
    private TimeConverter timeConverter;
    private final Context context;
    //用于EOT 数据，避免重复读取文件
    private Map<String, Double> eotCache;
    //八字規則緩存
    private BaziRules baziRulesCache;
    // 节气缓存
    // 缓存变量 Map 类型
    // 结构：Map<年份String, Map<节气名String, 时间String>>
    private Map<String, Map<String, String> >jieQiCache = null;


    // 1. 快捷入口添加构造函数，强制要求传入 Context

    public CommonCalendarService(Context context) {
        // 使用 getApplicationContext() 防止内存泄漏
        this.context = context.getApplicationContext();
        this.timeConverter = new TimeConverter();
        BaziDataLoader loader = BaziDataLoader.getInstance(context);
        this.baziRulesCache = loader.getBaziRules();
        if (this.baziRulesCache == null) {
            throw new RuntimeException("致命错误：核心数据 bazirules.json 加载失败！");
        }
        this.jieQiCache = loader.getJieQiData();
        if (BaziDataLoader.getInstance(context).getJieQiData() == null) {
            throw new RuntimeException("致命错误：节气数据 jieqi_data.json 加载失败！");
        }
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
        try (InputStream is = context.getAssets().open("eot/eot.json")) {
            Gson gson = new Gson();
            //把整个文件解析成一个大 JSON 对象 (JsonObject)
            JsonObject jsonObject = gson.fromJson(new InputStreamReader(is), JsonObject.class);
            if (jsonObject.has("equationOfTime")) {
                JsonObject eotData = jsonObject.getAsJsonObject("equationOfTime");
                Type mapType = new TypeToken<Map<String, Double>>() {}.getType();
                eotCache = gson.fromJson(eotData, mapType);
            }else{
                // 防止字段不存在
                eotCache = new HashMap<>();
            }
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
//        String dateKey = localDateTime.toLocalDate().toString();
        String dateKey = String.format(java.util.Locale.US,"%02d-%02d",
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth());
        Double val = eotCache.get(dateKey);
        return val != null ? val : 0.0;
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
            // 防止内容读取失败导致空指针
            if ( baziRulesCache.getSexagenaryCycle() == null) {
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
     * 计算日柱
     * 算法：(基准日干支 + 偏移天数) % 60
     * 基准：1900年1月1日 是 甲戌日 (索引 10)
     * @param input 输入DTO,真太阳时
     * @return
     */
    @Override
    public Pillar getDayPillar(FourPillarsInput input) {
        // 如果是 23:00 以后，在八字里算作第二天
        LocalDateTime calcTime = input.getLocalDateTime();
        if (calcTime.getHour() >= 23) {
            calcTime = calcTime.plusDays(1);
        }
        // 基准日期：1900-01-01
        LocalDate baseDate = LocalDate.of(1900, 1, 1);
        LocalDate targetDate = calcTime.toLocalDate();
        // 计算相差天数
        long daysBetween = ChronoUnit.DAYS.between(baseDate, targetDate);
        // 计算偏移量
        // 1900-01-01 是 甲戌(10)，所以初始值是 10
        // 注意：daysBetween 可能是负数（如果算1900以前），所以要处理负数取模
        long offset = (10 + daysBetween) % 60;
        // 处理负数取模结果
        if (offset < 0) {
            offset += 60;
        }
        // 5. 查表获取干支
        String ganZhi = baziRulesCache.getSexagenaryCycle().get((int)offset);
        return new Pillar(ganZhi.substring(0, 1), ganZhi.substring(1, 2));
    }

    /**
     *
     * @param input 输入DTO,真太阳时
     * @return
     */
    @Override
    public Pillar getMonthPillar(FourPillarsInput input) {
        // 1. 准备数据
        LocalDateTime inputTime = input.getLocalDateTime();
        int year = inputTime.getYear();
        // 1. 获取当年的节气数据 (Map<String, String> jieQiMap)
        // 结构例如: {"Lichun": "1900-02-04T05:51:31Z", "Jingzhe":"..."}
        Map<String, String> currentYearJieQi = jieQiCache.get(String.valueOf(year));
        if (currentYearJieQi == null) {
            throw new IllegalArgumentException("暂不支持该年份的节气查询：" + year);
        }

        // 从 JSON 规则中获取有序列表
        // ["Lichun", "Jingzhe"...]
        List<String> jieNames = baziRulesCache.getMonthValidator();
        // ["寅", "卯"...]
        List<String> branchNames = baziRulesCache.getMonthBranches();
        // 2. 核心算法：遍历这12个节，看时间落在哪个区间
        int foundIndex = -1;
        // 倒序遍历（从“小寒”往前找）
        // 或者正序遍历，找到第一个“大于”当前时间的节气，然后取前一个
        for (int i = 0; i < jieNames.size(); i++) {
            String jieName = jieNames.get(i);
            // "1900-02-04T05:51:31Z" 是内容是UTC日期字符串
            String jieDateStr = currentYearJieQi.get(jieName);
            // 解析 UTC 时间
            LocalDateTime jieTime = TimeConverter.parse(jieDateStr);

            if (inputTime.isAfter(jieTime) || inputTime.isEqual(jieTime)) {
                // 以上面取得“Lichun”的日期为例子，如果输入时间在立春之后，暂定是寅月；继续往后看，如果在惊蛰之后，那就是卯月...
                foundIndex = i;
            } else {
                // 一旦发现输入时间比某个节气早，说明没跨过去，循环结束，
                break;
            }
        }
        // 3. 处理特殊情况：foundIndex == -1
        // 说明比当年的“立春”还早 (比如 2024-01-20)
        // 这时候属于【上一年】的最后一个月 (丑月)
        if (foundIndex == -1) {
            // A. 既然没过立春，从八字角度看，年柱是上一年的
            // 这里的关键是：一定要保证 getYearPillar 能正确处理立春前的年份
            // 或者我们手动计算上一年的天干
            Pillar yearPillar = getYearPillar(input);
            // 这应该是上一年的年干 (比如癸)
            String yearStem = yearPillar.getStem();

            // B. 判断是 子月 还是 丑月？关键看 "小寒"
            // 小寒通常在 1月5日或6日，是 子/丑 的分界线
            String xiaoHanStr = currentYearJieQi.get("XiaoHan");
            LocalDateTime xiaoHanTime = TimeConverter.parse(xiaoHanStr);

            int monthStemIndexOffset; // 月份偏移量
            String monthBranch;       // 月支

            if (inputTime.isBefore(xiaoHanTime)) {
                // 还没到小寒 (1月1日 - 1月5日) -> 上一年的 子月 (11月)
                monthBranch = "子";
                monthStemIndexOffset = 10; // 对应子月
            } else {
                // 过了小寒，但没到立春 (1月6日 - 2月3日) -> 上一年的 丑月 (12月)
                monthBranch = "丑";
                monthStemIndexOffset = 11; // 对应丑月
            }
            // C. 计算月干 (使用上一年的年干 + 五虎遁)
            return calculateMonthStem(yearStem, monthStemIndexOffset, monthBranch);
        }
        // 4. 获取地支：确定的月份节气对应的地支，十二个节气对应是个地支
        String currentBranch = branchNames.get(foundIndex);
        // 先获取年柱的天干
        String yearStem = getYearPillar(input).getStem();

        // 计算月干 (foundIndex 就是偏移量，0=寅月, 1=卯月...)
        return calculateMonthStem(yearStem, foundIndex, currentBranch);
    }
    /**
     * 提取公共方法：计算月干 (五虎遁)
     * @param yearStem 年干 (甲, 乙...)
     * @param offset 月份偏移量 (0=寅, 1=卯 ... 10=子, 11=丑)
     * @param branch 月支 (寅, 卯...)
     */
    private Pillar calculateMonthStem(String yearStem, int offset, String branch) {
        // 1. 查表：甲年起丙寅
        String startStem = baziRulesCache.getMonthStartRule().get(yearStem);

        // 2. 转索引 (丙 -> 2)
        int startStemIndex = baziRulesCache.getHeavenlyStems().indexOf(startStem);

        // 3. 计算最终月干索引
        int currentStemIndex = (startStemIndex + offset) % 10;
        String monthStem = baziRulesCache.getHeavenlyStems().get(currentStemIndex);

        return new Pillar(monthStem, branch);
    }

    /**
     * 计算时柱
     * 规则：五鼠遁 (Five Rats Hunting)
     * @param input   输入DTO,真太阳时，时辰（小时），分钟
     * @return
     */
    public Pillar getHourPillar(FourPillarsInput input) {
        // 1. 获取时辰的地支索引的偏移量
        // 23:00-01:00 => 子(0)
        // 01:00-03:00 => 丑(1)
        // 公式：(小时 + 1) / 2
        int hour = input.getLocalDateTime().getHour();
        int branchIndex = (hour + 1) / 2;
        if (branchIndex >= 12) {
            branchIndex = 0; // 23点和24点(0点) 都是子时
        }
        // 2. 查表获取地支(0=子, 1=丑, ... 11=亥)
        String branch = baziRulesCache.getEarthlyBranches().get(branchIndex);

        // 3. 获取日柱天干 (用于推算时干)
        Pillar dayPillar = getDayPillar(input);
        String dayStem = dayPillar.getStem();

        // 4. 五鼠遁推算时干
        // 甲己还加甲 (甲/己日 -> 子时起甲子) 即如果日干是甲或着已那么时干就是甲,那么时柱的天干就是甲子
        // 乙庚丙作初 (乙/庚日 -> 子时起丙子)
        // 丙辛从戊起 (丙/辛日 -> 子时起戊子)
        // 丁壬庚子居 (丁/壬日 -> 子时起庚子)
        // 戊癸何方发，壬子是真途 (戊/癸日 -> 子时起壬子)

        //由日干查时辰的起始规则hourStartRule获取,key是dayStem;五鼠遁
        String startStem = baziRulesCache.getHourStartRule().get(dayStem);

        Objects.requireNonNull(startStem,"数据异常：无法根据日干 [" + dayStem + "] 推算时辰，请检查日柱数据。");

        //startStem在天干表stemList中所在位置
        List<String> stemsList = baziRulesCache.getHeavenlyStems();
        int startIndex = stemsList.indexOf(startStem);

        // 原始公式：时干索引 = (日干索引 % 5 * 2 + 时支索引) % 10
        // startIndex = 日干索引 % 5 * 2
        // startIndex只是当天的子时起始位置
        int hourStemIndex = (startIndex + branchIndex) % 10;
        String stem = stemsList.get(hourStemIndex);

        return new Pillar(stem, branch);
    }

    /**
     *
     * @param input 输入 DTO
     * @return
     */
    @Override
    public Pillar[] getFourPillars(FourPillarsInput input) {
        // 1. 计算年柱 (内部已包含立春判断)
        Pillar yearPillar = getYearPillar(input);

        // 2. 计算月柱 (内部已包含节气和年初回退)
        Pillar monthPillar = getMonthPillar(input);

        // 3. 计算日柱 (基于1900或1970基准)
        Pillar dayPillar = getDayPillar(input);

        // 4. 计算时柱 (内部会自动再次调用 getDayPillar 获取日干)
        // 如果想性能极致优化，可以把 dayPillar 传进去，不过现在这样写结构更解耦
        Pillar hourPillar = getHourPillar(input);

        // 5. 组装返回！
        // 顺序：年、月、日、时
        Pillar[] pillars = new Pillar[] {
                yearPillar,
                monthPillar,
                dayPillar,
                hourPillar
        };
        // ==========================================
        // 计算藏干和十神
        // ==========================================

        // 1. 获取日主 (日干) —— 所有十神都是跟它比
        String dayMaster = dayPillar.getStem();

        // 2. 获取藏干规则 Map
        Map<String, List<String>> hiddenRules = baziRulesCache.getHiddenStems();
        TenGodCalculator tenGodCalculator = new TenGodCalculator(baziRulesCache);
        // 3. 遍历四柱，填充信息
        for (Pillar pillar : pillars) {
            // A. 算天干的十神 (注意：日柱的天干十神通常叫"日主"或"元男/女"，也可以显示"比肩")
            if (pillar == dayPillar) {
                pillar.setStemTenGod("日主");
            } else {
                String tenGod = tenGodCalculator.calculate(dayMaster, pillar.getStem());
                pillar.setStemTenGod(tenGod);
            }

            // B. 找地支藏干
            String branch = pillar.getBranch();
            List<String> hStems = hiddenRules.get(branch); // 查表
            List<Pillar.HiddenStemInfo> infos = new ArrayList<>();
            // C. 算藏干的十神
            if (hStems != null) {
                List<String> hTenGods = new ArrayList<>();
                for (String hStem : hStems) {
                    // 用日主 vs 藏干
                    String htengod = tenGodCalculator.calculate(dayMaster, hStem);
                    Pillar.HiddenStemInfo info = new Pillar.HiddenStemInfo(hStem,htengod);
                    info.stemName=hStem;
                    info.tenGodName=htengod;
                    infos.add(info);
                }
            }
            pillar.setHiddenStems(infos);
        }
        return pillars;
    }

    /**
     * 辅助方法：读取 JSON 获取立春时间 (Gson版)
     */
    public Date getLiChunUtc(int year) throws Exception {

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
}
