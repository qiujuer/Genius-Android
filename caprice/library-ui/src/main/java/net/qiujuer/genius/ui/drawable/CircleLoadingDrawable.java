/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 10/15/2015
 * Changed 10/15/2015
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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * A drawable to draw loading
 */
public class CircleLoadingDrawable extends Drawable {
    private static final int FRAME_DURATION = 16;
    private static final int ANGLE_ADD = 4;
    private static final int MAX_ANGLE_SWEEP = 135;
    private static final int RING_SIZE = 4;

    private Paint mAnglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mBackgroundOval = new RectF();
    private RectF mAngleOval = new RectF();

    private boolean isRun;
    private float mStartAngle;
    private float mSweepAngle = MAX_ANGLE_SWEEP;

    private int mBackgroundColor = 0x22000000;
    private int[] mAngleColor = new int[]{0xcc000000, 0xfffe7865, 0xff842398};

    private int mAngleAdd = ANGLE_ADD - 2;
    private int mAngleColorIndex = 0;

    public CircleLoadingDrawable() {
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setStrokeWidth(RING_SIZE);
        mBackgroundPaint.setColor(mBackgroundColor);

        mAnglePaint.setStyle(Paint.Style.STROKE);
        mAnglePaint.setAntiAlias(true);
        mAnglePaint.setDither(true);
        mAnglePaint.setStrokeWidth(RING_SIZE);
        mAnglePaint.setColor(mAngleColor[0]);
    }

    public void setBackgroundBorderSize(float border) {
        mBackgroundPaint.setStrokeWidth(border);
    }

    public void setAngleBorderSize(float border) {
        mAnglePaint.setStrokeWidth(border);
    }

    public float getBackgroundBorderSize() {
        return mBackgroundPaint.getStrokeWidth();
    }

    public float getAngleBorderSize() {
        return mAnglePaint.getStrokeWidth();
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setAngleColor(int color) {
        setAngleColor(new int[]{color});
    }


    public void setAngleColor(int[] colors) {
        this.mAngleColor = colors;
        this.mAngleColorIndex = -1;
        setNextAngleColor();
    }

    public int[] getAngleColor() {
        return mAngleColor;
    }

    private void setNextAngleColor() {
        if (mAngleColor.length > 1) {
            mAngleColorIndex++;
            if (mAngleColorIndex >= mAngleColor.length)
                mAngleColorIndex = 0;

            mAnglePaint.setColor(mAngleColor[mAngleColorIndex]);
        } else {
            mAnglePaint.setColor(mAngleColor[0]);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        int center = Math.min(centerX, centerY);
        int areRadius = center - (((int) mBackgroundPaint.getStrokeWidth()) >> 1) - 1;
        mBackgroundOval.set(centerX - areRadius, centerY - areRadius, centerX + areRadius, centerY + areRadius);

        areRadius = center - (((int) mAnglePaint.getStrokeWidth()) >> 1) - 1;
        mAngleOval.set(centerX - areRadius, centerY - areRadius, centerX + areRadius, centerY + areRadius);
    }

    private final Runnable mAnim = new Runnable() {
        @Override
        public void run() {
            refresh();
            if (isRun) {
                scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DURATION);
            } else {
                unscheduleSelf(this);
            }
        }
    };

    public boolean isRun() {
        return isRun;
    }

    public void start() {
        if (!isRun) {
            isRun = true;
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION);
        }
    }

    public void stop() {
        if (isRun) {
            isRun = false;
            unscheduleSelf(mAnim);
            mAnglePaint.setColor(Color.TRANSPARENT);
            invalidateSelf();
        }
    }

    private void refresh() {
        final float angle = ANGLE_ADD;

        if ((mStartAngle + angle) > 360) {
            float eAngle = 360 - mStartAngle;
            mStartAngle = angle - eAngle;
        } else {
            mStartAngle += angle;
        }

        mSweepAngle += mAngleAdd;
        if (mSweepAngle > MAX_ANGLE_SWEEP) {
            mAngleAdd = -mAngleAdd;
        } else if (mSweepAngle < 0) {
            mAngleAdd = -mAngleAdd;
            mSweepAngle = 0;
            setNextAngleColor();
        }

        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRun) {
            canvas.drawArc(mBackgroundOval, 0, 360, false, mBackgroundPaint);
            canvas.drawArc(mAngleOval, mStartAngle, -mSweepAngle, false, mAnglePaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}