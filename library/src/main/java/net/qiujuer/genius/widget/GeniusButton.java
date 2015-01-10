/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 9/3/2014
 * Changed 06/01/2015
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
    private int bottom;
    private Attributes attributes;
    private TouchEffectAnimator touchEffectAnimator = null;

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
        // default values of specific attributes
        // saving padding values for using them after setting background drawable
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (attributes == null)
            attributes = new Attributes(this, getResources());

        if (attrs != null) {
            // Set if has own attrs
            attributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusButton, defStyle, 0);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusButton_g_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());

            attributes.setFontFamily(a.getString(R.styleable.GeniusButton_g_fontFamily));
            attributes.setFontWeight(a.getString(R.styleable.GeniusButton_g_fontWeight));
            attributes.setFontExtension(a.getString(R.styleable.GeniusButton_g_fontExtension));

            attributes.setTextAppearance(a.getInt(R.styleable.GeniusButton_g_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            attributes.setRadius(a.getDimensionPixelSize(R.styleable.GeniusButton_g_cornerRadius, Attributes.DEFAULT_RADIUS));

            // getting view specific attributes
            setTouchEffect(a.getInt(R.styleable.GeniusButton_g_touchEffect, 3));
            setTouchEffectColor(a.getColor(R.styleable.GeniusButton_g_touchEffectColor, -1));
            bottom = a.getDimensionPixelSize(R.styleable.GeniusButton_g_blockButtonEffectHeight, bottom);

            a.recycle();
        }

        if (!attributes.isHasOwnBackground()) {

            // creating normal state drawable
            ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            normalFront.getPaint().setColor(attributes.getColor(2));

            ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            normalBack.getPaint().setColor(attributes.getColor(1));

            normalBack.setPadding(0, 0, 0, bottom);

            Drawable[] d = {normalBack, normalFront};
            LayerDrawable normal = new LayerDrawable(d);

            // creating pressed state drawable
            ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedFront.getPaint().setColor(attributes.getColor(1));

            ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedBack.getPaint().setColor(attributes.getColor(0));
            if (bottom != 0) pressedBack.setPadding(0, 0, 0, bottom / 2);

            Drawable[] d2 = {pressedBack, pressedFront};
            LayerDrawable pressed = new LayerDrawable(d2);

            // creating disabled state drawable
            ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            disabledFront.getPaint().setColor(attributes.getColor(3));
            disabledFront.getPaint().setAlpha(0xA0);

            ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            disabledBack.getPaint().setColor(attributes.getColor(2));

            Drawable[] d3 = {disabledBack, disabledFront};
            LayerDrawable disabled = new LayerDrawable(d3);

            StateListDrawable states = new StateListDrawable();

            if (touchEffectAnimator == null)
                states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_enabled}, normal);
            states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

            // set Background
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                setBackgroundDrawable(states);
            else
                setBackground(states);
        }

        //set padding
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        // set color
        if (!attributes.isHasOwnTextColor()) {
            if (attributes.getTextAppearance() == 1) setTextColor(attributes.getColor(0));
            else if (attributes.getTextAppearance() == 2) setTextColor(attributes.getColor(3));
            else setTextColor(Color.WHITE);
        }

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), attributes);
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
            touchEffectAnimator = null;
        else {
            if (touchEffectAnimator == null) {
                touchEffectAnimator = new TouchEffectAnimator(this);
                touchEffectAnimator.setTouchEffect(touchEffect);
                touchEffectAnimator.setEffectColor(attributes.getColor(1));
                touchEffectAnimator.setClipRadius(attributes.getRadius());
            }
        }
    }

    public void setTouchEffectColor(int touchEffectColor) {
        if (touchEffectAnimator != null && touchEffectColor != -1)
            touchEffectAnimator.setEffectColor(touchEffectColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (touchEffectAnimator != null)
            touchEffectAnimator.onMeasure();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchEffectAnimator != null)
            touchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
}
