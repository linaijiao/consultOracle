package com.taodev.zhouyi.core.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taodev.zhouyi.domain.BaziRules;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

public class BaziDataLoader {
    private static BaziDataLoader instance;
    private Context context;

    // 缓存变量
    private BaziRules baziRulesCache;
    // 节气数据缓存
    private Map<String, Map<String, String>> jieQiCache;

    // 单例获取
    public static synchronized BaziDataLoader getInstance(Context context) {
        if (instance == null) {
            instance = new BaziDataLoader(context.getApplicationContext());
        }
        return instance;
    }

    private BaziDataLoader(Context context) {
        this.context = context;
        // 在构造时一次性加载所有数据
        initData();
    }

    private void initData() {
        // 1. 加载规则 (利用泛型方法)
        baziRulesCache = loadJsonFromAsset("bazi/bazirules.json", BaziRules.class);

        // 2. 加载节气 (利用的泛型方法)
        Type jieQiType = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
        jieQiCache = loadJsonFromAsset("jieqi/jieqi_data.json", jieQiType);
    }

    // --- 通用方法---
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
            // Gson 建议也做成全局静态单例，或者这里 new 也行
            T result = new Gson().fromJson(new InputStreamReader(is), typeOfT);
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BaziRules getBaziRules() { return baziRulesCache; }
    public Map<String, Map<String, String>> getJieQiData() { return jieQiCache; }
}