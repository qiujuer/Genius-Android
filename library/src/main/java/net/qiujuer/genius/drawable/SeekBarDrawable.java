/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/20/2015
 * Changed 03/01/2015
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
package net.qiujuer.genius.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.os.SystemClock;

/**
 * <h1>SeekBarDrawable</h1>
 * <p>
 * Special {@link net.qiujuer.genius.drawable.SeekBarStatusDrawable} implementation
 * To draw the Track, Scrubber and Thumb circle.
 * </p>
 * <p>
 * It's special because it will stop drawing once the state is pressed/focused BUT only after a small delay.
 * </p>
 * <p>
 * This special delay is meant to help avoiding frame glitches while the {@link BalloonMarkerDrawable} is added to the Window
 * </p>
 *
 * @hide
 */
public class SeekBarDrawable extends SeekBarStatusDrawable implements Animatable {
    private Point mPoint;
    private int mContentWidth;
    private float mTickDistance;
    private float mHotScale;

    private int mNumSegments;
    private int mTrackStroke;
    private int mScrubberStroke;
    private int mThumbRadius;
    private int mTickRadius;
    private int mTouchRadius;

    private boolean isRtl;
    private boolean isOpen;
    private boolean isRunning;

    public SeekBarDrawable(ColorStateList trackStateList, ColorStateList scrubberStateList, ColorStateList thumbStateList) {
        super(trackStateList, scrubberStateList, thumbStateList);
        mPoint = new Point();
    }

    public void setHotScale(float scale) {
        mHotScale = scale;
        int hotWidth = getHotWidth();
        Rect bounds = getBounds();
        int x;
        if (isRtl) {
            x = bounds.right - mTouchRadius - hotWidth;
        } else {
            x = bounds.left + mTouchRadius + hotWidth;
        }
        mPoint.set(x, bounds.centerY());
    }

    public void setRtl(boolean isRtl) {
        this.isRtl = isRtl;
    }

    public void setTrackStroke(int trackStroke) {
        this.mTrackStroke = trackStroke;
    }

    public void setScrubberStroke(int scrubberStroke) {
        this.mScrubberStroke = scrubberStroke;
    }

    public void setThumbRadius(int thumbRadius) {
        this.mThumbRadius = thumbRadius;
    }

    public void setTouchRadius(int touchRadius) {
        this.mTouchRadius = touchRadius;
    }

    public void setTickRadius(int tickRadius) {
        this.mTickRadius = tickRadius;
    }

    public void setNumSegments(int numSegments) {
        this.mNumSegments = numSegments;
    }

    public float getHotScale() {
        return mHotScale;
    }

    public int getThumbRadius() {
        return mThumbRadius;
    }

    public int getTickRadius() {
        return mTickRadius;
    }

    public Point getPosPoint() {
        return mPoint;
    }

    public void copyTouchBounds(Rect rect) {
        Rect bounds = getBounds();
        int hotWidth = getHotWidth();
        int x;
        if (isRtl) {
            x = bounds.right - mTouchRadius - hotWidth;
        } else {
            x = bounds.left + mTouchRadius + hotWidth;
        }
        rect.set(x - mTouchRadius, bounds.top,
                x + mTouchRadius, bounds.bottom);
    }

    public void animateToPressed() {
        // Delay 100'''
        scheduleSelf(mOpener, SystemClock.uptimeMillis() + 100);
        isRunning = true;
    }

    public void animateToNormal() {
        isOpen = false;
        isRunning = false;
        unscheduleSelf(mOpener);
        invalidateSelf();
    }

    private Runnable mOpener = new Runnable() {
        @Override
        public void run() {
            isOpen = true;
            invalidateSelf();
            isRunning = false;
        }
    };

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        animateToNormal();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mContentWidth = bounds.right - bounds.left - mTouchRadius - mTouchRadius;
        mTickDistance = (float) mContentWidth / (float) mNumSegments;
        setHotScale(mHotScale);
    }

    @Override
    public int getIntrinsicHeight() {
        return mTouchRadius * 2;
    }

    private int getHotWidth() {
        return (int) (mContentWidth * mHotScale);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int trackColor, int trackAlpha, int scrubberColor, int scrubberAlpha, int thumbColor, int thumbAlpha) {
        float halfTrackStroke = mTrackStroke / 2;
        float halfScrubberStroke = mScrubberStroke / 2;

        if (isRtl) {
            draw(canvas, paint, thumbColor, thumbAlpha, trackColor, scrubberColor, trackAlpha, scrubberAlpha, halfTrackStroke, halfScrubberStroke);
        } else {
            draw(canvas, paint, thumbColor, thumbAlpha, scrubberColor, trackColor, scrubberAlpha, trackAlpha, halfScrubberStroke, halfTrackStroke);
        }
    }


    private void draw(Canvas canvas, Paint paint, int thumbColor, int thumbAlpha, int colorLeft, int colorRight, int alphaLeft, int alphaRight, float halfLeft, float halfRight) {
        Rect bounds = getBounds();
        int thumbX = mPoint.x;
        int thumbY = mPoint.y;
        int startLeft = bounds.left + mTouchRadius;
        int startRight = bounds.right - mTouchRadius;

        // Track
        paint.setColor(colorLeft);
        paint.setAlpha(alphaLeft);
        canvas.drawRect(startLeft, thumbY - halfLeft, thumbX, thumbY + halfLeft, paint);

        // Ticks
        if (mTickRadius > 0) {
            for (int i = 0; i <= mNumSegments; i++) {
                float x = i * mTickDistance + startLeft;
                if (x > thumbX)
                    break;
                canvas.drawCircle(x, thumbY, mTickRadius, paint);
            }
        }

        // Scrubber
        paint.setColor(colorRight);
        paint.setAlpha(alphaRight);
        canvas.drawRect(thumbX, thumbY - halfRight, startRight, thumbY + halfRight, paint);

        // Ticks
        if (mTickRadius > 0) {
            for (int i = 0; i <= mNumSegments; i++) {
                float x = startRight - i * mTickDistance;
                if (x <= thumbX)
                    break;
                canvas.drawCircle(x, thumbY, mTickRadius, paint);
            }
        }

        // Thumb
        if (!isOpen) {
            paint.setColor(thumbColor);
            paint.setAlpha(thumbAlpha);
            canvas.drawCircle(thumbX, thumbY, mThumbRadius, paint);
        }
    }
}
