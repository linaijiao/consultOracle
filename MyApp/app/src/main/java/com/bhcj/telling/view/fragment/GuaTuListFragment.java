package com.bhcj.telling.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bhcj.telling.R;
import com.bhcj.telling.view.activity.ExplainActivity;
import com.bhcj.telling.utils.ConstantUtil;

/**
 * 卦图列表Fragment
 * 使用WebView显示卦图列表页面，支持JavaScript交互
 * 通过WebView加载HTML页面，实现卦图展示和点击跳转功能
 * 
 * @author bhcj
 * @version 1.0
 */
public class GuaTuListFragment extends Fragment {

    /** 根视图 */
    private View mRootView;
    
    /** WebView组件，用于显示卦图列表 */
    private WebView webView;

    /**
     * 创建Fragment视图
     * 初始化WebView并加载卦图列表页面
     * 
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的实例状态
     * @return 创建的视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_gua_tu_list, container, false);
        webView = mRootView.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用JavaScript支持
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/html/gua.html");
        webView.addJavascriptInterface(new WebAppInterface(), "gua");
        return mRootView;
    }

    /**
     * WebView与JavaScript交互接口
     * 提供JavaScript调用Android方法的功能
     */
    public class WebAppInterface {
        
        /**
         * 构造函数
         */
        public WebAppInterface() {
            // 空构造函数
        }
        
        /**
         * JavaScript调用的方法
         * 当用户在WebView中点击卦图时，通过此方法跳转到详情页面
         * 
         * @param key 卦象标识符
         */
        @JavascriptInterface
        public void myMethod(String key) {
            // 创建Intent跳转到卦象解释页面
            Intent intent = new Intent(getActivity(), ExplainActivity.class);
            intent.putExtra(ConstantUtil.GUA_IDENTIFY, key);
            startActivity(intent);
        }
    }

    /**
     * 创建Fragment实例的工厂方法
     * 
     * @param bundle 参数Bundle
     * @return Fragment实例
     */
    public static GuaTuListFragment newInstance(Bundle bundle) {
        GuaTuListFragment fragment = new GuaTuListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}