/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 10/15/2015
 * Changed 12/06/2015
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

    @Override
    public int getIntrinsicHeight() {
        return (int) Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
    }

    @Override
    public int getIntrinsicWidth() {
        return Integer.MAX_VALUE;
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
    protected void refresh() {
        mForegroundProgress = mForegroundProgress + mSpeed;

        if (mForegroundProgress > 1) {
            mForegroundProgress = mForegroundProgress - 1;
            getNextForegroundColor();
        }

        float center = (mEndX - mStartX) * mForegroundProgress;


        float hrefWidth;
        if (mProgressType == 1) {
            float width;
            if (mForegroundProgress > 0.5f) {
                width = mMaxForegroundLine * (1 - mForegroundProgress) * 2;
            } else {
                width = mMaxForegroundLine * mForegroundProgress * 2;
            }

            hrefWidth = width / 2;
        } else if (mProgressType == 2) {
            float width = mMaxForegroundLine * mForegroundProgress;
            hrefWidth = width / 2;
            if ((center + hrefWidth) > mEndX) {
                hrefWidth = mEndX - center;
            }
        } else {
            hrefWidth = center;
            if ((hrefWidth + hrefWidth) > mMaxForegroundLine) {
                hrefWidth = mMaxForegroundLine / 2;
            }

            if ((center + hrefWidth) > mEndX) {
                hrefWidth = mEndX - center;
            }
        }

        mForegroundLeft = center - hrefWidth;
        mForegroundRight = center + hrefWidth;

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