package net.qiujuer.genius.material;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import net.qiujuer.genius.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * MaterialDesignButton
 * Create By Qiujuer.
 */
public class MaterialButton extends Button {
    private static final long ANIMATION_DURATION = 500;
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final long MIN_RADIUS = 48;
    private static final long MIN_LONG = 96;

    private Paint mBackgroundPaint;

    private float mRadius;
    private float mLong;

    private float x, y;

    private int mTickColor = Color.parseColor("#0e8b9e");
    private int mPlusColor = Color.parseColor("#13b7d2");

    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    public MaterialButton(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MaterialButton.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MaterialButton.class.getName());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(x, y);
        //画圆
        //canvas.drawCircle(0, 0, mRadius, mBackgroundPaint);

        //画圆角矩形
        // 长方形 Left Top Right Bottom
        RectF rect = new RectF(-mLong / 2, -mRadius, mLong / 2, mRadius);
        //矩形 x半径 y半径 颜色
        canvas.drawRoundRect(rect, mRadius, mRadius, mBackgroundPaint);

        canvas.restore();
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                animatePlus();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                break;
            case MotionEvent.ACTION_UP:
                //抬起
                break;
        }

        return super.onTouchEvent(event);
    }

    public void animatePlus() {
        mBackgroundColorProperty.set(this, mTickColor);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(this, mLongProperty, MIN_LONG, 1000),
                ObjectAnimator.ofFloat(this, mRadiusProperty, MIN_RADIUS, 200),
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, mArgbEvaluator, mPlusColor)
        );
        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(ANIMATION_INTERPOLATOR);
        set.start();
    }

    private Property<MaterialButton, Float> mLongProperty = new Property<MaterialButton, Float>(Float.class, "long") {
        @Override
        public Float get(MaterialButton object) {
            return object.mLong;
        }

        @Override
        public void set(MaterialButton object, Float value) {
            object.mLong = value;
            invalidate();
        }
    };

    private Property<MaterialButton, Float> mRadiusProperty = new Property<MaterialButton, Float>(Float.class, "radius") {
        @Override
        public Float get(MaterialButton object) {
            return object.mRadius;
        }

        @Override
        public void set(MaterialButton object, Float value) {
            object.mRadius = value;
            //invalidate();
        }
    };

    private Property<MaterialButton, Integer> mBackgroundColorProperty = new Property<MaterialButton, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(MaterialButton object) {
            return object.mBackgroundPaint.getColor();
        }

        @Override
        public void set(MaterialButton object, Integer value) {
            object.mBackgroundPaint.setColor(value);
            //invalidate();
        }
    };

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MaterialButton, defStyle, 0);
        a.recycle();

        mBackgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mTickColor);

    }

}
