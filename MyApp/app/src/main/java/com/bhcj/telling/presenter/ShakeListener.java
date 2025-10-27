package com.bhcj.telling.presenter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 摇动检测监听器
 * 通过加速度传感器检测手机摇动动作
 * 实现SensorEventListener接口，监听传感器数据变化
 * 
 * @author bhcj
 * @version 1.0
 */
public class ShakeListener implements SensorEventListener {

    /** 传感器管理器 */
    private SensorManager sensorManager;
    
    /** 加速度传感器 */
    private Sensor sensor;

    /** 时间间隔阈值（毫秒） */
    private long timeInterval = 10;
    
    /** 上次检测时间 */
    private long lastTime = 100;
    
    /** 上次X轴加速度值 */
    private float lastX = 0;
    
    /** 上次Y轴加速度值 */
    private float lastY = 0;
    
    /** 上次Z轴加速度值 */
    private float lastZ = 0;
    
    /** 是否已初始化位置 */
    private boolean isInitPosition = false;
    
    /** 摇动加速度阈值 */
    private double acceleration = 1000;
    
    /** 摇动变化监听器 */
    private ShakeOnChangedListener shakeOnChangedListener;
    
    /** 上下文对象 */
    private Context context;

    /**
     * 构造函数
     * 
     * @param c 上下文对象
     */
    public ShakeListener(Context c) {
        context = c;
    }

    /**
     * 设置摇动变化监听器
     * 
     * @param shakeOnChangedListener 摇动变化监听器
     */
    public void setShakeOnChangedListener(ShakeOnChangedListener shakeOnChangedListener) {
        this.shakeOnChangedListener = shakeOnChangedListener;
    }

    /**
     * 开始监听摇动
     * 注册加速度传感器监听器
     */
    public void start() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * 停止监听摇动
     * 注销传感器监听器
     */
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    /**
     * 传感器数据变化回调方法
     * 检测摇动动作并触发相应的回调
     * 
     * @param event 传感器事件对象
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            long currentTime = System.currentTimeMillis();
            if (timeInterval > currentTime - lastTime) {
                return; // 如果两次回调间隔过小，直接忽略
            }
            
            // 读取传感器监听到的加速度值 m/(s^2)
            float[] values = event.values;
            if (!isInitPosition) {
                // 手机的状态不同，其x,y,z的值不同，重新对x,y,z赋值
                lastX = values[0];
                lastY = values[1];
                lastZ = values[2];
                // 仅在初始化时赋值
                isInitPosition = true;
            }
            float x = values[0];
            float y = values[1];
            float z = values[2];

            // 计算和上次的变化值
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            // 更新变化值
            lastX = x;
            lastY = y;
            lastZ = z;
            
            // 计算摇动灵敏度 m/(s^3)
            double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval) * 1000;
            if (speed > acceleration) {
                if (shakeOnChangedListener != null) {
                    shakeOnChangedListener.onChange(lastX, lastY, lastZ);
                }
                lastTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传感器精度变化回调方法
     * 当前实现为空，可根据需要添加精度变化处理逻辑
     * 
     * @param sensor 传感器对象
     * @param accuracy 精度值
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 当前不需要处理精度变化
    }
}
