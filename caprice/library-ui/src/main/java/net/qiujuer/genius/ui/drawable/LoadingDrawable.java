/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 10/16/2015
 * Changed 10/23/2015
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
package net.qiujuer.genius.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * A drawable to draw loading
 * The loading draw a Circle
 */
public abstract class LoadingDrawable extends Drawable implements android.graphics.drawable.Animatable, net.qiujuer.genius.ui.drawable.Animatable {
    protected static final int LINE_SIZE = 4;
    protected static int MIN_SIZE = 56;

    protected Paint mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private long mStartTime;
    private boolean mRun;

    private int[] mForegroundColor = new int[]{0xcc000000, 0xfffe7865, 0xff842398};
    private int mForegroundColorIndex = 0;

    private float mProgress;

    public LoadingDrawable() {
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setStrokeWidth(LINE_SIZE);
        mBackgroundPaint.setColor(0x32000000);

        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setAntiAlias(true);
        mForegroundPaint.setDither(true);
        mForegroundPaint.setStrokeWidth(LINE_SIZE);
        mForegroundPaint.setColor(mForegroundColor[0]);
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public LoadingDrawable(int minSize) {
        this();
        MIN_SIZE = minSize;
    }

    @Override
    public int getIntrinsicHeight() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        int size = (int) (maxLine * 2 + 10);
        return Math.max(size, MIN_SIZE);
    }

    @Override
    public int getIntrinsicWidth() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        int size = (int) (maxLine * 2 + 10);
        return Math.max(size, MIN_SIZE);
    }

    public void setBackgroundLineSize(float size) {
        mBackgroundPaint.setStrokeWidth(size);
    }

    public void setForegroundLineSize(float size) {
        mForegroundPaint.setStrokeWidth(size);
    }

    public float getBackgroundLineSize() {
        return mBackgroundPaint.getStrokeWidth();
    }

    public float getForegroundLineSize() {
        return mForegroundPaint.getStrokeWidth();
    }

    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public int getBackgroundColor() {
        return mBackgroundPaint.getColor();
    }

    public void setForegroundColor(int color) {
        setForegroundColor(new int[]{color});
    }

    public void setForegroundColor(int[] colors) {
        if (colors == null)
            return;
        this.mForegroundColor = colors;
        this.mForegroundColorIndex = -1;
        getNextForegroundColor();
    }

    public int[] getForegroundColor() {
        return mForegroundColor;
    }

    public int getNextForegroundColor() {
        if (mForegroundColor.length > 1) {
            mForegroundColorIndex++;
            if (mForegroundColorIndex >= mForegroundColor.length)
                mForegroundColorIndex = 0;

            mForegroundPaint.setColor(mForegroundColor[mForegroundColorIndex]);
        } else {
            mForegroundPaint.setColor(mForegroundColor[0]);
        }
        return mForegroundPaint.getColor();
    }

    /**
     * Get the loading progress
     *
     * @return Progress
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Set the draw progress
     * The progress include 0~1 float
     * On changed, stop animation draw
     *
     * @param progress Loading progress
     */
    public void setProgress(float progress) {
        if (progress < 0)
            mProgress = 0;
        else if (mProgress > 1)
            mProgress = 1;
        else
            mProgress = progress;
        stop();
        onProgressChange(mProgress);
        invalidateSelf();
    }

    private final Runnable mAnim = new Runnable() {
        @Override
        public void run() {
            if (mRun) {
                long curTime = SystemClock.uptimeMillis();
                refresh(mStartTime, curTime, ANIMATION_DURATION);
                invalidateSelf();
                scheduleSelf(this, curTime + FRAME_DURATION);
            } else {
                unscheduleSelf(this);
            }
        }
    };

    public boolean isRunning() {
        return mRun;
    }

    public void start() {
        if (!mRun) {
            mRun = true;
            mStartTime = SystemClock.uptimeMillis();
            scheduleSelf(mAnim, mStartTime + FRAME_DURATION);
        }
    }

    public void stop() {
        if (mRun) {
            mRun = false;
            unscheduleSelf(mAnim);
            invalidateSelf();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mRun || mProgress > 0)
            draw(canvas, mBackgroundPaint, mForegroundPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        boolean needRefresh = false;
        if (mBackgroundPaint.getColorFilter() != cf) {
            mBackgroundPaint.setColorFilter(cf);
            needRefresh = true;
        }

        if (mForegroundPaint.getColorFilter() != cf) {
            mForegroundPaint.setColorFilter(cf);
            needRefresh = true;
        }

        if (needRefresh)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        if (mBackgroundPaint.getXfermode() == null && mForegroundPaint.getXfermode() == null) {
            final int alpha = Color.alpha(mForegroundPaint.getColor());
            if (alpha == 0) {
                return PixelFormat.TRANSPARENT;
            }
            if (alpha == 255) {
                return PixelFormat.OPAQUE;
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }


    protected abstract void refresh(long startTime, long curTime, long timeLong);

    protected abstract void draw(Canvas canvas, Paint backgroundPaint, Paint foregroundPaint);

    protected abstract void onProgressChange(float progress);
}