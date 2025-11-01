package com.bhcj.telling.view.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.viewpager2.widget.ViewPager2;

import com.bhcj.telling.R;
import com.bhcj.telling.view.adapter.MainAdapter;

/**
 * 主界面Activity
 * 负责管理应用的主要界面，包括三个Tab页面：占卜、卦图列表、解卦
 * 使用ViewPager2和RadioGroup实现页面切换功能
 * 
 * @author bhcj
 * @version 1.0
 */
public class MainActivity extends BaseActivity {

    /** ViewPager2控件，用于页面滑动切换 */
    private ViewPager2 viewPager;
    
    /** RadioGroup控件，包含底部导航按钮组 */
    private RadioGroup radioGroup;
    
    /** 占卜按钮 */
    private RadioButton divineBtn;
    
    /** 卦图按钮 */
    private RadioButton diagramsBtn;
    
    /** 解卦按钮 */
    private RadioButton jgBtn;

    /**
     * Activity创建时的回调方法
     * 设置布局文件并初始化视图组件
     * 
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置主界面布局
        initView(); // 初始化视图组件
    }

    /**
     * 初始化视图组件
     * 设置ViewPager2适配器，配置页面切换监听器和底部导航按钮点击事件
     */
    private void initView() {
        // 初始化控件
        viewPager = findViewById(R.id.viewpager);
        radioGroup = findViewById(R.id.layout_tab);

        divineBtn = findViewById(R.id.divine);
        diagramsBtn = findViewById(R.id.diagrams);
        jgBtn = findViewById(R.id.jgBut);

        // 创建并设置ViewPager2适配器
        MainAdapter adapter = new MainAdapter(this);
        
        // 注册页面切换监听器，当页面切换时同步更新底部导航按钮状态
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // 根据当前页面位置更新对应的RadioButton选中状态
                switch (position) {
                    case 0: // 占卜页面
                        divineBtn.setChecked(true);
                        diagramsBtn.setChecked(false);
                        jgBtn.setChecked(false);
                        break;
                    case 1: // 卦图列表页面
                        divineBtn.setChecked(false);
                        diagramsBtn.setChecked(true);
                        jgBtn.setChecked(false);
                        break;
                    case 2: // 解卦页面
                        divineBtn.setChecked(false);
                        diagramsBtn.setChecked(false);
                        jgBtn.setChecked(true);
                        break;
                }
            }
        });
        
        // 设置ViewPager2适配器
        viewPager.setAdapter(adapter);

        // 设置底部导航按钮点击监听器，当按钮被点击时切换到对应页面
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
//                switch (id) {
//                    case R.id.divine: // 占卜按钮被点击
//                        viewPager.setCurrentItem(0);
//                        break;
//                    case R.id.diagrams: // 卦图按钮被点击
//                        viewPager.setCurrentItem(1);
//                        break;
//                    case R.id.jgBut: // 解卦按钮被点击
//                        viewPager.setCurrentItem(2);
//                        break;
//                }
                if (id==R.id.divine) {
                    viewPager.setCurrentItem(0);
                }else if(id==R.id.diagrams){
                    viewPager.setCurrentItem(1);
                }else{
                    viewPager.setCurrentItem(2);

                }
            }
        });
    }

}