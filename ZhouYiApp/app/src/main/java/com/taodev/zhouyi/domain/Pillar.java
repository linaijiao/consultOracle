package com.taodev.zhouyi.domain;

import java.util.Collection;
import java.util.List;

public class Pillar {
    //天干
    private String stem;
    private String branch;

    public List<String> getHiddenStems() {
        return hiddenStems;
    }

    // 四柱天干的十神
    private String stemTenGod;
    // 藏干
    private List<String> hiddenStems;

    // 四柱地支隐藏的天干的十神；也就是藏干的十神
    // 地支藏干的十神列表 (可能有 1~3 个)
    // 对象，包含：天干名 + 十神名
    public List<HiddenStemInfo> hiddenStemInfos;

    // 存放 "长生", "帝旺" 等
    public String lifeStage;

    public String getLifeStage() {
        return lifeStage;
    }

    // 纳音 (例如 "海中金")
    private String naYin;

    //空亡
    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public List<HiddenStemInfo> getHiddenStemInfos() {
        return hiddenStemInfos;
    }

    public void setHiddenStemInfos(List<HiddenStemInfo> hiddenStemInfos) {
        this.hiddenStemInfos = hiddenStemInfos;
    }

    public String getNaYin() {
        return naYin;
    }

    public void setNaYin(String naYin) {
        this.naYin = naYin;
    }

    public String getKongWang() {
        return kongWang;
    }

    public void setKongWang(String kongWang) {
        this.kongWang = kongWang;
    }

    // 空亡 (例如 "戌亥")
    private String kongWang;

    public String getStemTenGod() {
        return stemTenGod;
    }

    public void setStemTenGod(String stemTenGod) {
        this.stemTenGod = stemTenGod;
    }
    //天干十神
    //地支

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public Pillar(String stem, String branch) {
        this.stem = stem;
        this.branch = branch;
    }
    public List<String> HiddenStemInfo(){
        return hiddenStems;
    }

    public void setHiddenStems(List<HiddenStemInfo> infos) {
        this.hiddenStemInfos = infos;
    }

    public static class HiddenStemInfo {
        // 藏干天干 (如 "丙")
        public String stemName;
        // 对应的十神 (如 "食神")
        public String tenGodName;
        public HiddenStemInfo(String stemName, String tenGodName) {
            this.stemName = stemName;
            this.tenGodName = tenGodName;
        }
    }

}
