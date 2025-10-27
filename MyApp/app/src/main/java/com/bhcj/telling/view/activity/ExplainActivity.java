package com.bhcj.telling.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bhcj.telling.R;
import com.bhcj.telling.view.adapter.GuaAdapter;
import com.bhcj.telling.view.layout.TitleLayout;
import com.bhcj.telling.utils.ConstantUtil;
import com.bhcj.telling.utils.GuaUtil;
import com.bhcj.telling.model.bean.GuaWords;

import java.util.ArrayList;
import java.util.List;

/**
 * 卦象解释详情Activity
 * 显示卦象的详细解释信息，包括卦象名称、解释、象辞、象曰等
 * 通过Intent传递卦象标识符来获取对应的卦象数据
 * 
 * @author bhcj
 * @version 1.0
 */
public class ExplainActivity extends BaseActivity {

    /** 标题布局组件 */
    private TitleLayout titleLayout;
    
    /** 内容面板 */
    private LinearLayout contentPanel;
    
    /** 标题文本视图 */
    private TextView titleView;
    
    /** 解释文本视图 */
    private TextView explainView;
    
    /** 象曰文本视图 */
    private TextView xyView;
    
    /** 结果文本视图 */
    private TextView jgView;
    
    /** 列表视图 */
    private ListView listView;

    /**
     * Activity创建时的回调方法
     * 初始化界面并显示卦象详细信息
     * 
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        
        // 获取传递的卦象标识符
        Intent intent = getIntent();
        String guaIdentify = intent.getStringExtra(ConstantUtil.GUA_IDENTIFY);
        GuaWords words = GuaUtil.getGuaWords(getAssets(), guaIdentify);

        // 初始化标题布局
        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle(words.getName());

        // 设置标题文本
        titleView = findViewById(R.id.titleView);
        titleView.setText(words.getFullTitle());

        // 设置解释文本
        explainView = findViewById(R.id.explain);
        explainView.setText(words.getAllExplain());

        // 设置象曰文本
        xyView = findViewById(R.id.xiangyue);
        xyView.setText(words.getxYue());

        // 设置结果文本
        jgView = findViewById(R.id.jgView);
        jgView.setText(words.getDsExplain());

        // 设置列表视图
        listView = findViewById(R.id.listView);
        List<GuaWords.Entity> list = new ArrayList<>();
        list.addAll(words.getExplainList());
        list.addAll(words.getJgList());
        GuaAdapter guaAdapter = new GuaAdapter(ExplainActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(guaAdapter);
    }


}