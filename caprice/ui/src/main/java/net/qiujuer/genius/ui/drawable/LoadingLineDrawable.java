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
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A drawable to draw loading form Line Type
 */
public class LoadingLineDrawable extends LoadingDrawable {
    private float mCenterY;
    private float mStartX;
    private float mEndX;

    private float mMaxForegroundLine = 400;
    private float mForegroundLeft;
    private float mForegroundRight;
    private float mForegroundProgress;

    private float mSpeed = 0.008f;
    private int mProgressType = 1;

    public LoadingLineDrawable() {
        super();
    }

    public LoadingLineDrawable(float speed) {
        super();
        mSpeed = speed;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
    }

    @Override
    public int getIntrinsicWidth() {
        return Short.MAX_VALUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.left == 0 && bounds.top == 0 && bounds.right == 0 && bounds.bottom == 0) {
            return;
        }

        mStartX = bounds.left;
        mEndX = bounds.right;

        mCenterY = bounds.centerY();

        mMaxForegroundLine = (mEndX - mStartX) * 0.5f;

        // in this we update the progress
        if (mProgress != 0) {
            onProgressChange(mProgress);
        }
    }

    @Override
    int getNextForegroundColor() {
        mProgressType++;
        if (mProgressType > 3)
            mProgressType = 1;
        return super.getNextForegroundColor();
    }

    @Override
    protected void onProgressChange(float progress) {
        mForegroundLeft = mStartX;
        mForegroundRight = mStartX + ((mEndX - mStartX) * progress);
    }


    @Override
    protected void onRefresh() {
        mForegroundProgress = mForegroundProgress + mSpeed;

        if (mForegroundProgress > 1) {
            mForegroundProgress = mForegroundProgress - 1;
            getNextForegroundColor();
        }

        final float progress = mForegroundProgress;
        final float maxLine = mMaxForegroundLine;
        final float centerOffset = (mEndX - mStartX) * progress;
        float centerX = mStartX + centerOffset;


        float hrefWidth;
        if (mProgressType == 1) {
            // in this on progress=0.5 the line have max value
            if (progress > 0.5f) {
                hrefWidth = maxLine * (1 - progress);
            } else {
                hrefWidth = maxLine * progress;
            }
        } else if (mProgressType == 2) {
            // in this the line up to maxLine, then slide to the end, stick to the end until it ends.
            float width = maxLine * progress;
            hrefWidth = width / 2;
            if ((centerX + hrefWidth) > mEndX) {
                hrefWidth = mEndX - centerX;
            }
        } else {
            // in this the adhesion increases with the head until the maximum value,
            // sliding to the end, and the end of the adhesive tail.
            if ((centerOffset + centerOffset) > maxLine) {
                hrefWidth = maxLine / 2;
            } else {
                hrefWidth = centerOffset;
            }
            // if > the end, we cut it
            if ((centerX + hrefWidth) > mEndX) {
                hrefWidth = mEndX - centerX;
            }
        }

        mForegroundLeft = centerX - hrefWidth;
        mForegroundRight = centerX + hrefWidth;

    }

    @Override
    protected void drawBackground(Canvas canvas, Paint backgroundPaint) {
        canvas.drawLine(mStartX, mCenterY, mEndX, mCenterY, backgroundPaint);
    }

    @Override
    protected void drawForeground(Canvas canvas, Paint foregroundPaint) {
        canvas.drawLine(mForegroundLeft, mCenterY, mForegroundRight, mCenterY, foregroundPaint);
    }
}