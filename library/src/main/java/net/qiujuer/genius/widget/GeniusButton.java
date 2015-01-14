/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/03/2014
 * Changed 01/14/2015
 * Version 2.0.0
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
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;
import net.qiujuer.genius.animation.TouchEffect;
import net.qiujuer.genius.animation.TouchEffectAnimator;

/**
 * Created by Qiujuer
 * on 2014/9/3.
 */
public class GeniusButton extends Button implements Attributes.AttributeChangeListener {
    private int mBottom;

    private Attributes mAttributes;
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
            mAttributes = new Attributes(this, getResources());

        if (attrs != null) {
            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusButton, defStyle, 0);

            // Getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusButton_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setThemeSilent(customTheme, getResources());

            mAttributes.setFontFamily(a.getString(R.styleable.GeniusButton_g_fontFamily));
            mAttributes.setFontWeight(a.getString(R.styleable.GeniusButton_g_fontWeight));
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusButton_g_fontExtension));

            mAttributes.setTextAppearance(a.getInt(R.styleable.GeniusButton_g_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            mAttributes.setRadius(a.getDimensionPixelSize(R.styleable.GeniusButton_g_cornerRadius, Attributes.DEFAULT_RADIUS));

            // Getting view specific attributes
            setTouchEffect(a.getInt(R.styleable.GeniusButton_g_touchEffect, 3));
            setTouchEffectColor(a.getColor(R.styleable.GeniusButton_g_touchEffectColor, -1));
            mBottom = a.getDimensionPixelSize(R.styleable.GeniusButton_g_blockButtonEffectHeight, mBottom);

            a.recycle();
        }

        if (!mAttributes.isHasOwnBackground()) {

            // Creating normal state drawable
            ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            normalFront.getPaint().setColor(mAttributes.getColor(2));

            ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            normalBack.getPaint().setColor(mAttributes.getColor(1));

            normalBack.setPadding(0, 0, 0, mBottom);

            Drawable[] d = {normalBack, normalFront};
            LayerDrawable normal = new LayerDrawable(d);

            // Creating pressed state drawable
            ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            pressedFront.getPaint().setColor(mAttributes.getColor(1));

            ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            pressedBack.getPaint().setColor(mAttributes.getColor(0));
            if (mBottom != 0) pressedBack.setPadding(0, 0, 0, mBottom / 2);

            Drawable[] d2 = {pressedBack, pressedFront};
            LayerDrawable pressed = new LayerDrawable(d2);

            // Creating disabled state drawable
            ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            disabledFront.getPaint().setColor(mAttributes.getColor(3));
            disabledFront.getPaint().setAlpha(0xA0);

            ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadius(), null, null));
            disabledBack.getPaint().setColor(mAttributes.getColor(2));

            Drawable[] d3 = {disabledBack, disabledFront};
            LayerDrawable disabled = new LayerDrawable(d3);

            StateListDrawable states = new StateListDrawable();

            if (mTouchEffectAnimator == null)
                states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_enabled}, normal);
            states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

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
                mTouchEffectAnimator.setClipRadius(mAttributes.getRadius());
            }
        }
    }

    public void setTouchEffectColor(int touchEffectColor) {
        if (mTouchEffectAnimator != null && touchEffectColor != -1)
            mTouchEffectAnimator.setEffectColor(touchEffectColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTouchEffectAnimator != null)
            mTouchEffectAnimator.onMeasure();
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
    public Attributes getAttributes() {
        return mAttributes;
    }
}
