package com.taodev.zhouyi.fourpillars.ui;

import com.taodev.zhouyi.domain.LuckPillar;

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



    // 3. 大运列表 (纯字符串列表，给 Adapter 用)
    // 格式如： "甲戌 (4岁)"
    public List<LuckPillar> luckPillarList;


    // 空构造函数
    public FourPillarsDisplayModel() {}
}