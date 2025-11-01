package com.bhcj.telling.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhcj.telling.R;

/**
 * 头部Fragment
 * 用于显示应用头部信息的简单Fragment
 * 支持通过参数传递数据
 * 
 * @author bhcj
 * @version 1.0
 */
public class HeaderFragment extends Fragment {

    /** 参数1的键名 */
    private static final String ARG_PARAM1 = "param1";
    
    /** 参数2的键名 */
    private static final String ARG_PARAM2 = "param2";

    /** 参数1的值 */
    private String mParam1;
    
    /** 参数2的值 */
    private String mParam2;

    /**
     * 构造函数
     * Fragment需要空的公共构造函数
     */
    public HeaderFragment() {
        // 必需的空构造函数
    }

    /**
     * 创建Fragment实例的工厂方法
     * 使用提供的参数创建Fragment的新实例
     *
     * @param param1 参数1
     * @param param2 参数2
     * @return HeaderFragment的新实例
     */
    public static HeaderFragment newInstance(String param1, String param2) {
        HeaderFragment fragment = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment创建时的回调方法
     * 从参数中获取传递的数据
     * 
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * 创建Fragment视图
     * 填充布局并返回视图
     * 
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的实例状态
     * @return 创建的视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充Fragment的布局
        return inflater.inflate(R.layout.fragment_header, container, false);
    }
}