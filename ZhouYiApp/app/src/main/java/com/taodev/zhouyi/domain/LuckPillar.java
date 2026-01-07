package com.taodev.zhouyi.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 大运柱数据模型
 * 代表排盘结果中的"一竖列"
 */
public class LuckPillar implements Serializable {

    // === 1. 大运本柱信息 (对应表格红字部分) ===
    private String tenGod;    // 大运十神 (e.g. "七杀")
    private String stem;      // 大运天干 (e.g. "丙")
    private String branch;    // 大运地支 (e.g. "寅")
    private String lifeStage; // 大运长生

    // === 2. 时间定位信息 (对应表格中间部分) ===
    private int startAge;     // 存整岁，比如 4
    private String startAgeDesc; //存 "4岁2个月15天"
    private int startYear;    // 起运年份 (e.g. 1996)
    private int endYear;      // 结束年份 (e.g. 2005)


    public String getTenGod() {
        return tenGod;
    }

    // === 3. 下属流年列表 (对应表格最下面的一串字) ===
    // 这一步大运管辖的 10 个流年 (e.g. "丙子", "丁丑"...)
    private List<String> liuNianList;

    // === 构造函数 ===
    public LuckPillar(String tenGod, String stem, String branch,String lifeStage, int startAge,String startAgeDesc, int startYear) {
        this.tenGod = tenGod;
        this.stem = stem;
        this.branch = branch;
        this.lifeStage = lifeStage;
        this.startAge = startAge;
        this.startAgeDesc = startAgeDesc;
        this.startYear = startYear;
        this.endYear = startYear + 9; // 大运管10年
        this.liuNianList = new ArrayList<>();
    }

    // === Getter / Setter 方法 ===

    // 获取完整的干支字符串 (e.g. "丙寅")
    public String getGanZhi() {
        return stem + branch;
    }

    public String getStem() { return stem; }
    public String getBranch() { return branch; }
    public int getStartAge() { return startAge; }
    public int getStartYear() { return startYear; }
    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }
    public int getEndYear() {
        return endYear;
    }
    public List<String> getLiuNianList() { return liuNianList; }
    public void setLiuNianList(List<String> list) { this.liuNianList = list; }
}