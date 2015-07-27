/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/26/2015
 * Changed 07/27/2015
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
 * Move Draw Effect
 */
public class AutoEffect extends PressEffect {
    protected float mDownX;
    protected float mDownY;

    protected float mPaintX;
    protected float mPaintY;

    private int mCircleAlpha;

    public AutoEffect() {
        mMaxAlpha = 172;
        mMinRadiusFactor = 0;
        mMaxRadiusFactor = 0.78f;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        int preAlpha = setPaintAlpha(paint, mAlpha);
        if (paint.getAlpha() > 0) {
            canvas.drawColor(paint.getColor());
        }

        if (mRadius > 0) {
            if (preAlpha < 255) {
                preAlpha = getCircleAlpha(preAlpha, paint.getAlpha());
                paint.setAlpha(preAlpha);
            }
            if (mCircleAlpha != 255)
                setPaintAlpha(paint, mCircleAlpha);
            canvas.drawCircle(mPaintX, mPaintY, mRadius, paint);
        }
    }

    @Override
    public void animationEnter(float factor) {
        super.animationEnter(factor);
        mPaintX = mDownX + (mCenterX - mDownX) * factor;
        mPaintY = mDownY + (mCenterY - mDownY) * factor;
    }

    @Override
    public void animationExit(float factor) {
        super.animationExit(factor);
        mRadius = mMaxRadius;
        mCircleAlpha = 255 - (int) (255 * factor);
    }

    @Override
    public void touchDown(float dx, float dy) {
        mPaintX = mDownX = dx;
        mPaintY = mDownY = dy;
        mCircleAlpha = 255;
    }

    private int getCircleAlpha(int preAlpha, int nowAlpha) {
        if (nowAlpha > preAlpha)
            return 0;
        int dAlpha = preAlpha - nowAlpha;
        return (255 * dAlpha) / (255 - nowAlpha);
    }

}
