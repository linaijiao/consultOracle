package com.bhcj.telling.utils;

import android.util.Log;

/**
 * 日志工具类
 * 提供统一的日志输出功能，支持调试模式控制
 * 
 * @author bhcj
 * @version 1.0
 */
public class LogUtil {
    
    /** 调试模式标识，控制是否输出日志 */
    private static final boolean DEBUG = true;

    /**
     * 输出调试日志
     * 只有在调试模式下才会输出日志信息
     * 
     * @param tag 日志标签，用于标识日志来源
     * @param message 日志消息内容
     */
    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }
}
