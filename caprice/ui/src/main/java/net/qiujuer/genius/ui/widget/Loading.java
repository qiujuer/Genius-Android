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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.qiujuer.genius.ui.drawable.LoadingLineDrawable;

/**
 * This is android loading view
 */
@SuppressWarnings("WeakerAccess")
public class Loading extends View {
    public static int STYLE_CIRCLE = 1;
    public static int STYLE_LINE = 2;

    private LoadingDrawable mLoadingDrawable;
    private boolean mAutoRun;

    public Loading(Context context) {
        super(context);
        init(null, R.attr.gLoadingStyle, R.style.Genius_Widget_Loading);
    }

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.gLoadingStyle, R.style.Genius_Widget_Loading);
    }

    public Loading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Genius_Widget_Loading);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Loading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final Context context = getContext();
        final Resources resource = getResources();

        if (attrs == null) {
            // default we init a circle style loading drawable
            setProgressStyle(STYLE_CIRCLE);
            return;
        }

        final float density = resource.getDisplayMetrics().density;
        // default size 2dp
        final int baseSize = (int) (density * 2);

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Loading, defStyleAttr, defStyleRes);

        int bgLineSize = a.getDimensionPixelOffset(R.styleable.Loading_gBackgroundLineSize, baseSize);
        int fgLineSize = a.getDimensionPixelOffset(R.styleable.Loading_gForegroundLineSize, baseSize);

        int bgColor = 0;// transparent color
        ColorStateList colorStateList = a.getColorStateList(R.styleable.Loading_gBackgroundColor);
        if (colorStateList != null)
            bgColor = colorStateList.getDefaultColor();
        int fgColorId = a.getResourceId(R.styleable.Loading_gForegroundColor, R.array.g_default_loading_fg);

        int style = a.getInt(R.styleable.Loading_gProgressStyle, 1);
        boolean autoRun = a.getBoolean(R.styleable.Loading_gAutoRun, true);

        float progress = a.getFloat(R.styleable.Loading_gProgressFloat, 0);

        a.recycle();

        setProgressStyle(style);
        setAutoRun(autoRun);
        setProgress(progress);

        setBackgroundLineSize(bgLineSize);
        setForegroundLineSize(fgLineSize);
        setBackgroundColor(bgColor);

        // Check for IDE preview render
        if (!isInEditMode()) {
            String type = resource.getResourceTypeName(fgColorId);
            try {
                switch (type) {
                    case "color":
                        setForegroundColor(resource.getColor(fgColorId));
                        break;
                    case "array":
                        setForegroundColor(resource.getIntArray(fgColorId));
                        break;
                    default:
                        setForegroundColor(resource.getIntArray(R.array.g_default_loading_fg));
                        break;
                }
            } catch (Exception e) {
                setForegroundColor(resource.getIntArray(R.array.g_default_loading_fg));
            }
        }
    }

    /**
     * Start the loading animation
     */
    public void start() {
        mLoadingDrawable.start();
        mNeedRun = false;
    }

    /**
     * Stop the loading animation
     */
    public void stop() {
        mLoadingDrawable.stop();
        mNeedRun = false;
    }

    /**
     * Check the loading is Running state
     *
     * @return Loading is Running
     */
    public boolean isRunning() {
        return mLoadingDrawable.isRunning();
    }

    /**
     * Set the Background line size,
     * the unit is px, if you set dp plx use {@link net.qiujuer.genius.ui.Ui#dipToPx(Resources, float)}  }
     *
     * @param size Background line size
     */
    public void setBackgroundLineSize(int size) {
        mLoadingDrawable.setBackgroundLineSize(size);
        invalidate();
        requestLayout();
    }

    /**
     * Set the Foreground line size,
     * the unit is px, if you set dp plx use {@link net.qiujuer.genius.ui.Ui#dipToPx(Resources, float)}  }
     *
     * @param size Foreground line size
     */
    public void setForegroundLineSize(int size) {
        mLoadingDrawable.setForegroundLineSize(size);
        invalidate();
        requestLayout();
    }

    /**
     * Get the background line size
     *
     * @return the size unit is px
     */
    public float getBackgroundLineSize() {
        return mLoadingDrawable.getBackgroundLineSize();
    }

    /**
     * Get the foreground line size
     *
     * @return the size unit is px
     */
    public float getForegroundLineSize() {
        return mLoadingDrawable.getForegroundLineSize();
    }

    /**
     * Set the background color, eg: "#0xffffff"
     * else you @see {@link #setBackgroundColorRes(int)}
     *
     * @param color color value
     */
    public void setBackgroundColor(int color) {
        mLoadingDrawable.setBackgroundColor(color);
        invalidate();
    }

    /**
     * Set the background color by resource id
     *
     * @param colorRes Color resource id
     */
    public void setBackgroundColorRes(int colorRes) {
        ColorStateList colorStateList = UiCompat.getColorStateList(getResources(), colorRes);
        if (colorStateList == null)
            setBackgroundColor(0);
        else
            setBackgroundColor(colorStateList.getDefaultColor());
    }

    /**
     * Get background color value
     *
     * @return Color
     */
    public int getBackgroundColor() {
        return mLoadingDrawable.getBackgroundColor();
    }

    /**
     * Set the Foreground color, eg: "#0xffffff"
     * else you can ues {@link #setForegroundColor(int[])}
     *
     * @param color color value
     */
    public void setForegroundColor(int color) {
        setForegroundColor(new int[]{color});
    }

    /**
     * Set the  Foreground color by color array
     *
     * @param colors Color array
     */
    public void setForegroundColor(int[] colors) {
        mLoadingDrawable.setForegroundColor(colors);
        invalidate();
    }

    /**
     * Get the Foreground color array
     *
     * @return Color array
     */
    public int[] getForegroundColor() {
        return mLoadingDrawable.getForegroundColor();
    }

    /**
     * Get the loading progress value, default "0"
     *
     * @return Progress value
     */
    public float getProgress() {
        return mLoadingDrawable.getProgress();
    }

    /**
     * Set the loading Progress, the default "0";
     * If you set the value, the loading will stop animation
     * The Progress between 0 to 1 float.
     *
     * @param progress Progress
     */
    public void setProgress(float progress) {
        mLoadingDrawable.setProgress(progress);
        invalidate();
    }

    /**
     * Set the run type, default is true
     * If set "True", the loading will auto running after onAttachedToWindow()
     * If set "False", you can call {@link #start()} to running animation
     * <p>
     * You can only set the method before onAttachedToWindow() method call.
     *
     * @param autoRun Auto run
     */
    public void setAutoRun(boolean autoRun) {
        mAutoRun = autoRun;
    }

    /**
     * Get the loading run type
     *
     * @return Bool
     */
    public boolean isAutoRun() {
        return mAutoRun;
    }

    /**
     * Change the loading style
     * You can set {@link #STYLE_CIRCLE} or {@link #STYLE_LINE} parameters
     *
     * @param style {@link #STYLE_CIRCLE} or {@link #STYLE_LINE}
     */
    public void setProgressStyle(int style) {
        LoadingDrawable drawable = null;
        if (style == STYLE_CIRCLE) {
            drawable = new LoadingCircleDrawable(getResources().getDimensionPixelOffset(R.dimen.g_loading_minSize));
        } else if (style == STYLE_LINE) {
            drawable = new LoadingLineDrawable(getResources().getDimensionPixelOffset(R.dimen.g_loading_minSize));
        }
        if (drawable == null) {
            throw new NullPointerException("LoadingDrawable is null, You can only set the STYLE_CIRCLE and STYLE_LINE parameters.");
        } else {
            drawable.setCallback(this);
            mLoadingDrawable = drawable;

            invalidate();
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int iHeight = mLoadingDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        int iWidth = mLoadingDrawable.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();

        int measuredWidth;
        int measuredHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = Math.min(widthSize, iWidth);
        } else {
            measuredWidth = iWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = Math.min(heightSize, iHeight);
        } else {
            measuredHeight = iHeight;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();

        int curW = w - paddingLeft - paddingRight;
        int curH = h - paddingTop - paddingBottom;

        if (curW == curH) {
            mLoadingDrawable.setBounds(paddingLeft, paddingTop, curW + paddingLeft, curH + paddingTop);
        } else if (curW > curH) {
            final int left = paddingLeft + ((curW - curH) >> 1);
            mLoadingDrawable.setBounds(left, paddingTop, curH + left, curH + paddingTop);
        } else if (curW < curH) {
            final int top = paddingTop + ((curH - curW) >> 1);
            mLoadingDrawable.setBounds(paddingLeft, top, curW + paddingLeft, curW + top);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mLoadingDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLoadingDrawable.draw(canvas);
    }

    private boolean mNeedRun;

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        changeRunStateByVisibility(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        changeRunStateByVisibility(visibility);
    }

    private void changeRunStateByVisibility(int visibility) {
        if (mLoadingDrawable == null) {
            return;
        }
        if (visibility == VISIBLE) {
            if (mNeedRun) {
                start();
            }
        } else {
            if (mLoadingDrawable.isRunning()) {
                mNeedRun = true;
                mLoadingDrawable.stop();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoRun && mLoadingDrawable.getProgress() == 0) {
            if (getVisibility() == VISIBLE)
                mLoadingDrawable.start();
            else
                mNeedRun = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLoadingDrawable.stop();
    }
}
