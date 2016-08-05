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
@SuppressWarnings("WeakerAccess")
public abstract class LoadingDrawable extends Drawable implements android.graphics.drawable.Animatable, net.qiujuer.genius.ui.drawable.Animatable {
    private static final int LINE_SIZE = 4;

    protected Paint mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mRun;

    private int[] mForegroundColor = new int[]{0xcc000000, 0xfffe7865, 0xff842398};
    private int mForegroundColorIndex = 0;

    protected float mProgress;

    public LoadingDrawable() {
        final Paint bPaint = mBackgroundPaint;
        bPaint.setStyle(Paint.Style.STROKE);
        bPaint.setAntiAlias(true);
        bPaint.setDither(true);
        bPaint.setStrokeWidth(LINE_SIZE);
        bPaint.setColor(0x32000000);

        final Paint fPaint = mForegroundPaint;
        fPaint.setStyle(Paint.Style.STROKE);
        fPaint.setAntiAlias(true);
        fPaint.setDither(true);
        fPaint.setStrokeWidth(LINE_SIZE);
        fPaint.setColor(mForegroundColor[0]);
    }

    @Override
    public int getIntrinsicHeight() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        return (int) (maxLine * 2);
    }

    @Override
    public int getIntrinsicWidth() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        return (int) (maxLine * 2);
    }

    public void setBackgroundLineSize(float size) {
        mBackgroundPaint.setStrokeWidth(size);
        onBoundsChange(getBounds());
    }

    public void setForegroundLineSize(float size) {
        mForegroundPaint.setStrokeWidth(size);
        onBoundsChange(getBounds());
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

    int getNextForegroundColor() {
        final int[] colors = mForegroundColor;
        final Paint fPaint = mForegroundPaint;
        if (colors.length > 1) {
            int index = mForegroundColorIndex + 1;
            if (index >= colors.length)
                index = 0;

            fPaint.setColor(colors[index]);
            mForegroundColorIndex = index;
        } else {
            fPaint.setColor(colors[0]);
        }
        return fPaint.getColor();
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
                refresh();
                invalidateSelf();
                scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DURATION);
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
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION);
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
        int count = canvas.save();
        canvas.clipRect(getBounds());

        final Paint bPaint = mBackgroundPaint;
        if (bPaint.getColor() != 0 && bPaint.getStrokeWidth() > 0)
            drawBackground(canvas, bPaint);

        final Paint fPaint = mForegroundPaint;
        if ((mRun || mProgress > 0) && fPaint.getColor() != 0 && fPaint.getStrokeWidth() > 0)
            drawForeground(canvas, fPaint);

        canvas.restoreToCount(count);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        boolean needRefresh = false;
        final Paint bPaint = mBackgroundPaint;
        if (bPaint.getColorFilter() != cf) {
            bPaint.setColorFilter(cf);
            needRefresh = true;
        }

        final Paint fPaint = mForegroundPaint;
        if (fPaint.getColorFilter() != cf) {
            fPaint.setColorFilter(cf);
            needRefresh = true;
        }

        if (needRefresh)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        final Paint bPaint = mBackgroundPaint;
        final Paint fPaint = mForegroundPaint;
        if (bPaint.getXfermode() == null && fPaint.getXfermode() == null) {
            final int alpha = Color.alpha(fPaint.getColor());
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


    protected abstract void refresh();

    protected abstract void drawBackground(Canvas canvas, Paint backgroundPaint);

    protected abstract void drawForeground(Canvas canvas, Paint foregroundPaint);

    protected abstract void onProgressChange(float progress);
}