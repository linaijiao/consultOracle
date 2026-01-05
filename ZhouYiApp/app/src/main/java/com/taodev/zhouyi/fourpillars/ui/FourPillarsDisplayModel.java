package com.taodev.zhouyi.fourpillars.ui;

import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.domain.Pillar;

import java.io.Serializable;
import java.util.List;

public class FourPillarsDisplayModel implements Serializable {
    // 1. 顶部基本描述
    public String basicInfoText;

    // 2. 四柱显示的文本 (e.g. "甲子\n[海中金]")
    public String yearPillarText;
    public String monthPillarText;
    public String dayPillarText;
    public String hourPillarText;

    public List<Pillar> fourPillars;


    // 3. 大运列表
    public List<LuckPillar> luckPillarList;


    // 空构造函数
    public FourPillarsDisplayModel() {}
}