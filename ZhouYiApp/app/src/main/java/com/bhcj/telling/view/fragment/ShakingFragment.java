package com.bhcj.telling.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bhcj.telling.R;
import com.bhcj.telling.model.bean.GuaWords;
import com.bhcj.telling.presenter.ShakeListener;
import com.bhcj.telling.presenter.ShakeOnChangedListener;
import com.bhcj.telling.utils.ConstantUtil;
import com.bhcj.telling.utils.GuaUtil;
import com.bhcj.telling.utils.LogUtil;
import com.bhcj.telling.view.activity.ExplainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class ShakingFragment extends Fragment {

    private ShakeListener shakeListener;
    private ImageView animView1;
    private ImageView animView2;
    private ImageView animView3;
    private View mRootView;
    private List<Character> yaoList = Collections.synchronizedList(new ArrayList<Character>());

    public ShakingFragment() {
        // Required empty public constructor
    }


    public static ShakingFragment newInstance(Bundle bundle) {
        ShakingFragment fragment = new ShakingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_shaking, container, false);
        animView1 = mRootView.findViewById(R.id.animView1);
        animView2 = mRootView.findViewById(R.id.animView2);
        animView3 = mRootView.findViewById(R.id.animView3);
        shakeListener = new ShakeListener(getActivity());
        //alertDialog(mRootView);
        shakeListener.setShakeOnChangedListener(new ShakeOnChangedListener() {
            @Override
            public void onChange(float lastX, float lastY, float lastZ) {
                LogUtil.d("Sharking", lastX + "   " + "  " + lastY + "   " + lastZ);
                if (yaoList.size() >= 180) {
                    alertDialog(mRootView);
                } else {
                    ObjectAnimator animator1 = create(animView1, "translationY", lastX, lastY + lastX);
                    ObjectAnimator animator2 = create(animView2, "translationY", lastX, lastY + lastZ);
                    ObjectAnimator animator3 = create(animView3, "translationY", lastY, lastX + lastZ);
                    AnimatorSet set = new AnimatorSet();
                    set.play(animator1).before(animator2).before(animator3);
                    //set.playTogether(animator1,animator2,animator3);
                    set.start();
                }
            }
        });
        ObjectAnimator animator1 = create(animView1, "translationY", 0, 10);
        ObjectAnimator animator2 = create(animView2, "translationY", 0, 10);
        ObjectAnimator animator3 = create(animView3, "translationY", 0, 10);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).before(animator2).before(animator3);
        //set.playTogether(animator1,animator2,animator3);
        set.start();
        alertDialog(mRootView);
        return mRootView;
    }

    public void alertDialog(View v) {
        final View view = getLayoutInflater().inflate(R.layout.gua_dialog, null);
        Map<String, ImageView> viewMap = new HashMap<>(6);
        viewMap.put("0", (ImageView) view.findViewById(R.id.yaoImageView1));
        viewMap.put("1", (ImageView) view.findViewById(R.id.yaoImageView2));
        viewMap.put("2", (ImageView) view.findViewById(R.id.yaoImageView3));
        viewMap.put("3", (ImageView) view.findViewById(R.id.yaoImageView4));
        viewMap.put("4", (ImageView) view.findViewById(R.id.yaoImageView5));
        viewMap.put("5", (ImageView) view.findViewById(R.id.yaoImageView6));

        final StringBuilder idbuf = new StringBuilder(0);
        for (int i = yaoList.size() - 18; i < yaoList.size(); i = i + 3) {
            Character c1 = yaoList.get(i);
            Character c2 = yaoList.get(i + 1);
            Character c3 = yaoList.get(i + 2);
            StringBuilder sb = new StringBuilder(0).append(c1).append(c2).append(c3);
            String identify = GuaUtil.parse(sb.toString());
            idbuf.append(identify);
        }
        yaoList.clear();
        for (int i = 0; i < 6; i++) {
            ImageView imageView = viewMap.get(String.valueOf(i));
            if (GuaUtil.isOne(idbuf.charAt(i))) {
                imageView.setImageResource(R.drawable.guatu1);
            } else {
                imageView.setImageResource(R.drawable.guatu0);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.yuanhuan);
        GuaWords words = GuaUtil.getGuaWords(getActivity().getAssets(), idbuf.toString());
        builder.setTitle(words.getTitle() + " " + words.getName());
        builder.setView(view);
        builder.setPositiveButton("解卦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), ExplainActivity.class);
                intent.putExtra(ConstantUtil.GUA_IDENTIFY, idbuf.toString());
                startActivity(intent);
                // dialog.cancel();
            }
        });
        builder.setNegativeButton("重摇", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }

    private ObjectAnimator create(final ImageView view, String propertyName, float lastX, float lastY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, propertyName, 0, lastX * 100, lastY * 100, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int rand = new Random().nextInt(64);
                if (rand % 2 == 0) {
                    view.setImageResource(R.drawable.back);
                    yaoList.add(GuaUtil.BACK);
                } else {
                    view.setImageResource(R.drawable.front);
                    yaoList.add(GuaUtil.FRONT);
                }
            }
        });
        return animator;
    }

    @Override
    public void onResume() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (shakeListener != null) {
            shakeListener.start();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (shakeListener != null) {
            shakeListener.stop();
        }
        super.onPause();
    }

}