package com.bhcj.telling.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.bhcj.telling.model.bean.GuaWords;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 卦象工具类
 * 提供卦象相关的计算和解析功能
 * 包括硬币投掷结果的解析、卦象数据的获取和缓存
 * 
 * @author bhcj
 * @version 1.0
 */
public class GuaUtil {

    /** 卦象数据缓存Map，避免重复加载相同卦象数据 */
    private static Map<String, GuaWords> guaWordsMap = new ConcurrentHashMap<>();
    
    /** 硬币正面标识符 */
    public static final char FRONT = '正';
    
    /** 硬币反面标识符 */
    public static final char BACK = '反';
    
    /** 阳爻标识符 */
    public static final String ONE = "1";
    
    /** 阴爻标识符 */
    public static final String ZERO = "0";

    /**
     * 判断字符串解析结果是否为阳爻
     * 
     * @param bo 硬币投掷结果字符串
     * @return true表示阳爻，false表示阴爻
     */
    public static boolean isOne(String bo) {
        return ONE.equals(parse(bo));
    }

    /**
     * 判断字符是否为阳爻标识符
     * 
     * @param c 字符
     * @return true表示是阳爻标识符，false表示不是
     */
    public static boolean isOne(Character c) {
        return ONE.equals(String.valueOf(c));
    }

    /**
     * 解析硬币投掷结果字符串
     * 根据正面（'正'）的数量判断爻的类型
     * 正面数量为偶数时返回阳爻（"1"），奇数时返回阴爻（"0"）
     * 
     * @param bo 硬币投掷结果字符串，包含'正'和'反'字符
     * @return "1"表示阳爻，"0"表示阴爻
     */
    public static String parse(String bo) {
        int count = 0;
        for (char character : bo.toCharArray()) {
            if (FRONT == character) {
                count++;
            }
        }
        if (count % 2 == 0) {
            return "1";
        }
        return "0";
    }

    /**
     * 获取卦象解析数据
     * 先从缓存中查找，如果不存在则从assets目录加载JSON文件并缓存
     * 
     * @param assetManager 资源管理器
     * @param identity 卦象标识符
     * @return 卦象解析数据对象
     */
    public static GuaWords getGuaWords(AssetManager assetManager, String identity) {
        GuaWords guaWords = guaWordsMap.get(identity);
        if (null != guaWords) { // 已加载过，直接返回缓存数据
            return guaWords;
        }
        // 未加载过，从assets目录加载JSON文件
        String jsonString = FileUtils.readJsonFile(assetManager, identity);
        GuaWords words = JSON.parseObject(jsonString, GuaWords.class);
        guaWordsMap.put(identity, words); // 缓存数据
        return words;
    }

}
