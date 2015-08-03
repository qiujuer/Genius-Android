package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.qiujuer.genius.ui.GeniusUi;
import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.AutoEffect;
import net.qiujuer.genius.ui.drawable.effect.EaseEffect;
import net.qiujuer.genius.ui.drawable.effect.PressEffect;
import net.qiujuer.genius.ui.drawable.effect.RippleEffect;
import net.qiujuer.genius.ui.drawable.factory.ClipFilletFactory;

/**
 * Created by qiujuer on 15/7/23.
 * This is touch effect button
 */
public class Button extends android.widget.Button implements TouchEffectDrawable.PerformClicker {
    private TouchEffectDrawable mTouchDrawable;
    private int mTouchColor;

    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Button(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Button.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Button.class.getName());
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null)
            return;

        final Context context = getContext();

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Button, defStyleAttr, defStyleRes);

        String fontFile = a.getString(R.styleable.Button_gFont);
        int touchEffect = a.getInt(R.styleable.Button_gTouchEffect, 1);
        int touchColor = a.getColor(R.styleable.Button_gColorTouch, GeniusUi.TOUCH_PRESS_COLOR);

        // Load clip touch corner radius
        ClipFilletFactory touchFactory = null;
        float touchRadius = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadius, 0);
        if (touchRadius > 0) {
            touchFactory = new ClipFilletFactory(touchRadius);
        } else {
            float touchRadiusTL = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusTL, 0);
            float touchRadiusTR = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusTR, 0);
            float touchRadiusBL = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusBL, 0);
            float touchRadiusBR = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusBR, 0);
            if (touchRadiusTL > 0 || touchRadiusTR > 0 || touchRadiusBL > 0 || touchRadiusBR > 0) {
                float[] radius = new float[]{touchRadiusTL, touchRadiusTL, touchRadiusTR, touchRadiusTR,
                        touchRadiusBR, touchRadiusBR, touchRadiusBL, touchRadiusBL};
                touchFactory = new ClipFilletFactory(radius);
            }
        }
        a.recycle();

        // SetTouch
        setTouchEffect(touchEffect);
        setTouchColor(touchColor);
        setTouchClipFactory(touchFactory);

        // Check for IDE preview render
        if (!this.isInEditMode() && fontFile != null && fontFile.length() > 0) {
            Typeface typeface = GeniusUi.getFont(getContext(), fontFile);
            if (typeface != null) setTypeface(typeface);
        }
    }

    public void setTouchEffect(int touchEffect) {
        if (touchEffect == 0)
            mTouchDrawable = null;
        else {
            if (mTouchDrawable == null) {
                mTouchDrawable = new TouchEffectDrawable();
                mTouchDrawable.getPaint().setColor(mTouchColor);
                mTouchDrawable.setCallback(this);
                mTouchDrawable.setPerformClicker(this);
            }

            if (touchEffect == 1)
                mTouchDrawable.setEffect(new AutoEffect());
            else if (touchEffect == 2)
                mTouchDrawable.setEffect(new EaseEffect());
            else if (touchEffect == 3)
                mTouchDrawable.setEffect(new PressEffect());
            else if (touchEffect == 4)
                mTouchDrawable.setEffect(new RippleEffect());

        }
    }

    public void setTouchColor(int touchColor) {
        if (mTouchDrawable != null && touchColor != -1 && touchColor != mTouchColor) {
            mTouchColor = touchColor;
            mTouchDrawable.setColor(touchColor);
            invalidate();
        }
    }

    public void setTouchClipFactory(TouchEffectDrawable.ClipFactory factory) {
        if (mTouchDrawable != null) {
            mTouchDrawable.setClipFactory(factory);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        TouchEffectDrawable drawable = mTouchDrawable;
        if (drawable != null) {
            /*
            Rect padding = new Rect();
            if (drawable.getPadding(padding) && (padding.left > 0
                    || padding.top > 0 || padding.right > 0 || padding.bottom > 0)) {
                drawable.setBounds(padding.left, padding.top, getWidth() - padding.right, getHeight() - padding.bottom);
            } else
            */
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mTouchDrawable;
        return (drawable != null && who == drawable) || super.verifyDrawable(who);
    }

    @Override
    public boolean performClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.isPerformClick() && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    public void perform() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                performClick();
            }
        };

        if (!this.post(runnable)) {
            performClick();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null && isEnabled()) {
            d.onTouch(event);
            super.onTouchEvent(event);
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null) {
            d.draw(canvas);
        }

        super.onDraw(canvas);
    }
}
