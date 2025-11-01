package com.bhcj.telling.utils;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件工具类
 * 提供文件读取相关功能，主要用于从assets目录读取JSON文件
 * 
 * @author bhcj
 * @version 1.0
 */
public class FileUtils {

    /** JSON文件后缀名 */
    private static final String JSON_SUFFIX = ".json";

    /**
     * 从assets目录读取JSON文件内容
     * 根据卦象标识符读取对应的JSON文件，文件名对应卦码
     * 
     * @param assetManager 资源管理器
     * @param fileName 文件名（不包含后缀）
     * @return JSON文件内容字符串
     * @throws RuntimeException 当文件读取失败时抛出运行时异常
     */
    public static String readJsonFile(AssetManager assetManager, String fileName) {
        StringBuilder jsonBuf = new StringBuilder(0);
        String jsonFileName = getJsonFileName(fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(jsonFileName)))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                jsonBuf.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonBuf.toString();
    }

    /**
     * 获取完整的JSON文件名
     * 在文件名后添加.json后缀
     * 
     * @param fileName 原始文件名
     * @return 完整的JSON文件名
     */
    private static String getJsonFileName(String fileName) {
        return fileName + JSON_SUFFIX;
    }
}
