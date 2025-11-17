package com.bhcj.telling.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bhcj.telling.R;
import com.bhcj.telling.model.bean.GuaWords;
import com.bhcj.telling.view.activity.ExplainActivity;
import com.bhcj.telling.utils.ConstantUtil;
import com.bhcj.telling.utils.GuaUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 解卦Fragment
 * 提供手动选择爻象进行解卦的功能
 * 用户可以通过下拉选择器选择每个爻的状态，系统会实时计算并显示对应的卦象
 * 
 * @author bhcj
 * @version 1.0
 */
public class ResolvingFragment extends Fragment {

    /** 根视图 */
    private View mRootView;
    
    /** 卦象名称显示视图 */
    private TextView guaNameView;
    
    /** 卦象简要信息显示视图 */
    private TextView briefView;
    
    /** 卦象简要信息最后一行显示视图 */
    private TextView guaBriefLastLineView;
    
    /** 跳转链接视图 */
    private TextView linkView;
    
    /** 选择映射表，存储每个爻的选择结果 */
    private Map<Integer, String> selectMap = new LinkedHashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_resolving);
        mRootView = inflater.inflate(R.layout.activity_resolving, container, false);
        guaNameView = mRootView.findViewById(R.id.guaName);
        briefView = mRootView.findViewById(R.id.guaBrief);
        guaBriefLastLineView = mRootView.findViewById(R.id.guaBriefLastLine);
        load(R.id.yaoSelect1, R.id.boImageView1);
        load(R.id.yaoSelect2, R.id.boImageView2);
        load(R.id.yaoSelect3, R.id.boImageView3);
        load(R.id.yaoSelect4, R.id.boImageView4);
        load(R.id.yaoSelect5, R.id.boImageView5);
        load(R.id.yaoSelect6, R.id.boImageView6);

       linkView = mRootView.findViewById(R.id.linkView);
        linkView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExplainActivity.class);
                intent.putExtra(ConstantUtil.GUA_IDENTIFY, getIdentity());
                startActivity(intent);
            }
        });
        return mRootView;
    }

    private void load(int yaoSelect1, int imageView) {
        Spinner spinner = mRootView.findViewById(yaoSelect1);
        final ImageView boImageView = mRootView.findViewById(imageView);
        selectMap.put(boImageView.getId(), GuaUtil.ONE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] options = getResources().getStringArray(R.array.subjects);
                String flag = GuaUtil.parse(options[pos]);
                selectMap.put(boImageView.getId(), flag);
                if (GuaUtil.ONE.equals(flag)) {
                    boImageView.setImageResource(R.drawable.guatu1);
                } else {
                    boImageView.setImageResource(R.drawable.guatu0);
                }
                GuaWords words = GuaUtil.getGuaWords(getActivity().getAssets(), getIdentity());
                guaNameView.setText("本卦 第" + words.getTitle() + words.getName());
                briefView.setText(words.getBrief());

                Layout layout = briefView.getLayout();
                int lines = briefView.getLineCount();
                StringBuilder textBf = new StringBuilder(briefView.getText().toString());
                String lastLine = textBf.subSequence(layout.getLineStart(lines - 1), layout.getLineEnd(lines - 1)).toString();//第二行;
                String textLine = textBf.substring(0, textBf.indexOf(lastLine));
                briefView.setText(textLine);
                if(lastLine.length() > 15){
                    lastLine = lastLine.substring(0,15)+"...";
                }
                guaBriefLastLineView.setText(lastLine);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private String getIdentity() {
        StringBuilder guaCode = new StringBuilder();
        for (Map.Entry<Integer, String> entry : selectMap.entrySet()) {
            guaCode.append(entry.getValue());
        }
        return guaCode.toString();
    }

    public static ResolvingFragment newInstance(Bundle bundle) {
        ResolvingFragment fragment = new ResolvingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}