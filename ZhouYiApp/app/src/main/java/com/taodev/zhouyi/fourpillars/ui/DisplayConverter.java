package com.taodev.zhouyi.fourpillars.ui;

import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    public class DisplayConverter {
        public static FourPillarsDisplayModel convert(FourPillarsResult result) {
            FourPillarsDisplayModel model = new FourPillarsDisplayModel();

            if (result == null) return model;
            // 1. 格式化基本信息
            LocalDateTime date = result.getLocalDateTime();
            if (date != null) {
                model.basicInfoText = String.format("%d年%d月%d日 %d时 (输入时间)",
                        date.getYear(),
                        date.getMonthValue(), // 注意：getMonth()返回英文枚举，getMonthValue()才是数字
                        date.getDayOfMonth(),
                        date.getHour());
            }
            // 2. 格式化四柱 (调用下面的辅助方法)
            model.yearPillarText = formatPillar(result.getYearPillar());
            model.monthPillarText = formatPillar(result.getMonthPillar());
            model.dayPillarText = formatPillar(result.getDayPillar());
            model.hourPillarText = formatPillar(result.getHourPillar());

            // 3. 直接传递对象列表
            // 直接把 List<LuckPillar> 给 DisplayModel
            // 这样 Adapter 取出里面的 startYear, age, liuNianList 来显示
            if (result.getLuckPillars() != null) {
                model.luckPillarList = result.getLuckPillars();
            }

            return model;
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
