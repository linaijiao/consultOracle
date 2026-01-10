package com.taodev.zhouyi.fourpillars.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.taodev.zhouyi.R;
import com.taodev.zhouyi.domain.LuckPillar;
import com.taodev.zhouyi.fourpillars.ui.FourPillarsDisplayModel;

import java.util.ArrayList;
import java.util.List;

public class LuckPillarAdapter extends RecyclerView.Adapter<LuckPillarAdapter.ViewHolder> {

    // 1. 数据源：这是要显示的大运列表
    private List<FourPillarsDisplayModel.LuckPillarUiModel> luckPillars = new ArrayList<>();
    // 2. 构造方法 (初始化用)
    public LuckPillarAdapter(List<FourPillarsDisplayModel.LuckPillarUiModel> data) {
        this.luckPillars = data;
    }
    // 2. 更新数据的方法 (给 Activity 调用的)
    public void setLuckPillars(List<FourPillarsDisplayModel.LuckPillarUiModel> newData) {
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
        FourPillarsDisplayModel.LuckPillarUiModel item = luckPillars.get(position);

        // 在 Converter 里把数据拼成了 "甲戌\n4岁"，这里简单拆一下或者直接显示
        // 假设传入的是简单的字符串，实际项目中可以传对象
        // 2. 绑定数据 (根据你的 LuckPillar 字段)
        // 大运名字 (如: 戊寅)
        // 假设 XML 里叫 tv_luck_stem_branch，如果不确定，去 XML 确认一下 ID
        if (holder.tvTenGod != null) {
            holder.tvTenGod.setText(item.getTenGod());
        }
        if (holder.tvStemBranch != null) {
            holder.tvStemBranch.setText(item.getPillarName());
        }
        if (holder.tvLifeStage != null) {
            holder.tvLifeStage.setText(item.getLifeStage());
        }
        // 起运虚岁 (XML ID: tv_col_age)
        if (holder.tvAge != null) {
            holder.tvAge.setText(String.valueOf(item.getAgeInfo()));
        }
        // 起运年份 (XML ID: tv_col_start_year)
        if (holder.tvStartYear != null) {
            holder.tvStartYear.setText(String.valueOf(item.getStartYear()));
        }
        // 绑定中间的 10 行干支
        holder.tvYearlyLuckList.setText(item.yearlyLuckListString);
        // 绑定底部年份
        holder.tvEndYear.setText(String.valueOf(item.endYear));
    }

    @Override
    public int getItemCount() {
        int size = luckPillars == null ? 0 : luckPillars.size();
        // 告诉列表一共有多少个大运
        Log.d("DEBUG_ADAPTER", "大运列表数量: " + size);
        return size;
    }

    // 内部类：拿着界面上的控件引用
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenGod;
        TextView tvStemBranch;
        TextView tvLifeStage;
        TextView tvAge;
        TextView tvStartYear;
        TextView tvYearlyLuckList;
        TextView tvEndYear;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTenGod = itemView.findViewById(R.id.tv_col_luck);
            tvStemBranch = itemView.findViewById(R.id.tv_luck_stem_branch);
            tvLifeStage = itemView.findViewById(R.id.tv_luck_lifestage);
            tvAge = itemView.findViewById(R.id.tv_luck_age);
            tvStartYear = itemView.findViewById(R.id.tv_col_start_year);
            tvYearlyLuckList = itemView.findViewById(R.id.tv_yearly_luck_list);
            tvEndYear = itemView.findViewById(R.id.tv_col_end_year);


        }
    }
}