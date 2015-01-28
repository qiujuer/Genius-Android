/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/29/2014
 * Changed 01/27/2015
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

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Checkable;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * This is CheckBox widget
 * The widget extend view widget
 */
public class GeniusCheckBox extends View implements Checkable, Attributes.AttributeChangeListener {
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final int THUMB_ANIMATION_DURATION = 250;
    private static final int RING_WIDTH = 4;
    public static final int AUTO_CIRCLE_RADIUS = -1;

    // Animator
    private AnimatorSet mAnimatorSet;
    private float mSweepAngle;
    private int mCircleColor;

    private int mUnCheckedPaintColor = Attributes.DEFAULT_COLORS[4];
    private int mCheckedPaintColor = Attributes.DEFAULT_COLORS[2];

    private boolean mChecked;
    private boolean mIsAttachWindow;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private RectF mOval;
    private Paint mCirclePaint;
    private Paint mRingPaint;

    private float mCenterX, mCenterY;
    private boolean mCustomCircleRadius;
    private int mCircleRadius = AUTO_CIRCLE_RADIUS;
    private int mRingWidth = RING_WIDTH;

    private Attributes mAttributes;

    public GeniusCheckBox(Context context) {
        super(context);
        init(null, 0);
    }

    public GeniusCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GeniusCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        if (mAttributes == null)
            mAttributes = new Attributes(this, getResources());

        boolean enable = isEnabled();
        boolean check = isChecked();

        if (attrs != null) {
            // Load attributes
            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.GeniusCheckBox, defStyle, 0);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusCheckBox_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setThemeSilent(customTheme, getResources());

            // getting custom attributes
            mRingWidth = a.getDimensionPixelSize(R.styleable.GeniusCheckBox_g_ringWidth, mRingWidth);
            mCircleRadius = a.getDimensionPixelSize(R.styleable.GeniusCheckBox_g_circleRadius, mCircleRadius);
            mCustomCircleRadius = mCircleRadius != AUTO_CIRCLE_RADIUS;

            check = a.getBoolean(R.styleable.GeniusCheckBox_g_checked, false);
            enable = a.getBoolean(R.styleable.GeniusCheckBox_g_enabled, true);

            a.recycle();
        }
        // To check call performClick()
        setOnClickListener(null);

        // Refresh display with current params
        refreshDrawableState();

        // Init
        initPaint();
        initSize();
        initColor();

        // Init
        setEnabled(enable);
        setChecked(check);
    }

    private void initPaint() {
        if (mCirclePaint == null) {
            mCirclePaint = new Paint(ANTI_ALIAS_FLAG);
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setDither(true);
        }

        if (mRingPaint == null) {
            mRingPaint = new Paint();
            mRingPaint.setStrokeWidth(mRingWidth);
            mRingPaint.setStyle(Paint.Style.STROKE);
            mRingPaint.setStrokeJoin(Paint.Join.ROUND);
            mRingPaint.setStrokeCap(Paint.Cap.ROUND);
            mRingPaint.setAntiAlias(true);
            mRingPaint.setDither(true);
        }
    }

    private void initSize() {
        int w = getWidth();
        int h = getHeight();

        if (w == 0)
            w = getMeasuredWidth();
        if (h == 0)
            h = getMeasuredHeight();

        if (w > 0 && h > 0) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int contentWidth = w - paddingLeft - paddingRight;
            int contentHeight = h - paddingTop - paddingBottom;

            int center = Math.min(contentHeight, contentWidth) / 2;
            int areRadius = center - (mRingWidth + 1) / 2;
            mCenterX = center + paddingLeft;
            mCenterY = center + paddingTop;

            if (mOval == null)
                mOval = new RectF(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            else {
                mOval.set(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            }

            if (!mCustomCircleRadius)
                mCircleRadius = center - mRingWidth * 2;
            else if (mCircleRadius > center)
                mCircleRadius = center;

            // Refresh view
            if (!isInEditMode()) {
                invalidate();
            }
        }
    }

    private void initColor() {
        if (mAttributes == null)
            return;
        if (isEnabled()) {
            mUnCheckedPaintColor = mAttributes.getColor(4);
            mCheckedPaintColor = mAttributes.getColor(2);
        } else {
            mUnCheckedPaintColor = mAttributes.getColor(5);
            mCheckedPaintColor = mAttributes.getColor(3);
        }
        setCircleColor(isChecked() ? mCheckedPaintColor : mUnCheckedPaintColor);
    }

    private void setSweepAngle(float value) {
        mSweepAngle = value;
        invalidate();
    }

    private void setCircleColor(int color) {
        mCircleColor = color;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Init this Layout size
        initSize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Init this Layout size
        initSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCirclePaint.setColor(mCircleColor);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mCirclePaint);

        if (mOval != null) {
            mRingPaint.setColor(mUnCheckedPaintColor);
            canvas.drawArc(mOval, 225, 360, false, mRingPaint);
            mRingPaint.setColor(mCheckedPaintColor);
            canvas.drawArc(mOval, 225, mSweepAngle, false, mRingPaint);
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            super.setEnabled(enabled);
            initColor();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachWindow = false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();

            // To Animator
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isAttachedToWindow() && isLaidOut())
                    || (mIsAttachWindow && mOval != null)) {
                animateThumbToCheckedState(checked);
            } else {
                // Immediately move the thumb to the new position.
                cancelPositionAnimator();
                setCircleColor(checked ? mCheckedPaintColor : mUnCheckedPaintColor);
                setSweepAngle(checked ? 360 : 0);
            }

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }
            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            mBroadcasting = false;
        }
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @Override
    public Attributes getAttributes() {
        return mAttributes;
    }

    public void setRingWidth(int width) {
        if (mRingWidth != width) {
            mRingWidth = width;
            mRingPaint.setStrokeWidth(mRingWidth);
            initSize();
        }
    }

    public void setCircleRadius(int radius) {
        if (mCircleRadius != radius) {
            if (radius < 0)
                mCustomCircleRadius = false;
            else {
                mCustomCircleRadius = true;
                mCircleRadius = radius;
            }
            initSize();
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * =============================================================================================
     * The Animate
     * =============================================================================================
     */

    private void animateThumbToCheckedState(boolean newCheckedState) {
        ObjectAnimator sweepAngleAnimator = ObjectAnimator.ofFloat(this, SWEEP_ANGLE, newCheckedState ? 360 : 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            sweepAngleAnimator.setAutoCancel(true);

        ObjectAnimator circleColorAnimator = newCheckedState ? ObjectAnimator.ofObject(this, CIRCLE_COLOR, ARGB_EVALUATOR, mUnCheckedPaintColor, mCheckedPaintColor) :
                ObjectAnimator.ofObject(this, CIRCLE_COLOR, ARGB_EVALUATOR, mCheckedPaintColor, mUnCheckedPaintColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            circleColorAnimator.setAutoCancel(true);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                sweepAngleAnimator,
                circleColorAnimator
        );
        // set Time
        mAnimatorSet.setDuration(THUMB_ANIMATION_DURATION);
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

    private static final Property<GeniusCheckBox, Float> SWEEP_ANGLE = new Property<GeniusCheckBox, Float>(Float.class, "sweepAngle") {
        @Override
        public Float get(GeniusCheckBox object) {
            return object.mSweepAngle;
        }

        @Override
        public void set(GeniusCheckBox object, Float value) {
            object.setSweepAngle(value);
        }
    };
    private static final Property<GeniusCheckBox, Integer> CIRCLE_COLOR = new Property<GeniusCheckBox, Integer>(Integer.class, "circleColor") {
        @Override
        public Integer get(GeniusCheckBox object) {
            return object.mCircleColor;
        }

        @Override
        public void set(GeniusCheckBox object, Integer value) {
            object.setCircleColor(value);
        }
    };

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkBox  The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(GeniusCheckBox checkBox, boolean isChecked);
    }
}
