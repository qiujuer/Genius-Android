/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/29/2014
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

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
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

import net.qiujuer.genius.R;
import net.qiujuer.genius.widget.attribute.Attributes;
import net.qiujuer.genius.widget.attribute.CheckBoxAttributes;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * This is CheckBox widget
 * The widget extend view widget
 */
public class GeniusCheckBox extends View implements Checkable, Attributes.AttributeChangeListener {
    private static final boolean IS_HEIGHT_JELLY_BEAN_MR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANIMATION_DURATION = 250;

    // Animator
    private ObjectAnimator mAnimator;
    private AnimatorProperty mCurProperty = new AnimatorProperty();

    private int mUnCheckedPaintColor = Attributes.DEFAULT_COLORS[4];
    private int mCheckedPaintColor = Attributes.DEFAULT_COLORS[2];

    private Paint mCirclePaint;
    private Paint mRingPaint;
    private RectF mOval;
    private float mCenterX, mCenterY;

    private CheckBoxAttributes mAttributes;

    private boolean mChecked;
    private boolean mIsAttachWindow;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;

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
            mAttributes = new CheckBoxAttributes(this, getResources());

        boolean enable = isEnabled();
        boolean check = isChecked();

        if (attrs != null) {
            // Load attributes
            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.GeniusCheckBox, defStyle, 0);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusCheckBox_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setTheme(customTheme, getResources());

            // getting custom attributes
            mAttributes.setRingWidth(a.getDimensionPixelSize(R.styleable.GeniusCheckBox_g_ringWidth, mAttributes.getRingWidth()));
            mAttributes.setCircleRadius(a.getDimensionPixelSize(R.styleable.GeniusCheckBox_g_circleRadius, mAttributes.getCircleRadius()));
            mAttributes.setCustomCircleRadius(mAttributes.getCircleRadius() != Attributes.INVALID);

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
            mRingPaint.setStyle(Paint.Style.STROKE);
            mRingPaint.setStrokeJoin(Paint.Join.ROUND);
            mRingPaint.setStrokeCap(Paint.Cap.ROUND);
            mRingPaint.setAntiAlias(true);
            mRingPaint.setDither(true);
        }
        mRingPaint.setStrokeWidth(mAttributes.getRingWidth());

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
            int areRadius = center - (mAttributes.getRingWidth() + 1) / 2;

            if (isRtl())
                mCenterX = w - center - paddingRight;
            else
                mCenterX = center + paddingLeft;
            mCenterY = center + paddingTop;

            if (mOval == null)
                mOval = new RectF(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            else {
                mOval.set(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            }

            if (!mAttributes.isCustomCircleRadius())
                mAttributes.setCircleRadius(center - mAttributes.getRingWidth() * 2);
            else if (mAttributes.getCircleRadius() > center)
                mAttributes.setCircleRadius(center);

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
        setAnimatorValue(isChecked());
    }

    private void setAnimatorValue(boolean checked) {
        if (checked) {
            mCurProperty.mCircleColor = mCheckedPaintColor;
            mCurProperty.mSweepAngle = 360;
        } else {
            mCurProperty.mCircleColor = mUnCheckedPaintColor;
            mCurProperty.mSweepAngle = 0;
        }
        invalidate();
    }

    private void setAnimatorValue(AnimatorProperty property) {
        mCurProperty = property;
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

        mCirclePaint.setColor(mCurProperty.mCircleColor);
        canvas.drawCircle(mCenterX, mCenterY, mAttributes.getCircleRadius(), mCirclePaint);

        if (mOval != null) {
            mRingPaint.setColor(mUnCheckedPaintColor);
            canvas.drawArc(mOval, 225, 360, false, mRingPaint);
            mRingPaint.setColor(mCheckedPaintColor);
            canvas.drawArc(mOval, 225, mCurProperty.mSweepAngle, false, mRingPaint);
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

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isAttachedToWindow() && isLaidOut())
                    || (mIsAttachWindow && mOval != null)) {
                // To Animator
                animateCheckedState(checked);
            } else {
                setAnimatorValue(checked);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isRtl() {
        return IS_HEIGHT_JELLY_BEAN_MR1 &&
                (getLayoutDirection() == LAYOUT_DIRECTION_RTL);
        // return IS_HEIGHT_JELLY_BEAN_MR1 &&
        //        (ViewCompat.getLayoutDirection(this) == LAYOUT_DIRECTION_RTL);
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @Override
    public CheckBoxAttributes getAttributes() {
        return mAttributes;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * =============================================================================================
     * The Animate
     * =============================================================================================
     */

    private void animateCheckedState(boolean newCheckedState) {
        AnimatorProperty property = new AnimatorProperty();
        if (newCheckedState) {
            property.mSweepAngle = 360;
            property.mCircleColor = mCheckedPaintColor;
        } else {
            property.mSweepAngle = 0;
            property.mCircleColor = mUnCheckedPaintColor;
        }

        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofObject(this, ANIM_VALUE, new AnimatorEvaluator(mCurProperty), property);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                mAnimator.setAutoCancel(true);
            mAnimator.setDuration(ANIMATION_DURATION);
            mAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
        } else {
            mAnimator.cancel();
            mAnimator.setObjectValues(property);
        }
        mAnimator.start();
    }

    /**
     * =============================================================================================
     * The custom properties
     * =============================================================================================
     */

    private final static class AnimatorProperty {
        private float mSweepAngle;
        private int mCircleColor;
    }

    private final static class AnimatorEvaluator implements TypeEvaluator<AnimatorProperty> {
        private final AnimatorProperty mProperty;

        public AnimatorEvaluator(AnimatorProperty property) {
            mProperty = property;
        }

        @Override
        public AnimatorProperty evaluate(float fraction, AnimatorProperty startValue, AnimatorProperty endValue) {
            // Values
            mProperty.mSweepAngle = (int) (startValue.mSweepAngle + (endValue.mSweepAngle - startValue.mSweepAngle) * fraction);

            // Color
            int startA = (startValue.mCircleColor >> 24) & 0xff;
            int startR = (startValue.mCircleColor >> 16) & 0xff;
            int startG = (startValue.mCircleColor >> 8) & 0xff;
            int startB = startValue.mCircleColor & 0xff;

            int endA = (endValue.mCircleColor >> 24) & 0xff;
            int endR = (endValue.mCircleColor >> 16) & 0xff;
            int endG = (endValue.mCircleColor >> 8) & 0xff;
            int endB = endValue.mCircleColor & 0xff;

            mProperty.mCircleColor = (startA + (int) (fraction * (endA - startA))) << 24 |
                    (startR + (int) (fraction * (endR - startR))) << 16 |
                    (startG + (int) (fraction * (endG - startG))) << 8 |
                    (startB + (int) (fraction * (endB - startB)));

            return mProperty;
        }
    }

    private final static Property<GeniusCheckBox, AnimatorProperty> ANIM_VALUE = new Property<GeniusCheckBox, AnimatorProperty>(AnimatorProperty.class, "animValue") {
        @Override
        public AnimatorProperty get(GeniusCheckBox object) {
            return object.mCurProperty;
        }

        @Override
        public void set(GeniusCheckBox object, AnimatorProperty value) {
            object.setAnimatorValue(value);
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
