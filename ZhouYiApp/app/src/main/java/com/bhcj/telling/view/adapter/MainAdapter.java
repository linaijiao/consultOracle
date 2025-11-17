package com.bhcj.telling.view.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bhcj.telling.view.fragment.GuaTuListFragment;
import com.bhcj.telling.view.fragment.ResolvingFragment;
import com.bhcj.telling.view.fragment.ShakingFragment;
import com.bhcj.telling.view.fragment.ThrowCoinsFragment;

/**
 * 主界面ViewPager2适配器
 * 管理主界面的三个Fragment页面：投币占卜、卦图列表、解卦结果
 * 继承自FragmentStateAdapter，支持Fragment的状态保存和恢复
 * 
 * @author bhcj
 * @version 1.0
 */
public class MainAdapter extends FragmentStateAdapter {
    
    /** 解卦结果Fragment */
    private ResolvingFragment resolvingFragment = ResolvingFragment.newInstance(new Bundle());
    
    /** 卦图列表Fragment */
    private GuaTuListFragment guaTuListFragment = GuaTuListFragment.newInstance(new Bundle());
    
    /** 摇卦Fragment */
    private ShakingFragment shakingFragment = ShakingFragment.newInstance(new Bundle());
    
    /** 投币占卜Fragment */
    private ThrowCoinsFragment throwCoinsFragment = ThrowCoinsFragment.newInstance(new Bundle());

    /**
     * 构造函数
     * 
     * @param fragmentActivity FragmentActivity实例
     */
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * 获取Fragment数量
     * 
     * @return Fragment数量，当前为3个
     */
    public int getItemCount() {
        return 3;
    }

    /**
     * 根据位置创建对应的Fragment
     * 
     * @param position Fragment位置
     * @return 对应位置的Fragment实例
     */
    @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // 投币占卜页面
                return throwCoinsFragment;
            case 1: // 卦图列表页面
                return guaTuListFragment;
            case 2: // 解卦结果页面
                return resolvingFragment;
        }
        return null;
    }
}
