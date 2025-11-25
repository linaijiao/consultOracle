package com.bhcj.telling.view.view;

import static org.jbox2d.dynamics.BodyType.DYNAMIC;
import static org.jbox2d.dynamics.BodyType.STATIC;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jawnnypoo.physicslayout.Physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

enum ThrowStatus {
    start, throwing, end;
}

public class ThrowCoinsView extends FrameLayout {
    public interface ThrowDoneCallback {
        void onThrowDone(ThrowCoinsView view, int[] result);
    }

    public void setThrowDoneCallback(ThrowDoneCallback callback) {
        this.throwDoneCallback = callback;
    }

    private ThrowDoneCallback throwDoneCallback;


    private ArrayList<CoinView> coins = new ArrayList<>();
    private ArrayList<CoinView> resultCoins = new ArrayList<>();
    private boolean isThrowing = false;

    public boolean getISThrowing() {
        return isThrowing;
    }

    private Physics physics; // 用于处理物理引擎的实例

    public ThrowCoinsView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ThrowCoinsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThrowCoinsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 初始化方法
    private void init(Context context, AttributeSet attrs) {
//        initialize(); // 初始化硬币相关操作
        setWillNotDraw(false);
        physics = new Physics(this, attrs); // 创建 Physics 类的实例
//        physics.setOnBodyCreatedListener(bodyCreatedListener);
        physics.setOnCollisionListener(collisionListener);
        initialize();
        physics.setPixelsPerMeter(50);
//        physics.setBoundsSize(20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        physics.onSizeChanged(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateViewState();
        physics.onLayout(changed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isThrowing) {
            physics.onDraw(canvas);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return physics.onInterceptTouchEvent(ev);
//        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return physics.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }

    // 初始化硬币相关操作
    void initialize() {
        // 获取布局参数中的硬币大小和列数
        int coinSize = getLayoutParams() instanceof LayoutParams ?
                ((LayoutParams) getLayoutParams()).coinSize : 250;
        int columnNumber = getLayoutParams() instanceof LayoutParams ?
                ((LayoutParams) getLayoutParams()).columnNumber : 3;

        // 创建硬币视图并添加到布局中
        for (int i = 0; i < columnNumber; i++) {
            CoinView coinView = createCoinView(i);
            addView(coinView);
            coins.add(coinView);
        }
        requestLayout();
    }

    // 创建硬币视图
    private CoinView createCoinView(int id) {
        int size = getLayoutParams() instanceof LayoutParams ?
                ((LayoutParams) getLayoutParams()).coinSize : 250;
        CoinView coinView = new CoinView(getContext(), id);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.TOP;
        coinView.setLayoutParams(params);
        return coinView;
    }

    // 开始抛硬币动画
    public void start(Vec2 impulse) {
        reset(); // 重置动画状态
        isThrowing = true;
        ThrowCoinsView coinView = this;
        setCoinsViewPosition(ThrowStatus.throwing, true, new Runnable() {
            @Override
            public void run() {
                // 所有硬币视图动画完成后执行的操作
                updateViewState(); // 更新视图状态
                throwCoins(impulse); // 执行抛硬币动画
                physics.addOnPhysicsProcessedListener(physicsProcessedListener);
                coinView.invalidate();
            }
        });
    }

    // 停止抛硬币动画
    public void stop() {
        isThrowing = false;
        physics.removeOnPhysicsProcessedListener(physicsProcessedListener);
        updateViewState(); // 更新视图状态
    }

    // 重置动画状态
    public void reset() {
        isThrowing = false;
        resultCoins.clear();
        setCoinsViewPosition(ThrowStatus.start, true, null);
    }

    public void throwCoins1(Vec2 impulse) {

    }

    // 执行抛硬币动画
    public void throwCoins(Vec2 impulse) {
        float multiple = 9.8f;
        float y = pixelsToMeters(getRandomY()) * multiple;
        for (CoinView coinView : coins) {
            Body body = physics.findBodyById(coinView.getId());
            if (body == null) {
                continue;
            }
            if (impulse == null) {
                impulse = new Vec2(pixelsToMeters(getRandomX()) * multiple * 2, y);
            }

            float angularImpulse = new Random().nextInt(360);
            if (impulse != null) {
                angularImpulse = impulse.length();
            }
            body.applyAngularImpulse(angularImpulse);
            Vec2 position = body.getPosition();
            body.applyLinearImpulse(impulse, position);
        }
    }

    void throwDone() {
        // 将 coins 中的元素按照 x 坐标升序排序
        resultCoins.sort(new Comparator<View>() {
            @Override
            public int compare(View coin1, View coin2) {
                // 假设 getX() 返回 float 类型
                float x1 = coin1.getX();
                float x2 = coin2.getX();
                // 根据横坐标升序排序
                return Float.compare(x1, x2);
            }
        });
        int[] result = showHexagram(resultCoins);

        setCoinsViewPosition(ThrowStatus.end, true, null);

        if (throwDoneCallback != null) {
            throwDoneCallback.onThrowDone(this, result);
        }
    }

    private void handleCollision(int id1, int id2) {
        int[] ids = new int[]{id1, id2};
//        boolean isAwake = false;
        for (int id : ids) {
            CoinView coinView = findViewById(id);
            if (coinView != null) {
                coinView.playCollisionSoundEffect();
//                coinView.flipAnimate().start();
                if (coinView.flipAnimation != null) {
                    coinView.flipAnimation.cancel();
                }
            }
        }
    }

    private void handleCollisionExit(int id1, int id2) {
        int[] ids = new int[]{id1, id2};
        for (int id : ids) {
            CoinView coinView = findViewById(id);
            Body body = physics.findBodyById(id);
            if (coinView != null && body != null) {
//                coinView.flipAnimate(body).start();
            }
        }
    }

    // 处理物理引擎处理完成的事件
    private void handlePhysicsProcessed(@NonNull Physics physics, @NonNull World world) {
        if (!isThrowing) {
            return;
        }
        for (CoinView coinView : coins) {
            Body body = physics.findBodyById(coinView.getId());
            if (body == null) {
                continue;
            }
            if (isBodyStopped(body)) {
                handleBodyStopped(coinView, body);
            }
        }
        if (resultCoins.size() == coins.size()) {
            stop();
            throwDone();
        }
    }

    // 处理硬币停止运动的事件
    private void handleBodyStopped(CoinView coinView, Body body) {
        if (!isThrowing) {
            return;
        }
        if (!resultCoins.contains(coinView)) {
            resultCoins.add(coinView);
        }
    }

    private int[] showHexagram(ArrayList<CoinView> coins) {
        int[] hexagramResults = new int[3];

        // https://github.com/douxt/divination/blob/adab3e4db865614ec9cf0ba4faff6a7b898d9bee/src/divination/divination.js#L118
        int yangNum = 0;

        StringBuilder hexagramBuilder = new StringBuilder("卜卦结果：");
        for (int i = 0; i < coins.size(); i++) {
            CoinView coinView = coins.get(i);
            int result = coinView.getRotation() % 360 > 180 ? 0 : 1;
            yangNum += result;
            hexagramResults[i] = result;
            hexagramBuilder.append(result);
        }
        hexagramBuilder.append(",").append(yangNum);
//        Toast.makeText(getContext(), hexagramBuilder.toString(), Toast.LENGTH_SHORT).show();
        return hexagramResults;
    }

    // 设置硬币视图位置
    private void setCoinsViewPosition(ThrowStatus status, boolean animated, final Runnable onAnimationEndCallback) {
        int coinCount = coins.size();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < coinCount; i++) {
            CoinView coinView = coins.get(i);
            Vec2 position = coinViewPosition(coinView, status);
            if (animated) {
                animators.addAll(CoinViewPositionAnimation(coinView, status, position));
            } else {
                coinView.setX(position.x);
                coinView.setY(position.y);
            }
            if (status == ThrowStatus.start) {
                coinView.resetRotation();
            }
        }
        if (animated) {
            long duration = 500; // 设置动画持续时间为500毫秒
            // 启动动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(duration);
            animatorSet.playTogether(animators);
            // 添加动画完成的监听器
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 在动画完成时执行回调操作
                    if (onAnimationEndCallback != null) {
                        onAnimationEndCallback.run();
                    }
                }
            });
            animatorSet.start();
        } else {
            if (onAnimationEndCallback != null) {
                onAnimationEndCallback.run();
            }
        }
    }

    Vec2 coinViewPosition(CoinView coinView, ThrowStatus status) {
        float y = 0;
        float x = 0;
        float spacingOfGapPixels = 20;
        int id = coinView.getId();
        int columnIndex = id % getColumnNumber();
        float itemPixelsWidth = coinView.getWidth();

        switch (status) {
            case start:
                y = 0;
                x = columnIndex * spacingOfGapPixels;
                break;
            case throwing:
                y = coinView.getHeight();
//                y = getHeight() - coinView.getHeight();
//                y = getHeight() / 2 + coinView.getHeight() / 2;
                x = (getWidth() - itemPixelsWidth * getColumnNumber()) / 2 +
                        (itemPixelsWidth + spacingOfGapPixels) * columnIndex;
                break;
            case end:
                y = 0;
                x = getWidth() - coinView.getWidth() + columnIndex * spacingOfGapPixels;
                ;
                break;
        }
        return new Vec2(x, y);
    }

    ArrayList<ObjectAnimator> CoinViewPositionAnimation(
            final CoinView coinView, ThrowStatus status, Vec2 position) {
        // 使用属性动画移动 CoinView 到新位置
        ArrayList<ObjectAnimator> animators = new ArrayList<>();
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(coinView, "x", coinView.getX(), position.x);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(coinView, "y", coinView.getY(), position.y);

        animators.add(xAnimator);
        animators.add(yAnimator);

        // 设置动画持续时间
        long duration = 500; // 设置动画持续时间为500毫秒
        xAnimator.setDuration(duration);
        yAnimator.setDuration(duration);
        return animators;
    }

    public boolean isBodyStopped(Body body) {
        Vec2 linearVelocity = body.getLinearVelocity();
        float angularVelocity = body.getAngularVelocity();

        // 设置一个阈值，表示速度接近零的情况
        float linearVelocityThreshold = 60f;
        float angularVelocityThreshold = 1f;

        // 检查线性速度和角速度是否都小于阈值
        boolean linearStopped = Math.abs(linearVelocity.x) > linearVelocityThreshold
                || Math.abs(linearVelocity.y) > linearVelocityThreshold;

        boolean angularStopped = Math.abs(angularVelocity) < angularVelocityThreshold;

        // 如果线性速度和角速度都小于阈值，则认为物体停止运动
        return linearStopped && angularStopped;
    }

    // 将像素转换为米
    private float pixelsToMeters(float pixels) {
        return pixels / physics.getPixelsPerMeter();
    }

    // 将米转换为像素
    private float metersToPixels(float meters) {
        return meters * physics.getPixelsPerMeter();
    }

    // 获取随机的X坐标
    private float getRandomX() {
        int width = getWidth();
        return (float) (new Random().nextInt(width) - width / 2);
    }

    // 获取随机的Y坐标
    private float getRandomY() {
        float screenHeight = getHeight() - getCoinSize();
        int baseHeight = (int) screenHeight / 3;
        return (float) (new Random().nextInt(baseHeight * 3) + baseHeight);
    }

    // 更新视图状态
    private void updateViewState() {
        for (CoinView coinView : coins) {
            BodyDef bodyDef = coinView.physicsConfig.getBodyDef();
            bodyDef.type = isThrowing ? DYNAMIC : STATIC;
            coinView.physicsConfig.setBodyDef(bodyDef);
        }
        physics.onLayout(true);
    }

    // 获取列数
    private int getColumnNumber() {
        return getLayoutParams() instanceof LayoutParams ?
                ((LayoutParams) getLayoutParams()).columnNumber : 3;
    }

    // 获取硬币大小
    private int getCoinSize() {
        return getLayoutParams() instanceof LayoutParams ?
                ((LayoutParams) getLayoutParams()).coinSize : 250;
    }

    // 设置布局参数
    public void setLayoutParams(LayoutParams params) {
        super.setLayoutParams(params);
        initialize(); // 重新初始化硬币相关操作
    }

    // 布局参数的内部类，存储硬币大小和列数
    public static class LayoutParams extends FrameLayout.LayoutParams {
        public int coinSize = 250;
        public int columnNumber = 3;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    Physics.OnCollisionListener collisionListener = new Physics.OnCollisionListener() {
        @Override
        public void onCollisionEntered(int i, int i1) {
            handleCollision(i, i1);
        }

        @Override
        public void onCollisionExited(int i, int i1) {
            handleCollisionExit(i, i1);
        }
    };

    private Physics.OnPhysicsProcessedListener physicsProcessedListener = new Physics.OnPhysicsProcessedListener() {

        @Override
        public void onPhysicsProcessed(@NonNull Physics physics, @NonNull World world) {
            handlePhysicsProcessed(physics, world);
        }
    };

    private Physics.OnBodyCreatedListener bodyCreatedListener = new Physics.OnBodyCreatedListener() {
        @Override
        public void onBodyCreated(@NonNull View view, @NonNull Body body) {
//            handleBodyCreated(view, body);
        }
    };
}

