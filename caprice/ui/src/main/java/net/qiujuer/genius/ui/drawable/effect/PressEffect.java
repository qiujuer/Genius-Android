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
 * Press Draw Effect
 */
public class PressEffect extends EaseEffect {
    protected float mMinRadius;
    protected float mMaxRadius;
    protected float mRadius;

    protected float mCenterX;
    protected float mCenterY;

    protected float mMinRadiusFactor;
    protected float mMaxRadiusFactor;

    public PressEffect() {
        this(0.68f, 0.98f);
    }

    public PressEffect(float minRadiusFactor, float maxRadiusFactor) {
        this.mMinRadiusFactor = minRadiusFactor;
        this.mMaxRadiusFactor = maxRadiusFactor;
    }

    public void setMaxRadiusFactor(float factor) {
        this.mMaxRadiusFactor = factor;
    }

    public void setMinRadiusFactor(float factor) {
        this.mMinRadiusFactor = factor;
    }

    public float getMaxRadiusFactor() {
        return mMaxRadiusFactor;
    }

    public float getMinRadiusFactor() {
        return mMinRadiusFactor;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mRadius > 0 && mAlpha > 0) {
            setPaintAlpha(paint, mAlpha);
            canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);
        }
    }

    @Override
    public void animationEnter(float factor) {
        super.animationEnter(factor);
        mRadius = mMinRadius + (mMaxRadius - mMinRadius) * factor;
    }

    @Override
    public void animationExit(float factor) {
        super.animationExit(factor);
        mRadius = mMaxRadius + (mMinRadius - mMaxRadius) * factor;
    }

    @Override
    protected void onResize(float width, float height) {
        mCenterX = width / 2;
        mCenterY = height / 2;

        final float radius = Math.max(mCenterX, mCenterY);
        setMaxRadius(radius);
    }

    protected void setMaxRadius(float radius) {
        mMinRadius = radius * mMinRadiusFactor;
        mMaxRadius = radius * mMaxRadiusFactor;
    }
}
