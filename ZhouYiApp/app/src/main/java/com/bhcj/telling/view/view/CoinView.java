package com.bhcj.telling.view.view;

import static org.jbox2d.dynamics.BodyType.STATIC;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bhcj.telling.R;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.Shape;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import java.util.Random;

/**
 * 硬币视图类
 * 实现硬币的物理效果和动画，包括翻转动画、碰撞音效等
 * 继承自FrameLayout，支持物理引擎集成
 * 
 * @author bhcj
 * @version 1.0
 */
public class CoinView extends FrameLayout {

    /** 初始力向量 */
    public Vec2 startForce;
    
    /** 物理配置对象 */
    public PhysicsConfig physicsConfig;

    private ImageView frontImageView;
    private ImageView backImageView;
//    private Camera camera;
//    private Matrix matrix;
//    private float flipAngle = 0;
//
//    private Bitmap frontBitmap;
//    private Bitmap backBitmap;

    private SoundPool soundPool;
    int soundID;
    public Animator flipAnimation;

    public CoinView(Context context, int id) {
        super(context);
        init(context, id);
    }

    public CoinView(Context context, int id, AttributeSet attrs) {
        super(context, attrs);
        init(context, id);
    }

    public CoinView(Context context, int id, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, id);
    }

    private void init(Context context, int id) {
        frontImageView = new ImageView(context);
        backImageView = new ImageView(context);

        frontImageView.setImageResource(R.drawable.front);
        backImageView.setImageResource(R.drawable.back);

        addView(frontImageView);
        addView(backImageView);

//        camera = new Camera();
//        matrix = new Matrix();

        setId(id);

        PhysicsConfig config = new PhysicsConfig();
        config.setShape(Shape.RECTANGLE);

        BodyDef bodyDef = PhysicsConfig.Companion.createDefaultBodyDef();
        bodyDef.gravityScale = 3;
        bodyDef.type = STATIC;
        config.setBodyDef(bodyDef);

        FixtureDef fixtureDef = PhysicsConfig.Companion.createDefaultFixtureDef();
        fixtureDef.friction = 0.5f;
        config.setFixtureDef(fixtureDef);
        physicsConfig = config;

        Physics.Companion.setPhysicsConfig(this, config);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

        soundID = soundPool.load(getContext(), R.raw.foley_20_pence_peice_put_down_on_surface, 1);
   }

    public void resetRotation() {
        float rotation = (float) new Random().nextInt(360);
        setRotation(rotation);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        float value = (rotation % 360);
        if (value > 180) {
            frontImageView.setVisibility(View.INVISIBLE);
            backImageView.setVisibility(View.VISIBLE);
        } else {
            backImageView.setVisibility(View.INVISIBLE);
            frontImageView.setVisibility(View.VISIBLE);
        }
    }

    public void setRotationAngle(float targetAngle) {
    }

    private float interpolate(float start, float end, float fraction) {
        return start + fraction * (end - start);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        // 省略部分代码...
//    }

    private float scaleFactor = 0.01f;
//    private Animator flipAnimation;

    void playCollisionSoundEffect() {
        soundPool.play(soundID, 1, 1, 0, 0, 1);
    }

    public Animator flipAnimate(Body body) {
        if (this.flipAnimation != null) {
            this.flipAnimation.cancel();
        }

        Vec2 line = body.getLinearVelocity();
        float acceleration = line.length();
        float angularVelocity = body.getAngularVelocity();

        float oldRotationY = frontImageView.getRotationY();
        float targetRotationY = oldRotationY + (float) Math.toDegrees(line.y);

        long baseDuration = 100;
        long mappedDuration = baseDuration + (long) (acceleration * scaleFactor);
        int rotationCount = (int) Math.abs(angularVelocity * scaleFactor); // 根据角速度控制旋转次数

        boolean isClockwise = angularVelocity < 0;

        AnimatorSet flipAnimator = new AnimatorSet();
        flipAnimator.setDuration(mappedDuration);
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        flipAnimator.setInterpolator(interpolator);

        PropertyValuesHolder rotationFront = PropertyValuesHolder.ofFloat("rotationX", oldRotationY, targetRotationY);
        PropertyValuesHolder rotationBack = PropertyValuesHolder.ofFloat("rotationX", -oldRotationY, -targetRotationY);

        ObjectAnimator frontAnimator = ObjectAnimator.ofPropertyValuesHolder(this.frontImageView, rotationFront);
//        frontAnimator.setRepeatCount(rotationCount);
        ObjectAnimator backAnimator = ObjectAnimator.ofPropertyValuesHolder(this.backImageView, rotationBack);
//        frontAnimator.setRepeatCount(rotationCount);

        flipAnimator.playTogether(frontAnimator, backAnimator);

//        frontAnimator.addUpdateListener(animation -> {
//            float animatedValue = (float) animation.getAnimatedValue();
//            float frontAlpha = Math.max(0, Math.min(1, animatedValue / 180f));
//            float backAlpha = 1 - frontAlpha;
//            frontImageView.setAlpha(frontAlpha);
//            backImageView.setAlpha(backAlpha);
//        });
        flipAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                resetAnimation().start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
//                resetAnimation().start();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }

            Animator resetAnimation() {
                ObjectAnimator frontResetAnimator = ObjectAnimator.ofFloat(frontImageView, "rotationX", 0);
                frontResetAnimator.setRepeatCount(1);
                ObjectAnimator backResetAnimator = ObjectAnimator.ofFloat(backImageView, "rotationX", 0);
                backResetAnimator.setRepeatCount(1);
//                frontResetAnimator.addUpdateListener(animation -> {
//                    float animatedValue = (float) animation.getAnimatedValue();
//                    float frontAlpha = Math.max(0, Math.min(1, animatedValue / 180f));
//                    float backAlpha = 1 - frontAlpha;
//                    frontImageView.setAlpha(frontAlpha);
//                    backImageView.setAlpha(backAlpha);
//                });
                AnimatorSet set = new AnimatorSet();
                set.setDuration(mappedDuration);
                set.playTogether(frontResetAnimator, backResetAnimator);
                set.start();
                return set;
            }
        });


        this.flipAnimation = (Animator) flipAnimator;
        return (Animator) flipAnimator;
    }
}