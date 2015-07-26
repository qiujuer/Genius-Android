/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/24/2015
 * Changed 07/26/2015
 * Version 1.0.0
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
package net.qiujuer.genius.ui.drawable.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Float Draw effect
 */
public class FloatEffect extends Effect {
    private float mEndRadius;
    private float mRadius = 0;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private int mRippleAlpha = 255;
    private int mEndRippleAlpha = 255;


    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mRadius != 0) {
            setPaintAlpha(paint, mRippleAlpha);
            canvas.drawCircle(mPaintX, mPaintY, mRadius, paint);
        }
    }

    @Override
    public void touchDown(float x, float y) {
        // Set this start point
        modulatePaint(x, y);

        // This color alpha
        mRippleAlpha = mEndRippleAlpha;
    }

    @Override
    public void animationIn(float factor) {
        mRadius = mEndRadius * factor;
        mPaintX = mDownX + (mCenterX - mDownX) * factor;
        mPaintY = mDownY + (mCenterY - mDownY) * factor;
    }

    @Override
    public void animationOut(float factor) {
        mRippleAlpha = mEndRippleAlpha - (int) (mEndRippleAlpha * factor);
    }

    @Override
    protected void onResize(float width, float height) {
        mCenterX = width / 2;
        mCenterY = height / 2;

        mEndRadius = Math.min(width, height) / 2;
    }

    private void modulatePaint(float x, float y) {

        float dX = x - mCenterX;
        float dY = y - mCenterY;

        float percent = mEndRadius / (float) (Math.sqrt(dX * dX + dY * dY));

        if (percent < 1) {
            mPaintX = mDownX = mCenterX + dX * percent;
            mPaintY = mDownY = mCenterY + dY * percent;
        } else {
            mPaintX = mDownX = x;
            mPaintY = mDownY = y;
        }
    }
}
