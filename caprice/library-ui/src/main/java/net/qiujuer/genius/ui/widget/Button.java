package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.qiujuer.genius.ui.GeniusUi;
import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.AutoEffect;
import net.qiujuer.genius.ui.drawable.effect.EaseEffect;
import net.qiujuer.genius.ui.drawable.effect.PressEffect;
import net.qiujuer.genius.ui.drawable.effect.RippleEffect;

/**
 * Created by qiujuer on 15/7/23.
 * This is touch effect button
 */
public class Button extends android.widget.Button implements TouchEffectDrawable.PerformClicker {
    private TouchEffectDrawable mTouchDrawable;
    private int mTouchColor;

    public Button(Context context) {
        super(context);
        init(null, 0);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Button(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final Context context = getContext();

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Button, defStyle, 0);


        int touchEffect = a.getInt(R.styleable.Button_gTouchEffect, 4);
        int touchColor = a.getColor(R.styleable.FloatActionButton_gColorTouch, GeniusUi.TOUCH_PRESS_COLOR);
        a.recycle();

        setTouchEffect(touchEffect);
        setTouchColor(touchColor);
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
            mTouchDrawable.getPaint().setColor(touchColor);
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mTouchDrawable != null)
            mTouchDrawable.setBounds(0, 0, getWidth(), getHeight());
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (mTouchDrawable != null)
            return who == mTouchDrawable || super.verifyDrawable(who);
        else return super.verifyDrawable(who);
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
            this.performClick();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean bFlag = super.onTouchEvent(event);

        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null && isEnabled()) {
            d.onTouch(event);
            return true;
        }

        return bFlag;
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
