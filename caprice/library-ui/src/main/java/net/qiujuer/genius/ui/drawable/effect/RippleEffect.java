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
 * Ripple Draw Effect
 */
public class RippleEffect extends PressEffect {
    protected float mPaintX;
    protected float mPaintY;

    public RippleEffect() {
        mMinRadiusFactor = 0;
        mMaxRadiusFactor = 1;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mRadius > 0) {
            canvas.drawCircle(mPaintX, mPaintY, mRadius, paint);
        } else if (mAlpha > 0) {
            setPaintAlpha(paint, mAlpha);
            canvas.drawColor(paint.getColor());
        }
    }


    @Override
    public void animationExit(float factor) {
        super.animationExit(factor);
        mRadius = 0;
    }

    @Override
    public void touchDown(float dx, float dy) {
        mPaintX = dx;
        mPaintY = dy;

        float x = dx < mCenterX ? getWidth() : 0;
        float y = dy < mCenterY ? getHeight() : 0;
        float radius = (float) Math.sqrt((x - dx) * (x - dx) + (y - dy) * (y - dy));
        setMaxRadius(radius);
    }
}
