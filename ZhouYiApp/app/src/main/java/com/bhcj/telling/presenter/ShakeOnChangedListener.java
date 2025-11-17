package com.bhcj.telling.presenter;

/**
 * 摇动变化监听器接口
 * 当检测到摇动动作时，通过此接口回调通知监听者
 * 
 * @author bhcj
 * @version 1.0
 */
public interface ShakeOnChangedListener {

    /**
     * 摇动变化回调方法
     * 当检测到摇动动作时调用此方法
     * 
     * @param lastX 最后检测到的X轴加速度值
     * @param lastY 最后检测到的Y轴加速度值
     * @param lastZ 最后检测到的Z轴加速度值
     */
    void onChange(float lastX, float lastY, float lastZ);
}
