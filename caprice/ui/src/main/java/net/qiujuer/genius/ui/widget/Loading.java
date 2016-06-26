/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/28/2015
 * Changed 05/10/2016
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
package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

/**
 * This is android loading view
 */
public class Loading extends View {
    private LoadingDrawable mDrawable;
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
            mDrawable = new LoadingCircleDrawable(resource.getDimensionPixelOffset(R.dimen.g_loading_minSize));
            mDrawable.setCallback(this);
            return;
        }

        final float density = resource.getDisplayMetrics().density;
        final int baseSize = (int) (density * 2);

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Loading, defStyleAttr, defStyleRes);

        int bgLineSize = a.getDimensionPixelOffset(R.styleable.Loading_gBackgroundLineSize, baseSize);
        int fgLineSize = a.getDimensionPixelOffset(R.styleable.Loading_gForegroundLineSize, baseSize);

        int bgColor = a.getColor(R.styleable.Loading_gBackgroundColor, resource.getColor(R.color.grey_300));
        int fgColorId = a.getResourceId(R.styleable.Loading_gForegroundColor, R.array.g_default_loading_fg);

        int lineStyle = a.getInt(R.styleable.Loading_gLineStyle, 1);
        boolean autoRun = a.getBoolean(R.styleable.Loading_gAutoRun, true);

        float progress = a.getFloat(R.styleable.Loading_gProgressFloat, 0);

        a.recycle();

        setLineStyle(lineStyle);
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

    public void start() {
        mDrawable.start();
        mNeedRun = false;
    }

    public void stop() {
        mDrawable.stop();
        mNeedRun = false;
    }

    public boolean isRunning() {
        return mDrawable.isRunning();
    }

    public void setBackgroundLineSize(float size) {
        mDrawable.setBackgroundLineSize(size);
    }

    public void setForegroundLineSize(float size) {
        mDrawable.setForegroundLineSize(size);
    }

    public float getBackgroundLineSize() {
        return mDrawable.getBackgroundLineSize();
    }

    public float getForegroundLineSize() {
        return mDrawable.getForegroundLineSize();
    }

    public void setBackgroundColor(int color) {
        mDrawable.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return mDrawable.getBackgroundColor();
    }

    public void setForegroundColor(int color) {
        setForegroundColor(new int[]{color});
    }

    public void setForegroundColor(int[] colors) {
        mDrawable.setForegroundColor(colors);
    }

    public int[] getForegroundColor() {
        return mDrawable.getForegroundColor();
    }

    public float getProgress() {
        return mDrawable.getProgress();
    }

    public void setProgress(float progress) {
        mDrawable.setProgress(progress);
    }

    public void setAutoRun(boolean autoRun) {
        mAutoRun = autoRun;
    }

    public boolean isAutoRun() {
        return mAutoRun;
    }

    public void setLineStyle(int style) {
        mDrawable = new LoadingCircleDrawable(getResources().getDimensionPixelOffset(R.dimen.g_loading_minSize));
        mDrawable.setCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int iHeight = mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        int iWidth = mDrawable.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();

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
            mDrawable.setBounds(paddingLeft, paddingTop, curW + paddingLeft, curH + paddingTop);
        } else if (curW > curH) {
            final int left = paddingLeft + ((curW - curH) >> 1);
            mDrawable.setBounds(left, paddingTop, curH + left, curH + paddingTop);
        } else if (curW < curH) {
            final int top = paddingTop + ((curH - curW) >> 1);
            mDrawable.setBounds(paddingLeft, top, curW + paddingLeft, curW + top);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
    }

    private boolean mNeedRun;

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        saveOrRecoveryRun(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        saveOrRecoveryRun(visibility);
    }

    private void saveOrRecoveryRun(int visibility) {
        if (mDrawable == null) {
            return;
        }
        if (visibility == VISIBLE) {
            if (mNeedRun) {
                start();
            }
        } else {
            if (mDrawable.isRunning()) {
                mNeedRun = true;
                mDrawable.stop();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoRun && mDrawable.getProgress() == 0) {
            if (getVisibility() == VISIBLE)
                mDrawable.start();
            else
                mNeedRun = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDrawable.stop();
    }
}
