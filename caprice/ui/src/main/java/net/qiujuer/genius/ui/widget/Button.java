/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.Effect;
import net.qiujuer.genius.ui.drawable.effect.EffectFactory;
import net.qiujuer.genius.ui.drawable.factory.ClipFilletFactory;

/**
 * This is touch effect button
 * Include 'Auto' 'Ease' 'Press' 'Ripple' effect to touch
 * And supper custom font
 * <p>
 * <p><strong>XML attributes</strong></p>
 * <p>
 * See {@link net.qiujuer.genius.ui.R.styleable#Button_gFont Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchEffect Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchCornerRadius Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchCornerRadiusTL Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchCornerRadiusTR Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchCornerRadiusBL Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchCornerRadiusBR Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#Button_gTouchDurationRate Attributes}
 */
public class Button extends android.widget.Button implements TouchEffectDrawable.PerformClicker {
    private TouchEffectDrawable mTouchDrawable;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gButtonStyle);
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
        int touchEffect = a.getInt(R.styleable.Button_gTouchEffect, EffectFactory.TOUCH_EFFECT_NONE);
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
        float touchDurationRate = a.getFloat(R.styleable.Button_gTouchDurationRate, 1.0f);

        a.recycle();

        // Initial  TouchEffectDrawable
        if (touchEffect != 0) {
            TouchEffectDrawable touchEffectDrawable = new TouchEffectDrawable();
            touchEffectDrawable.setColor(touchColor);
            touchEffectDrawable.setEffect(EffectFactory.creator(touchEffect));
            touchEffectDrawable.setEnterDuration(touchDurationRate);
            touchEffectDrawable.setExitDuration(touchDurationRate);
            // Check for IDE preview render to set Touch factory
            if (!this.isInEditMode()) {
                touchEffectDrawable.setClipFactory(touchFactory);
            }
            setTouchDrawable(touchEffectDrawable);
        }

        // Check for IDE preview render to set Font
        if (!this.isInEditMode()) {
            if (fontFile != null && fontFile.length() > 0) {
                Typeface typeface = Ui.getFont(getContext(), fontFile);
                if (typeface != null) setTypeface(typeface);
            }
        }
    }

    /**
     * Get the TouchEffect drawable,
     * you can set parameters in this
     *
     * @return See {@link TouchEffectDrawable}
     */
    @SuppressWarnings("unused")
    public TouchEffectDrawable getTouchDrawable() {
        return mTouchDrawable;
    }

    /**
     * In this, you can set TouchEffectDrawable,
     * to init TouchEffectDrawable.
     * <p>
     * If you not need touch effect,
     * you should set NULL.
     * <p>
     * But, if need it,
     * you should call {@link TouchEffectDrawable#setEffect(Effect)}
     *
     * @param touchDrawable TouchEffectDrawable
     */
    public void setTouchDrawable(TouchEffectDrawable touchDrawable) {
        if (mTouchDrawable != touchDrawable) {
            if (mTouchDrawable != null) {
                mTouchDrawable.setCallback(null);
            }
            if (touchDrawable != null) {
                touchDrawable.setCallback(this);
                // We must set layer type is View.LAYER_TYPE_SOFTWARE,
                // to support Canvas.clipPath()
                // on Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                if (getLayerType() != View.LAYER_TYPE_SOFTWARE)
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            mTouchDrawable = touchDrawable;
        }
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        // In this, to support Canvas.clipPath(),
        // must set layerType is View.LAYER_TYPE_SOFTWARE
        // on your need touch draw
        if (mTouchDrawable != null)
            layerType = View.LAYER_TYPE_SOFTWARE;
        super.setLayerType(layerType, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        TouchEffectDrawable drawable = mTouchDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mTouchDrawable;
        return (drawable != null && who == drawable) || super.verifyDrawable(who);
    }

    @Override
    public boolean performClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.performClick(this) && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    public void postPerformClick() {
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
        //return super.onTouchEvent(event);
        final boolean ret = super.onTouchEvent(event);

        // send to touch drawable
        final TouchEffectDrawable d = mTouchDrawable;
        if (ret && d != null && isEnabled()) {
            d.onTouch(event);
        }

        return ret;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null) {
            d.draw(canvas);
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean performLongClick() {
        return super.performLongClick();
    }
}
