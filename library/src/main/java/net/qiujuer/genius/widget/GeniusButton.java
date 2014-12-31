/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 9/3/2014
 * Changed 12/30/2014
 * Version 1.0.0
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

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Qiujuer
 * on 2014/9/3.
 */
public class GeniusButton extends Button implements Attributes.AttributeChangeListener {
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final long ANIMATION_TIME = 420;
    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    private Paint backgroundPaint;
    private float paintX, paintY, radius;
    private int bottom;
    private Attributes attributes;
    private AnimatorSet mAnimatorSet;

    private boolean isMaterial = true;
    private boolean isAutoMove = true;

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
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusButton, defStyle, 0);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusButton_g_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());

            attributes.setFontFamily(a.getString(R.styleable.GeniusButton_g_fontFamily));
            attributes.setFontWeight(a.getString(R.styleable.GeniusButton_g_fontWeight));
            attributes.setFontExtension(a.getString(R.styleable.GeniusButton_g_fontExtension));

            attributes.setTextAppearance(a.getInt(R.styleable.GeniusButton_g_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            attributes.setRadius(a.getDimensionPixelSize(R.styleable.GeniusButton_g_cornerRadius, 0));

            // getting view specific attributes
            setMaterial(a.getBoolean(R.styleable.GeniusButton_g_isMaterial, true));
            setAutoMove(a.getBoolean(R.styleable.GeniusButton_g_isAutoMove, true));
            bottom = a.getDimensionPixelSize(R.styleable.GeniusButton_g_blockButtonEffectHeight, bottom);

            a.recycle();
        }

        // creating normal state drawable
        ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalFront.getPaint().setColor(attributes.getColor(2));

        ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalBack.getPaint().setColor(attributes.getColor(1));

        normalBack.setPadding(0, 0, 0, bottom);

        Drawable[] d = {normalBack, normalFront};
        LayerDrawable normal = new LayerDrawable(d);

        // creating disabled state drawable
        ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledFront.getPaint().setColor(attributes.getColor(3));

        ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledBack.getPaint().setColor(attributes.getColor(2));

        Drawable[] d3 = {disabledBack, disabledFront};
        LayerDrawable disabled = new LayerDrawable(d3);

        // set StateListDrawable
        StateListDrawable states = new StateListDrawable();
        if (!isMaterial) {
            // creating pressed state drawable
            ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedFront.getPaint().setColor(attributes.getColor(1));

            ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedBack.getPaint().setColor(attributes.getColor(0));
            if (bottom != 0) pressedBack.setPadding(0, 0, 0, bottom / 2);

            Drawable[] d2 = {pressedBack, pressedFront};
            LayerDrawable pressed = new LayerDrawable(d2);

            states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
        }

        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

        // set Background
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            setBackgroundDrawable(states);
        else
            setBackground(states);

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (attributes.getTextAppearance() == 1) setTextColor(attributes.getColor(0));
        else if (attributes.getTextAppearance() == 2) setTextColor(attributes.getColor(3));
        else setTextColor(Color.WHITE);

        backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(attributes.getColor(1));

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), attributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    public boolean isMaterial() {
        return isMaterial;
    }

    public void setMaterial(boolean isMaterial) {
        this.isMaterial = isMaterial;
    }

    public void setAutoMove(boolean isAutoMove) {
        this.isAutoMove = isAutoMove;
    }

    public boolean isAutoMove() {
        return isAutoMove;
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(paintX, paintY, radius, backgroundPaint);
        super.onDraw(canvas);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMaterial) {
            paintX = event.getX();
            paintY = event.getY();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if (isMaterial) {
            if (isAutoMove)
                startMoveRoundAnimator();
            else
                startRoundAnimator();
            /*
            // To Animator
            if (isAttachedToWindow() && isLaidOut()) {
            } else {
                // Immediately move the thumb to the new position.
                cancelPositionAnimator();
            }*/
        }
        return super.performClick();
    }

    /**
     * =============================================================================================
     * The Animator methods
     * =============================================================================================
     */

    /**
     * Start Round Animator
     */
    private void startRoundAnimator() {
        float start, end, height, width, pStart, pEnd;
        long time = (long) (ANIMATION_TIME * 1.85);

        //Height Width
        height = getHeight();
        width = getWidth();

        //Start End
        if (height < width) {
            start = height;
            end = width;
            pStart = paintY;
            pEnd = paintX;
        } else {
            start = width;
            end = height;
            pStart = paintX;
            pEnd = paintY;
        }

        float startRadius = (start / 2 > pStart ? start - pStart : pStart) * 1.15f;
        float endRadius = (end / 2 > pEnd ? end - pEnd : pEnd) * 0.85f;

        //If The approximate square approximate square
        if (startRadius > endRadius) {
            startRadius = endRadius * 0.6f;
            endRadius = endRadius / 0.8f;
            time = (long) (time * 0.5);
        }

        cancelPositionAnimator();
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(this, mRadiusProperty, startRadius, endRadius),
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, ARGB_EVALUATOR, attributes.getColor(1), attributes.getColor(2))
        );
        // set Time
        mAnimatorSet.setDuration((long) (time / end * endRadius));
        mAnimatorSet.setInterpolator(ANIMATION_INTERPOLATOR);
        mAnimatorSet.start();
    }

    /**
     * Start Move Round Animator
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startMoveRoundAnimator() {
        float start, end, height, width, pStart, speed = 0.3f;
        long time = ANIMATION_TIME;

        //Height Width
        height = getHeight();
        width = getWidth();

        //Start End
        if (height < width) {
            start = height;
            end = width;
            pStart = paintY;
        } else {
            start = width;
            end = height;
            pStart = paintX;
        }
        start = start / 2 > pStart ? start - pStart : pStart;
        end = end * 0.8f / 2f;

        //If The approximate square approximate square
        if (start > end) {
            start = end * 0.6f;
            end = end / 0.8f;
            time = (long) (time * 0.65);
            speed = 1f;
        }

        //PaintX
        ObjectAnimator aPaintX = ObjectAnimator.ofFloat(this, mPaintXProperty, paintX, width / 2);
        aPaintX.setAutoCancel(true);
        //PaintY
        ObjectAnimator aPaintY = ObjectAnimator.ofFloat(this, mPaintYProperty, paintY, height / 2);
        aPaintY.setAutoCancel(true);
        //Set Time
        if (height < width) {
            aPaintX.setDuration(time);
            aPaintY.setDuration((long) (time * speed));
        } else {
            aPaintX.setDuration((long) (time * speed));
            aPaintY.setDuration(time);
        }

        //Radius
        ObjectAnimator aRadius = ObjectAnimator.ofFloat(this, mRadiusProperty, start, end);
        aRadius.setAutoCancel(true);
        aRadius.setDuration(time);
        //Background
        ObjectAnimator aBackground = ObjectAnimator.ofObject(this, mBackgroundColorProperty, ARGB_EVALUATOR, attributes.getColor(1), attributes.getColor(2));
        aBackground.setAutoCancel(true);
        aBackground.setDuration(time);

        cancelPositionAnimator();
        //AnimatorSet
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(aPaintX, aPaintY, aRadius, aBackground);
        mAnimatorSet.setInterpolator(ANIMATION_INTERPOLATOR);
        mAnimatorSet.start();
    }

    private void cancelPositionAnimator() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    /**
     * =============================================================================================
     * The custom properties
     * =============================================================================================
     */

    private Property<GeniusButton, Float> mPaintXProperty = new Property<GeniusButton, Float>(Float.class, "paintX") {
        @Override
        public Float get(GeniusButton object) {
            return object.paintX;
        }

        @Override
        public void set(GeniusButton object, Float value) {
            object.paintX = value;
        }
    };

    private Property<GeniusButton, Float> mPaintYProperty = new Property<GeniusButton, Float>(Float.class, "paintY") {
        @Override
        public Float get(GeniusButton object) {
            return object.paintY;
        }

        @Override
        public void set(GeniusButton object, Float value) {
            object.paintY = value;
        }
    };

    private Property<GeniusButton, Float> mRadiusProperty = new Property<GeniusButton, Float>(Float.class, "radius") {
        @Override
        public Float get(GeniusButton object) {
            return object.radius;
        }

        @Override
        public void set(GeniusButton object, Float value) {
            object.radius = value;
            invalidate();
        }
    };

    private Property<GeniusButton, Integer> mBackgroundColorProperty = new Property<GeniusButton, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(GeniusButton object) {
            return object.backgroundPaint.getColor();
        }

        @Override
        public void set(GeniusButton object, Integer value) {
            object.backgroundPaint.setColor(value);
        }
    };

}
