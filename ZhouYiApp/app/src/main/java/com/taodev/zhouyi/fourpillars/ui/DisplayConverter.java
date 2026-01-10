package com.taodev.zhouyi.fourpillars.ui;

import android.icu.util.Calendar;

import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DisplayConverter {
    public static FourPillarsDisplayModel convertToDisplayModel(FourPillarsResult result) {
        FourPillarsDisplayModel model = new FourPillarsDisplayModel();

        if (result == null) return model;
        // 1. 格式化基本信息
        LocalDateTime date = result.getLocalDateTime();
        if (date != null) {
            model.basicInfoText = String.format(
                    "命主姓名：%s。\n出生公历: %d年%d月%d日 %d时 (输入时间)",
                    result.getName(),
                    date.getYear(),
                    date.getMonthValue(), // 注意：getMonth()返回英文枚举，getMonthValue()才是数字
                    date.getDayOfMonth(),
                    date.getHour());
        }
        // 2. 格式化四柱 (调用下面的辅助方法)
        List<FourPillarsDisplayModel.PillarUiModel> uiPillars = new ArrayList<>();
        String[] titles = {"年柱", "月柱", "日柱", "时柱"};
        //获取四柱
        List<Pillar> rawPillars = result.getPillars();
        // 遍历 4 个柱子 (年、月、日、时)
        for (int i = 0; i < 4; i++) {
            // 拿到原始数据
            // 调用 convertPillar 把单个柱子转好，放进列表
            model.pillars.add(convertPillar(rawPillars.get(i), titles[i]));
        }

        // 3. 直接传递对象列表
        // 直接把 List<LuckPillar> 给 DisplayModel
        // 这样 Adapter 取出里面的 startYear, age, yearlyLuckList 来显示
        for (LuckPillar luck : result.getLuckPillars()) {
            // 调用小助手 convertLuckPillar
            model.luckPillars.add(convertLuckPillar(result,luck));
        }
        // 4. 起运日期，交运日期
        // 使用 Calendar 实例
        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getLuckPillars().get(0).getTransitionDate());
        // 安全地获取年份
        int baseStartYear = cal.get(Calendar.YEAR);
        model.startLuckInfo = String.format(
                "起运周岁：%s,交运日期: %d年%d月%d日",
                result.getLuckPillars().get(0).getStartAgeDesc(),
                cal.get(Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH),
                cal.get(java.util.Calendar.HOUR)
        );


        return model;
    }
    private static FourPillarsDisplayModel.PillarUiModel convertPillar(Pillar rawPillar, String title) {
        // 1. 创建 UI 对象 ( uiPillar)
        FourPillarsDisplayModel.PillarUiModel uiModel = new FourPillarsDisplayModel.PillarUiModel();

        // 2. 如果原始数据为空，返回一个空壳防止崩溃
        if (rawPillar == null) {
            uiModel.columnName = title;
            uiModel.stemName = "";
            return uiModel;
        }

        // 3. 正常赋值
        uiModel.columnName = title; // 设置标题 "年柱"
        uiModel.stemName = rawPillar.getStem();
        uiModel.branchName = rawPillar.getBranch();
        uiModel.headTenGod = rawPillar.getStemTenGod();
        // 1. 获取源数据 (Domain List)
        List<Pillar.HiddenStemInfo> sourceList = rawPillar.getHiddenStemInfos();
        // 2. 初始化目标列表 (UI List)
        uiModel.hiddenStemItems = new ArrayList<>();
        // 3. 搬运数据
        if (sourceList != null) {
            for (Pillar.HiddenStemInfo sourceItem : sourceList) {
                //  创建一个新的 UI 对象
                FourPillarsDisplayModel.HiddenStemUiItem uiItem =
                        new FourPillarsDisplayModel.HiddenStemUiItem(sourceItem.getStemName(), sourceItem.getTenGodName());
                //  加到 UI 列表里
                uiModel.hiddenStemItems.add(uiItem);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (uiModel.hiddenStemItems != null) {
            for (FourPillarsDisplayModel.HiddenStemUiItem item : uiModel.hiddenStemItems) {
                // 拼接方式：天干 + 空格
                // 效果："癸 辛 己 "
//                sb.append(item.stem).append(" ");

                // 显示十神，可以改成这样：
                 sb.append(item.stem).append("(").append(item.god).append(") ");
            }
        }
        uiModel.hiddenStemString = sb.toString();
        uiModel.lifeStage =rawPillar.getLifeStage();
        uiModel.naYin = rawPillar.getNaYin();
        uiModel.kongWang = rawPillar.getKongWang();
        // ... 处理藏干 List ...

        return uiModel;
    }

    /**
     * 输入：原始的藏干列表
     * 输出：UI 专用的藏干列表
     *  */
    private static List<FourPillarsDisplayModel.HiddenStemUiItem> convertHiddenStems(List<Pillar.HiddenStemInfo> rawStems) {
        List<FourPillarsDisplayModel.HiddenStemUiItem> uiHiddenStemList = new ArrayList<>();

        // 1. 安全检查：如果原始数据是空的，直接返回空列表 (防止崩溃)
        if (rawStems == null || rawStems.isEmpty()) {
            return uiHiddenStemList;
        }

        // 2. 遍历每一个原始藏干
        for (Pillar.HiddenStemInfo rawItem : rawStems) {
            if (rawItem != null) {
                // 获取天干名字 (比如 "丙")
                String stemName = rawItem.getStemName() != null ? rawItem.getStemName(): "";

                // 获取十神名字 (比如 "食神")
                String tenGodName = rawItem.getTenGodName() != null ? rawItem.getTenGodName() : "";

                // 3. 创建 UI 对象并添加
                // (假设你的 HiddenStemUiItem 有这个构造函数)
                uiHiddenStemList.add(new FourPillarsDisplayModel.HiddenStemUiItem(stemName, tenGodName));
            }
        }

        return uiHiddenStemList;
    }
    private static FourPillarsDisplayModel.LuckPillarUiModel convertLuckPillar(FourPillarsResult fourPillarsResult, LuckPillar raw) {
        FourPillarsDisplayModel.LuckPillarUiModel luckPillarUiModel = new FourPillarsDisplayModel.LuckPillarUiModel();
        luckPillarUiModel.tenGod=raw.getTenGod();
        luckPillarUiModel.pillarName = raw.getGanZhi();
        luckPillarUiModel.lifeStage = raw.getLifeStage();
        //虛歲
        int birthYear = fourPillarsResult.getBirthDate().getYear();
//        luckPillarUiModel.ageInfo = (raw.getStartYear() - birthYear + 1) + "";
        luckPillarUiModel.ageInfo = raw.getStartNominalAge() + "";
        luckPillarUiModel.startYear = raw.getStartYear() +"";
        luckPillarUiModel.endYear = raw.getEndYear() + "";

        // ... 赋值流年 list ...
        List<String> yearlyLuckList = raw.getYearlyLuckList();
        if (yearlyLuckList != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String ganZhi : yearlyLuckList) {
                //拼一个干支，换一行
                stringBuilder.append(ganZhi).append("\n"); //
            }
            // 删除最后一个多余的换行符
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
            luckPillarUiModel.yearlyLuckList = raw.getYearlyLuckList();
            luckPillarUiModel.yearlyLuckListString = stringBuilder.toString();
        }
        return luckPillarUiModel;
    }

    // 辅助方法：把一根柱子变成漂亮的文本
    private static String formatPillar(Pillar pillar) {
        if (pillar == null) return "";
        // 显示格式：
        // 比肩
        // 甲子
        // [海中金]
        return pillar.getStemTenGod() + "\n" +
                pillar.getStem() + pillar.getBranch() + "\n" +
                "[" + pillar.getNaYin() + "]";
    }
}
