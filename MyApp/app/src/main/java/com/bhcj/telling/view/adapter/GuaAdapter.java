package com.bhcj.telling.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bhcj.telling.R;
import com.bhcj.telling.model.bean.GuaWords;

import java.util.List;

/**
 * 卦象条目适配器
 * 用于在ListView中显示卦象解释条目
 * 继承自ArrayAdapter，管理GuaWords.Entity对象列表
 * 
 * @author bhcj
 * @version 1.0
 */
public class GuaAdapter extends ArrayAdapter<GuaWords.Entity> {
    
    /**
     * 构造函数
     * 
     * @param context 上下文对象
     * @param resource 布局资源ID
     * @param objects 数据对象列表
     */
    public GuaAdapter(@NonNull Context context, int resource, @NonNull List<GuaWords.Entity> objects) {
        super(context, resource, objects);
    }

    /**
     * 获取指定位置的视图
     * 创建或复用视图，并绑定数据
     * 
     * @param position 位置索引
     * @param convertView 可复用的视图
     * @param parent 父视图组
     * @return 配置好的视图
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GuaWords.Entity entity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.gua_item, parent, false);
        TextView name = view.findViewById(R.id.entity_name);
        TextView text = view.findViewById(R.id.entity_text);
        name.setText(entity.getName() + "：");
        text.setText(entity.getText());
        return view;
    }
}
