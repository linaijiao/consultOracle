package com.bhcj.telling.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.bhcj.telling.R;
import com.bhcj.telling.model.bean.GuaWords;
import com.bhcj.telling.presenter.ShakeListener;
import com.bhcj.telling.presenter.ShakeOnChangedListener;
import com.bhcj.telling.utils.ConstantUtil;
import com.bhcj.telling.utils.GuaUtil;
import com.bhcj.telling.utils.LogUtil;
import com.bhcj.telling.view.activity.ExplainActivity;
import com.bhcj.telling.view.view.ThrowCoinsView;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class ThrowCoinsFragment extends Fragment {

    public static ThrowCoinsFragment newInstance(Bundle bundle) {
        ThrowCoinsFragment fragment = new ThrowCoinsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private int rowNumber = 6;

    private boolean isThrowing = false;
    private Vec2 shakeImpulse;

    private ShakeListener shakeListener;

    private final ArrayList<ThrowCoinsView> coinWorlds = new ArrayList<>();

    private final ArrayList<ThrowCoinsView> coinThrowingView = new ArrayList<>();
    private final ArrayList<String> coinResult = new ArrayList<>();

    ArrayList<ImageView> coinResultView = new ArrayList<>();

    private final ThrowCoinsView.ThrowDoneCallback throwDoneCallback = new ThrowCoinsView.ThrowDoneCallback() {
        @Override
        public void onThrowDone(ThrowCoinsView view, int[] result) {
            coinThrowingView.remove(view);
            StringBuilder sb = new StringBuilder(0);
            for (int element : result) {
                sb.append(element == 1 ? GuaUtil.FRONT : GuaUtil.BACK);
            }
            String identify = GuaUtil.parse(sb.toString());
            coinResult.add(identify);
            updateCoinResultView(coinResultView);
            throwNext();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_throw_coins, container, true);
        configResultView(view);
        configCoinWorld(view);
        view.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        shakeListener = new ShakeListener(getContext());
        //alertDialog(mRootView);
        shakeListener.setShakeOnChangedListener(new ShakeOnChangedListener() {
            @Override
            public void onChange(float lastX, float lastY, float lastZ) {
                LogUtil.d("Sharking", lastX + "   " + "  " + lastY + "   " + lastZ);
                shakeImpulse = new Vec2(lastX*100, lastY*100);
                if (isThrowing == false) {
                    start();
                } else {
                    for (ThrowCoinsView coinWorld : coinWorlds) {
                        if (coinWorld.getISThrowing()) {
                            coinWorld.throwCoins(shakeImpulse);
                            return;
                        }
                    }
                }
            }
        });
        return view;
    }

    void configResultView(View view) {
        FrameLayout rootLayout = view.findViewById(R.id.guaContainer);
        View resultView = coinsResultView();
        coinResultView = coinResultItems(resultView);
        updateCoinResultView(coinResultView);
        rootLayout.addView(resultView);
    }

    void configCoinWorld(View view) {
//        ThrowCoinsView coin = view.findViewById(R.id.throwCoinsView);
//        coinWorlds.add(coin);
        // 获取ThrowCoinsFragment的根布局
        FrameLayout rootLayout = view.findViewById(R.id.coinContainer);

        for (int i = 0; i < rowNumber; i++) {
            ThrowCoinsView coinView = new ThrowCoinsView(requireContext());
            coinWorlds.add(coinView);
        }

        for (int i = 0; i < coinWorlds.size(); i++) {
            ThrowCoinsView coinView = coinWorlds.get(coinWorlds.size() - 1 - i);
            coinView.setThrowDoneCallback(throwDoneCallback);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            layoutParams.leftMargin = i * 10;
            coinView.setLayoutParams(layoutParams);
            rootLayout.addView(coinView);
        }
    }

    private void reset() {
        coinThrowingView.clear();
        coinResult.clear();
        for (ThrowCoinsView coinWorld : coinWorlds) {
            coinWorld.stop();
            coinWorld.reset();
        }
        updateCoinResultView(coinResultView);
    }

    private void start() {
        reset();
        isThrowing = true;
        coinThrowingView.addAll(coinWorlds);
        throwNext();
    }

    private void throwNext() {
        if (coinThrowingView.size() == 0) {
            throwDone();
            stop();
            return;
        }
        ThrowCoinsView coinView = coinThrowingView.iterator().next();
        coinView.start(shakeImpulse);
    }

    private void throwDone() {
        Log.d("coinResult", coinResult.toString());
        isThrowing = false;
        alertDialog(getView());
        reset();
    }

    void updateCoinResultView(ArrayList<ImageView> coinResultView) {
        for (int i = 0; i < coinResultView.size(); i++) {
            ImageView imageView = coinResultView.get(i);
            if (coinResult.size() > i) {
                if (coinResult.get(i).equals("1")) {
                    imageView.setImageResource(R.drawable.guatu1);
                } else {
                    imageView.setImageResource(R.drawable.guatu0);
                }
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void alertDialog(View v) {
        String id = String.join("", coinResult);
        View view = coinsResultView();
        view.setAlpha(1f);
        updateCoinResultView(coinResultItems(view));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.yuanhuan);
        GuaWords words = GuaUtil.getGuaWords(getActivity().getAssets(), id);
        builder.setTitle(words.getTitle() + " " + words.getName());
        builder.setView(view);
        builder.setPositiveButton("解卦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), ExplainActivity.class);
                intent.putExtra(ConstantUtil.GUA_IDENTIFY, id);
                startActivity(intent);
                // dialog.cancel();
                reset();
            }
        });
        builder.setNegativeButton("重摇", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                reset();
            }
        });
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }

    View coinsResultView() {
        View view = getLayoutInflater().inflate(R.layout.gua_dialog, null);
        view.setAlpha(0.5f);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(layoutParams);
        return view;
    }

    ArrayList<ImageView> coinResultItems(View view) {
        ArrayList<ImageView> coinResultView = new ArrayList<>();
        coinResultView.add(view.findViewById(R.id.yaoImageView1));
        coinResultView.add(view.findViewById(R.id.yaoImageView2));
        coinResultView.add(view.findViewById(R.id.yaoImageView3));
        coinResultView.add(view.findViewById(R.id.yaoImageView4));
        coinResultView.add(view.findViewById(R.id.yaoImageView5));
        coinResultView.add(view.findViewById(R.id.yaoImageView6));
        return coinResultView;
    }

    private void stop() {

        // Add any additional logic for stopping here
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
