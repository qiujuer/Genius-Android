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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * A drawable that changes it's ColorStateList color depending on the Drawable State
 * <p>
 * Subclasses should implement {@link #draw(android.graphics.Canvas, int, int)}
 * </p>
 */
public abstract class ColorStateDrawable extends Drawable {
    private ColorStateList mTintStateList;
    private int mCurrentColor;
    private int mAlpha = 255;

    public ColorStateDrawable(ColorStateList tintStateList) {
        super();
        setColorStateList(tintStateList);
    }

    @Override
    public boolean isStateful() {
        return (mTintStateList.isStateful()) || super.isStateful();
    }


    @Override
    public void draw(Canvas canvas) {
        int alpha = modulateAlpha(Color.alpha(mCurrentColor));
        draw(canvas, mCurrentColor, alpha);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public boolean setState(int[] stateSet) {
        boolean changed = super.setState(stateSet);
        changed = updateTintColor(stateSet) || changed;
        return changed;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    /**
     * Set the Tint ColorStateList
     *
     * @param tintStateList ColorStateList
     */
    public void setColorStateList(ColorStateList tintStateList) {
        mTintStateList = tintStateList;
        mCurrentColor = tintStateList.getDefaultColor();
    }

    /**
     * Get the Tint ColorStateList
     *
     * @return mTintStateList
     */
    public ColorStateList getColor() {
        return mTintStateList;
    }

    /**
     * Get The CurrentColor
     *
     * @return mCurrentColor
     */
    public int getCurrentColor() {
        return mCurrentColor;
    }

    /**
     * Modulate color Alpha
     *
     * @param alpha color alpha
     * @return modulate colorAlpha and this alpha
     */
    protected int modulateAlpha(int alpha) {
        int scale = mAlpha + (mAlpha >> 7);
        return alpha * scale >> 8;
    }

    /**
     * Subclasses should implement this method to do the actual drawing
     *
     * @param canvas The current {@link android.graphics.Canvas} to draw into
     * @param color  The {@link android.graphics.Color} the color object that defines with the currentColor
     *               {@link android.content.res.ColorStateList} color
     * @param alpha  The the alpha is the modulateAlpha for paint {@link android.graphics.Paint}
     */
    public abstract void draw(Canvas canvas, int color, int alpha);


    /**
     * Update tint color
     *
     * @param state tint stats
     * @return is changed
     */
    private boolean updateTintColor(int[] state) {
        final int color = mTintStateList.getColorForState(state, mCurrentColor);
        if (color != mCurrentColor) {
            mCurrentColor = color;
            //We've changed states
            invalidateSelf();
            return true;
        }
        return false;
    }
}
