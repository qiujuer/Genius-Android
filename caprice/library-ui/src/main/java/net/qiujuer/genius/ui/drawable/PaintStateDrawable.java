/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/16/2015
 * Changed 02/16/2015
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
package net.qiujuer.genius.ui.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

/**
 * A drawable that changes it's Paint color depending on the ColorStateDrawable State
 * <p>
 * Subclasses should implement {@link #draw(android.graphics.Canvas, android.graphics.Paint)}
 * </p>
 */
public abstract class PaintStateDrawable extends ColorStateDrawable {
    private final Paint mPaint;

    public PaintStateDrawable(ColorStateList tintStateList) {
        super(tintStateList);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public void draw(Canvas canvas, int color, int alpha) {
        mPaint.setColor(color);
        mPaint.setAlpha(alpha);
        draw(canvas, mPaint);
    }

    /**
     * Get the Paint
     *
     * @return mPaint
     */
    public Paint getPaint() {
        return mPaint;
    }

    /**
     * Subclasses should implement this method to do the actual drawing
     *
     * @param canvas The current {@link android.graphics.Canvas} to draw into
     * @param paint  The {@link android.graphics.Paint} the Paint object that defines with the current
     *               {@link android.content.res.ColorStateList} color
     */
    public abstract void draw(Canvas canvas, Paint paint);
}
