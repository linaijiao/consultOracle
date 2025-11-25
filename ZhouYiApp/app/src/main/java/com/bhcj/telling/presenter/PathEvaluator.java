package com.bhcj.telling.presenter;

import android.animation.TypeEvaluator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

/**
 * 路径评估器
 * 实现TypeEvaluator接口，用于在指定路径上计算动画插值点
 * 主要用于硬币动画的路径计算
 * 
 * @author bhcj
 * @version 1.0
 */
public class PathEvaluator implements TypeEvaluator<PointF> {

    /** 动画路径对象 */
    private Path mPath;

    /**
     * 构造函数
     * 
     * @param path 动画路径
     */
    public PathEvaluator(Path path) {
        mPath = path;
    }

    /**
     * 计算路径上指定位置的坐标点
     * 
     * @param fraction 动画进度（0.0-1.0）
     * @param startValue 起始值（未使用）
     * @param endValue 结束值（未使用）
     * @return 路径上对应位置的坐标点
     */
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        float[] point = new float[2];
        pathMeasure.getPosTan(pathMeasure.getLength() * fraction, point, null);
        return new PointF(point[0], point[1]);
    }
}


