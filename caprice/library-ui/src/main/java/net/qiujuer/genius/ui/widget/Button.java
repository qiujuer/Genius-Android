/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/23/2015
 * Changed 11/12/2015
 * Version 3.0.0
 * Author Qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.AutoEffect;
import net.qiujuer.genius.ui.drawable.effect.EaseEffect;
import net.qiujuer.genius.ui.drawable.effect.PressEffect;
import net.qiujuer.genius.ui.drawable.effect.RippleEffect;
import net.qiujuer.genius.ui.drawable.factory.ClipFilletFactory;

/**
 * This is touch effect button
 * Include 'Auto' 'Ease' 'Press' 'Ripple' effect to touch
 * And supper custom font
 */
public class Button extends android.widget.Button implements TouchEffectDrawable.PerformClicker {
    private TouchEffectDrawable mTouchDrawable;
    private int mTouchColor;

    public Button(Context context) {
        super(context);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.gButtonStyle, R.style.Genius_Widget_Button);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Genius_Widget_Button);
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
        final Resources resources = getResources();

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Button, defStyleAttr, defStyleRes);

        String fontFile = a.getString(R.styleable.Button_gFont);
        int touchEffect = a.getInt(R.styleable.Button_gTouchEffect, 1);
        int touchColor = a.getColor(R.styleable.Button_gTouchColor, Ui.TOUCH_PRESS_COLOR);

        // Load clip touch corner radius
        int touchRadius = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadius, resources.getDimensionPixelOffset(R.dimen.g_button_touch_corners_radius));
        int touchRadiusTL = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusTL, touchRadius);
        int touchRadiusTR = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusTR, touchRadius);
        int touchRadiusBL = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusBL, touchRadius);
        int touchRadiusBR = a.getDimensionPixelOffset(R.styleable.Button_gTouchCornerRadiusBR, touchRadius);
        float[] radius = new float[]{touchRadiusTL, touchRadiusTL, touchRadiusTR, touchRadiusTR,
                touchRadiusBR, touchRadiusBR, touchRadiusBL, touchRadiusBL};
        ClipFilletFactory touchFactory = new ClipFilletFactory(radius);


        a.recycle();

        if (attrs.getAttributeValue(Ui.androidStyleNameSpace, "background") == null || getBackground() == null) {
            // Set Background
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                Drawable drawable = getResources().getDrawable(R.drawable.g_button_background);
                setBackgroundDrawable(drawable);
            } else
                setBackgroundResource(R.drawable.g_button_background);
        }

        // SetTouch
        setTouchEffect(touchEffect);
        setTouchColor(touchColor);

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            // Touch factory
            setTouchClipFactory(touchFactory);

            // Font
            if (fontFile != null && fontFile.length() > 0) {
                Typeface typeface = Ui.getFont(getContext(), fontFile);
                if (typeface != null) setTypeface(typeface);
            }
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
