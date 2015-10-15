/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 10/15/2015
 * Changed 10/16/2015
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
import android.graphics.RectF;

/**
 * A drawable to draw loading
 */
public class CircleLoadingDrawable extends LoadingDrawable {
    private static final int ANGLE_ADD = 4;
    private static final int MAX_ANGLE_SWEEP = 135;

    private RectF mBackgroundOval = new RectF();
    private RectF mForegroundOval = new RectF();

    private float mStartAngle;
    private float mSweepAngle;
    private int mAngleIncrement = 2;

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        int center = Math.min(centerX, centerY);
        int areRadius = center - (((int) mBackgroundPaint.getStrokeWidth()) >> 1) - 1;
        mBackgroundOval.set(centerX - areRadius, centerY - areRadius, centerX + areRadius, centerY + areRadius);

        areRadius = center - (((int) mForegroundPaint.getStrokeWidth()) >> 1) - 1;
        mForegroundOval.set(centerX - areRadius, centerY - areRadius, centerX + areRadius, centerY + areRadius);
    }

    @Override
    protected void refresh(long startTime, long curTime, long timeLong) {
        final float angle = ANGLE_ADD;

        if ((mStartAngle + angle) > 360) {
            float eAngle = 360 - mStartAngle;
            mStartAngle = angle - eAngle;
        } else {
            mStartAngle += angle;
        }

        mSweepAngle += mAngleIncrement;
        if (mSweepAngle > MAX_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement;
        } else if (mSweepAngle < 0) {
            mAngleIncrement = -mAngleIncrement;
            mSweepAngle = 0;
            getNextForegroundColor();
        }
    }

    @Override
    protected void draw(Canvas canvas, Paint backgroundPaint, Paint foregroundPaint) {
        canvas.drawArc(mBackgroundOval, 0, 360, false, backgroundPaint);
        canvas.drawArc(mForegroundOval, mStartAngle, -mSweepAngle, false, foregroundPaint);
    }
}