/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/03/2014
 * Changed 02/10/2015
 * Version 2.0.0
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
package net.qiujuer.genius.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;
import net.qiujuer.genius.animation.TouchEffect;
import net.qiujuer.genius.animation.TouchEffectAnimator;
import net.qiujuer.genius.widget.attribute.Attributes;
import net.qiujuer.genius.widget.attribute.ButtonAttributes;
import net.qiujuer.genius.widget.attribute.GeniusAttributes;

/**
 * GeniusButton this have touch effect animator
 */
public class GeniusButton extends Button implements Attributes.AttributeChangeListener {
    private ButtonAttributes mAttributes = null;
    private TouchEffectAnimator mTouchEffectAnimator = null;

    public GeniusButton(Context context) {
        super(context);
        init(null, 0);
    }

    public GeniusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GeniusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @SuppressWarnings("deprecation")
    private void init(AttributeSet attrs, int defStyle) {
        // Default values of specific attributes
        // Saving padding values for using them after setting background drawable
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (mAttributes == null)
            mAttributes = new ButtonAttributes(this, getResources());

        if (attrs != null) {
            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusButton, defStyle, 0);

            // Getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusButton_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setTheme(customTheme, getResources());

            mAttributes.setFontFamily(GeniusAttributes.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.GeniusButton_g_fontFamily, 0)]);
            mAttributes.setFontWeight(GeniusAttributes.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.GeniusButton_g_fontWeight, 3)]);
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusButton_g_fontExtension));
            mAttributes.setTextAppearance(a.getInt(R.styleable.GeniusButton_g_textAppearance, GeniusAttributes.DEFAULT_TEXT_APPEARANCE));
            mAttributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusButton_g_borderWidth, GeniusAttributes.DEFAULT_BORDER_WIDTH));

            // Set init Corners Radius
            mAttributes.initCornerRadius(a, R.styleable.GeniusButton_g_cornerRadius,
                    R.styleable.GeniusButton_g_cornerRadii_A, R.styleable.GeniusButton_g_cornerRadii_B,
                    R.styleable.GeniusButton_g_cornerRadii_C, R.styleable.GeniusButton_g_cornerRadii_D);

            // Getting view specific attributes
            mAttributes.setBottom(a.getDimensionPixelSize(R.styleable.GeniusButton_g_blockButtonEffectHeight, mAttributes.getBottom()));
            setTouchEffect(a.getInt(R.styleable.GeniusButton_g_touchEffect, 3));
            setTouchEffectColor(a.getColor(R.styleable.GeniusButton_g_touchEffectColor, -1));
            mAttributes.setDelayClick(a.getBoolean(R.styleable.GeniusButton_g_delayClick, mAttributes.isDelayClick()));

            a.recycle();
        }

        if (!mAttributes.isHasOwnBackground()) {

            Drawable[] background;
            if (mAttributes.getBorderWidth() > 0)
                background = initBorderBackground();
            else
                background = initBackground();

            StateListDrawable states = new StateListDrawable();

            if (mTouchEffectAnimator == null)
                states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, background[1]);
            states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, background[1]);
            states.addState(new int[]{android.R.attr.state_enabled}, background[0]);
            states.addState(new int[]{-android.R.attr.state_enabled}, background[2]);

            // Set Background
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                setBackgroundDrawable(states);
            else
                setBackground(states);
        }

        // Set padding
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        // Set color
        if (!mAttributes.isHasOwnTextColor()) {
            if (mAttributes.getTextAppearance() == 1) setTextColor(mAttributes.getColor(0));
            else if (mAttributes.getTextAppearance() == 2) setTextColor(mAttributes.getColor(3));
            else setTextColor(Color.WHITE);
        }

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), mAttributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    private Drawable[] initBackground() {
        Drawable[] background = new Drawable[3];

        // Creating normal state drawable
        ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
        normalFront.getPaint().setColor(mAttributes.getColor(2));

        if (mAttributes.getBottom() > 0) {
            ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
            normalBack.getPaint().setColor(mAttributes.getColor(1));
            normalBack.setPadding(0, 0, 0, mAttributes.getBottom());

            Drawable[] d = {normalBack, normalFront};
            LayerDrawable normal = new LayerDrawable(d);

            background[0] = normal;
        } else
            background[0] = normalFront;

        // Creating pressed state drawable
        ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
        pressedFront.getPaint().setColor(mAttributes.getColor(1));

        if (mAttributes.getBottom() > 0) {
            ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
            pressedBack.getPaint().setColor(mAttributes.getColor(0));
            if (mAttributes.getBottom() != 0)
                pressedBack.setPadding(0, 0, 0, mAttributes.getBottom() / 2);

            Drawable[] d2 = {pressedBack, pressedFront};
            LayerDrawable pressed = new LayerDrawable(d2);

            background[1] = pressed;
        } else
            background[1] = pressedFront;

        // Creating disabled state drawable
        ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
        disabledFront.getPaint().setColor(mAttributes.getColor(3));
        disabledFront.getPaint().setAlpha(0xA0);


        if (mAttributes.getBottom() > 0) {
            ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadii(), null, null));
            disabledBack.getPaint().setColor(mAttributes.getColor(2));

            Drawable[] d3 = {disabledBack, disabledFront};
            LayerDrawable disabled = new LayerDrawable(d3);

            background[2] = disabled;
        } else
            background[2] = disabledFront;

        return background;
    }

    private Drawable[] initBorderBackground() {
        Drawable[] background = new Drawable[3];

        // Creating normal state drawable
        GradientDrawable normal = new GradientDrawable();
        normal.setCornerRadii(mAttributes.getOuterRadiiNull());
        normal.setColor(mAttributes.getColor(2));
        normal.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(1));
        background[0] = normal;

        // Creating pressed state drawable
        GradientDrawable pressed = new GradientDrawable();
        pressed.setCornerRadii(mAttributes.getOuterRadiiNull());
        pressed.setColor(mAttributes.getColor(1));
        pressed.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(0));
        background[1] = pressed;

        // Creating disabled state drawable
        GradientDrawable disabled = new GradientDrawable();
        disabled.setCornerRadii(mAttributes.getOuterRadiiNull());
        disabled.setColor(mAttributes.getColor(3));
        disabled.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(2));
        disabled.setAlpha(0xA0);
        background[2] = disabled;

        return background;
    }

    private void setTouchEffect(int value) {
        TouchEffect touchEffect = TouchEffect.Move;
        switch (value) {
            case 0:
                touchEffect = TouchEffect.None;
                break;
            case 1:
                touchEffect = TouchEffect.Ease;
                break;
            case 2:
                touchEffect = TouchEffect.Ripple;
                break;
            case 3:
                touchEffect = TouchEffect.Move;
                break;
            case 4:
                touchEffect = TouchEffect.Press;
                break;
        }
        setTouchEffect(touchEffect);
    }

    public void setTouchEffect(TouchEffect touchEffect) {
        if (touchEffect == TouchEffect.None)
            mTouchEffectAnimator = null;
        else {
            if (mTouchEffectAnimator == null) {
                mTouchEffectAnimator = new TouchEffectAnimator(this);
                mTouchEffectAnimator.setTouchEffect(touchEffect);
                mTouchEffectAnimator.setEffectColor(mAttributes.getColor(1));
                mTouchEffectAnimator.setClipRadii(mAttributes.getOuterRadii());
            }
        }
    }

    public void setTouchEffectColor(int touchEffectColor) {
        if (mTouchEffectAnimator != null && touchEffectColor != -1)
            mTouchEffectAnimator.setEffectColor(touchEffectColor);
    }

    @Override
    public boolean performClick() {
        if (mAttributes.isDelayClick() && mTouchEffectAnimator != null) {
            return !mTouchEffectAnimator.interceptClick() && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTouchEffectAnimator != null)
            mTouchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchEffectAnimator != null)
            mTouchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @Override
    public ButtonAttributes getAttributes() {
        return mAttributes;
    }
}
