package com.taodev.zhouyi.fourpillars.ui;

import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class    FourPillarsDisplayModel implements Serializable {
    // 1. 顶部基本描述
    public String chartTitleText;
    public String basicInfoText;

    //四柱
    public List<PillarUiModel> pillars = new ArrayList<>();

    //节气信息
    public String solarTermsInfo;
    //起运周岁、交运日期
    public String startLuckInfo;

    // 3. 大运列表
    public List<LuckPillarUiModel> luckPillars = new ArrayList<>();


    // 空构造函数
    public FourPillarsDisplayModel() {}

    public static class PillarUiModel {
        // 柱名标题 (如 "年柱")
        public String columnName;
        // 顶部十神(天干十神)
        public String headTenGod;
        // 天干
        public String stemName;
        // 地支
        public String branchName;
        // 藏干及地支的十神
        public List<HiddenStemUiItem> hiddenStemItems;
        // 给 TextView 显示用的字符串
        public String hiddenStemString = "";
        // 衰旺:存放 "长生", "帝旺" 等
        public String lifeStage;
        // 纳音
        public String naYin;
        // 空亡 (例如 "戌亥")
        public String kongWang;

        public String getColumnName() {
            return columnName;
        }

        public String getHeadTenGod() {
            return headTenGod;
        }

        public String getStemName() {
            return stemName;
        }

        public String getBranchName() {
            return branchName;
        }

        public List<HiddenStemUiItem> getHiddenStemItems() {
            return hiddenStemItems;
        }

        public String getLifeStage() {
            return lifeStage;
        }

        public String getNaYin() {
            return naYin;
        }

        public String getKongWang() {
            return kongWang;
        }
    }
    public static class HiddenStemUiItem {
        // 藏干名字 (如 "甲")
        public String stem;
        // 地支藏干对应的十神 (如 "偏印")
        public String god;

        public HiddenStemUiItem(String stem, String god) {
            this.stem = stem;
            this.god = god;
        }
    }

    public static class LuckPillarUiModel {
        // 十神 (如：杀)
        public String tenGod;
        // 干支 (如：丁亥)
        public String pillarName;
        // 长生 (如：沐浴)
        public String lifeStage;
        // 虚岁 (如：7岁)
        public String ageInfo;
        // 始于 (如：2011)
        public String startYear;
        //流年，从大运起始年的干支到大运终止年的干支
        public List<String> yearlyLuckList;
        // 给 TextView 显示用的字符串
        public String yearlyLuckListString = "";
        // 止于 (如：2020)
        public String endYear;
        public String getTenGod() {
            return tenGod;
        }

        public String getPillarName() {
            return pillarName;
        }

        public String getLifeStage() {
            return lifeStage;
        }

        public String getAgeInfo() {
            return ageInfo;
        }

        public String getStartYear() {
            return startYear;
        }

        public String getEndYear() {
            return endYear;
        }
        public List<String> getYearlyLuckList() {
            return yearlyLuckList;
        }

        public void setYearlyLuckList(List<String> yearlyLuckList) {
            this.yearlyLuckList = yearlyLuckList;
        }
    }
}