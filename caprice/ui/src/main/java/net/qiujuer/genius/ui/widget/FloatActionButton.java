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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.FloatEffect;

/**
 * This is touch effect FloatActionButton
 * The button extend see{@link ImageView} widget
 * <p>
 * <p><strong>XML attributes</strong></p>
 * <p>
 * See {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_gBackgroundColor Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_gTouchColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_android_enabled Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_shadowColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_shadowDx Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_shadowDy Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_shadowRadius Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_shadowAlpha Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_gInterceptEvent Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#FloatActionButton_gTouchDurationRate Attributes}
 */
@SuppressWarnings("unused")
public class FloatActionButton extends ImageView implements TouchEffectDrawable.PerformClicker,
        TouchEffectDrawable.PerformLongClicker {
    private int mShadowRadius;
    private TouchEffectDrawable mTouchDrawable;
    private ColorStateList mBackgroundColor;

    public FloatActionButton(Context context) {
        this(context, null);
    }

    public FloatActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gFloatActionButtonStyle);
    }

    public FloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Genius_Widget_FloatActionButton);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(FloatActionButton.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(FloatActionButton.class.getName());
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null)
            return;

        final Context context = getContext();
        final Resources resource = getResources();
        final float density = resource.getDisplayMetrics().density;

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FloatActionButton, defStyleAttr, defStyleRes);

        ColorStateList bgColor = a.getColorStateList(R.styleable.FloatActionButton_gBackgroundColor);
        int touchColor = a.getColor(R.styleable.FloatActionButton_gTouchColor, Ui.TOUCH_PRESS_COLOR);
        boolean enabled = a.getBoolean(R.styleable.FloatActionButton_android_enabled, true);
        int shadowColor = a.getInt(R.styleable.FloatActionButton_shadowColor, 0xFF000000);
        float shadowDx = a.getDimension(R.styleable.FloatActionButton_shadowDx, density * Ui.X_OFFSET);
        float shadowDy = a.getDimension(R.styleable.FloatActionButton_shadowDy, density * Ui.Y_OFFSET);
        float shadowR = a.getDimension(R.styleable.FloatActionButton_shadowRadius, (density * Ui.SHADOW_RADIUS));
        int shadowAlpha = a.getInt(R.styleable.FloatActionButton_shadowAlpha, 0x20);
        float touchDurationRate = a.getFloat(R.styleable.FloatActionButton_gTouchDurationRate, 1.0f);
        // Load intercept event type, the default is intercept click event
        int interceptEvent = a.getInt(R.styleable.FloatActionButton_gInterceptEvent, 0x0001);
        a.recycle();

        // Enabled
        setEnabled(enabled);

        // BackgroundColor
        if (bgColor == null) {
            bgColor = UiCompat.getColorStateList(resource, R.color.g_default_float_action_bg);
        }

        // Background drawable
        final float maxShadowOffset = Math.max(shadowDx, shadowDy);

        mShadowRadius = (int) (shadowR + 0.5);
        mShadowRadius += maxShadowOffset;

        ShapeDrawable background = new ShapeDrawable(new OvalShadowShape(mShadowRadius,
                Ui.changeColorAlpha(shadowColor, 0x70)));

        // Background paint
        Paint paint = background.getPaint();

        if (!isInEditMode()) {
            paint.setShadowLayer(mShadowRadius - maxShadowOffset, shadowDx, shadowDy,
                    Ui.changeColorAlpha(shadowColor, shadowAlpha));
        }

        // The background initial before setBackgroundColor
        UiCompat.setBackground(this, background);

        // Set the background color
        setBackgroundColor(bgColor);

        // TouchDrawable
        mTouchDrawable = new TouchEffectDrawable(new FloatEffect(), ColorStateList.valueOf(touchColor));
        mTouchDrawable.setCallback(this);
        mTouchDrawable.setInterceptEvent(interceptEvent);
        mTouchDrawable.setEnterDuration(touchDurationRate);
        mTouchDrawable.setExitDuration(touchDurationRate);

        // We want set this LayerType type on Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        final int padding = mShadowRadius;
        // set padding so the inner image sits correctly within the shadow.
        setPadding(Math.max(padding, getPaddingLeft()),
                Math.max(padding, getPaddingTop()),
                Math.max(padding, getPaddingRight()),
                Math.max(padding, getPaddingBottom()));
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        // In this, to support Canvas.clipPath(),
        // must set layerType is View.LAYER_TYPE_SOFTWARE
        layerType = View.LAYER_TYPE_SOFTWARE;
        super.setLayerType(layerType, paint);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mBackgroundColor != null) {
            setBackgroundColor(mBackgroundColor.getColorForState(getDrawableState(), mBackgroundColor.getDefaultColor()));
        }
    }

    /**
     * Set background by {@link ColorStateList}
     * The color will apply to {@link Drawable} ShapeDrawable
     *
     * @param colorStateList ColorStateList
     */
    public void setBackgroundColor(ColorStateList colorStateList) {
        if (colorStateList != null && mBackgroundColor != colorStateList) {
            mBackgroundColor = colorStateList;
            setBackgroundColor(mBackgroundColor.getColorForState(getDrawableState(), mBackgroundColor.getDefaultColor()));
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(UiCompat.getColor(getResources(), colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            // set any fill 255 alpha color
            ((ShapeDrawable) getBackground())
                    .getPaint()
                    .setColor(Ui.changeColorAlpha(color, 0xFF));
        }
    }

    public void setTouchColor(int color) {
        mTouchDrawable.setColor(color);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

        left = Math.max(mShadowRadius, left);
        top = Math.max(mShadowRadius, top);
        right = Math.max(mShadowRadius, right);
        bottom = Math.max(mShadowRadius, bottom);

        super.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                + mShadowRadius * 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = mTouchDrawable;
        if (drawable != null) {
            drawable.setBounds(mShadowRadius, mShadowRadius, getWidth() - mShadowRadius, getHeight() - mShadowRadius);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mTouchDrawable;
        return (drawable != null && who == mTouchDrawable) || super.verifyDrawable(who);
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
    public boolean performClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.performClick(this) && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    public boolean performLongClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.performLongClick(this) && super.performLongClick();
        } else
            return super.performLongClick();
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
    public void postPerformLongClick() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                performLongClick();
            }
        };

        if (!this.post(runnable)) {
            performLongClick();
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
     * This extends {@link Shape} to apply Shadow
     */
    private static class OvalShadowShape extends Shape {
        private Paint mShadowPaint;
        private float mCenterX;
        private float mCenterY;
        private float mRadius;
        private int mShadowRadius;
        private int mFillColor;
        private RectF mRect = new RectF();

        /**
         * Returns the RectF that defines this rectangle's bounds.
         */
        protected final RectF rect() {
            return mRect;
        }


        OvalShadowShape(int shadowRadius, int fillColor) {
            super();
            mShadowRadius = shadowRadius;
            mFillColor = fillColor;
            mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadowPaint.setStyle(Paint.Style.FILL);
            mShadowPaint.setAntiAlias(true);
            mShadowPaint.setDither(true);
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);

            mRect.set(0, 0, width, height);

            mCenterX = width / 2;
            mCenterY = height / 2;
            mRadius = Math.min(mCenterX, mCenterY);

            RadialGradient radialGradient = new RadialGradient(mCenterX, mCenterY,
                    mRadius, new int[]{mFillColor, 0x00ffffff},
                    new float[]{0.65f, 1}, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(radialGradient);

        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mShadowPaint);
            canvas.drawCircle(mCenterX, mCenterY, mRadius - mShadowRadius, paint);
        }
    }
}
