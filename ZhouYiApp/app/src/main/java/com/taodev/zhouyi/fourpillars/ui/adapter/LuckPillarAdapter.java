package com.taodev.zhouyi.fourpillars.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.taodev.zhouyi.R;
import com.taodev.zhouyi.domain.LuckPillar;

import java.util.ArrayList;
import java.util.List;

public class LuckPillarAdapter extends RecyclerView.Adapter<LuckPillarAdapter.ViewHolder> {

    // 1. 数据源：这是要显示的大运列表
    private List<LuckPillar> luckPillars = new ArrayList<>();
    // 2. 构造方法 (初始化用)
    public LuckPillarAdapter(List<LuckPillar> data) {
        this.luckPillars = data;
    }
    // 2. 更新数据的方法 (给 Activity 调用的)
    public void setLuckPillars(List<LuckPillar> newData) {
        this.luckPillars = newData;
        // 通知界面刷新
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3. 创建视图：加载要显示的 xml 布局
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_luck_pillar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 4. 绑定数据：把 List 里的第 position 个数据，填到界面上
        LuckPillar item = luckPillars.get(position);

        // 在 Converter 里把数据拼成了 "甲戌\n4岁"，这里简单拆一下或者直接显示
        // 假设传入的是简单的字符串，实际项目中可以传对象
        // 2. 绑定数据 (根据你的 LuckPillar 字段)
        // 大运名字 (如: 戊寅)
        // 假设 XML 里叫 tv_luck_stem_branch，如果不确定，去 XML 确认一下 ID
        if (holder.tvStemBranch != null) {
            holder.tvStemBranch.setText(item.getStem() + item.getBranch());
        }
        // 起运虚岁 (XML ID: tv_col_age)
        if (holder.tvAge != null) {
            holder.tvAge.setText(String.valueOf(item.getStartAge()));
        }
        // 起运年份 (XML ID: tv_col_start_year)
        if (holder.tvStartYear != null) {
            holder.tvStartYear.setText(String.valueOf(item.getStartYear()));
        }
    }

    @Override
    public int getItemCount() {
        // 告诉列表一共有多少个大运
        return luckPillars == null ? 0 : luckPillars.size();
    }

    // 内部类：拿着界面上的控件引用
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStemBranch;
        TextView tvAge;
        TextView tvStartYear;

        public ViewHolder(View itemView) {
            super(itemView);
            tvStemBranch = itemView.findViewById(R.id.tv_luck_stem_branch);
            tvAge = itemView.findViewById(R.id.tv_luck_age);
            tvStartYear = itemView.findViewById(R.id.tv_col_start_year);

        }
    }
}