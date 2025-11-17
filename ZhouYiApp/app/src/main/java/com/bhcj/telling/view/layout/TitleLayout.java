package com.bhcj.telling.view.layout;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bhcj.telling.R;

/**
 * 自定义标题栏布局
 * 提供统一的标题栏样式，包含返回按钮和标题文本
 * 继承自FrameLayout，支持动态加载布局文件
 * 
 * @author bhcj
 * @version 1.0
 */
public class TitleLayout extends FrameLayout {
    
    /** 标题文本视图 */
    TextView tvTitle;
    
    /** 返回按钮 */
    Button titleBack;

    /**
     * 构造函数
     * 初始化标题栏布局和组件
     * 
     * @param context 上下文对象
     * @param attrs 属性集合
     */
    public TitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 动态加载布局文件
        LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        // 绑定对应组件
        titleBack = findViewById(R.id.titleBack);
        tvTitle = findViewById(R.id.tvTitle);
        // 绑定返回按钮的点击事件
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
    }

    /**
     * 设置标题文字
     * 
     * @param title 标题文本
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }
    
    /**
     * 设置返回按钮是否显示
     * 
     * @param b true显示返回按钮，false隐藏返回按钮
     */
    public void setBack(Boolean b) {
        if (!b) titleBack.setVisibility(View.GONE);
        else titleBack.setVisibility(View.VISIBLE);
    }
}

