package com.bhcj.telling.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 基础Activity类
 * 所有Activity的基类，提供通用的Activity功能
 * 继承自AppCompatActivity，支持Material Design主题
 * 
 * @author bhcj
 * @version 1.0
 */
public class BaseActivity extends AppCompatActivity {
    
    /**
     * Activity创建时的回调方法
     * 子类可以重写此方法进行特定的初始化操作
     * 
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 子类可以在此处添加通用的初始化逻辑
    }
}