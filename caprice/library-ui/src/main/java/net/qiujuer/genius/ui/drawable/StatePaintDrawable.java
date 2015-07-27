/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/16/2015
 * Changed 07/24/2015
 * Version 2.1.0
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
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import net.qiujuer.genius.ui.GeniusUi;

import java.lang.reflect.Method;

/**
 * A drawable that changes it's Paint color depending on the {@link StateColorDrawable(ColorStateList)} State
 * <p>
 * Subclasses should implement {@link #draw(android.graphics.Canvas, android.graphics.Paint)}
 * </p>
 */
public abstract class StatePaintDrawable extends StateColorDrawable {
    private PorterDuffColorFilter mTintFilter;
    private ColorStateList mTint = null;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    protected final Paint mPaint;

    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     */
    public StatePaintDrawable(ColorStateList tintStateList) {
        super(tintStateList);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getColor());

        mTintFilter = updateTintFilter(mTintFilter, mTint, mTintMode);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        final Paint paint = mPaint;
        if (paint != null && paint.getColorFilter() != cf) {
            paint.setColorFilter(cf);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        final Paint p = mPaint;
        if (p.getXfermode() == null) {
            final int alpha = p.getAlpha();
            if (alpha == 0) {
                return PixelFormat.TRANSPARENT;
            }
            if (alpha == 255) {
                return PixelFormat.OPAQUE;
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }

    @Override
    public void setTintList(ColorStateList tint) {
        if (mTint != tint) {
            mTint = tint;
            mTintFilter = updateTintFilter(mTintFilter, tint, mTintMode);
            invalidateSelf();
        }
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        if (tintMode != mTintMode || tintMode.compareTo(mTintMode) != 0) {
            mTintMode = tintMode;
            mTintFilter = updateTintFilter(mTintFilter, mTint, tintMode);
            invalidateSelf();
        }
    }

    @Override
    public boolean isStateful() {
        return super.isStateful() || (mTint != null && mTint.isStateful());
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        boolean changed = super.onStateChange(stateSet);
        if (mTint != null && mTintMode != null) {
            mTintFilter = updateTintFilter(mTintFilter, mTint, mTintMode);
            return true;
        }
        return changed;
    }

    @Override
    protected void onColorChange(int color) {
        final Paint paint = mPaint;
        if (paint != null && paint.getColor() != color)
            paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        final Paint paint = mPaint;
        final int prevAlpha = paint.getAlpha();
        paint.setAlpha(GeniusUi.modulateAlpha(prevAlpha, getAlpha()));
        // only draw shape if it may affect output
        if (paint.getAlpha() != 0 || paint.getXfermode() != null /*|| paint.hasShadowLayer()*/) {
            final boolean clearColorFilter;
            if (mTintFilter != null && paint.getColorFilter() == null) {
                paint.setColorFilter(mTintFilter);
                clearColorFilter = true;
            } else {
                clearColorFilter = false;
            }

            // call draw
            draw(canvas, mPaint);

            if (clearColorFilter) {
                paint.setColorFilter(null);
            }
        }
        // restore
        paint.setAlpha(prevAlpha);
    }

    /**
     * Returns the Paint used to draw the shape.
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


    // other subclass could wack the Shader's localmatrix based on the
    // resize params (e.g. scaletofit, etc.). This could be used to scale
    // a bitmap to fill the bounds without needing any other special casing.

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter tintFilter, ColorStateList tint,
                                           PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }

        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        if (tintFilter == null) {
            return new PorterDuffColorFilter(color, tintMode);
        }

        //tintFilter.setColor(color);
        //tintFilter.setMode(tintMode);
        try {
            Class<PorterDuffColorFilter> tClass = (Class<PorterDuffColorFilter>) tintFilter.getClass();
            Method method = tClass.getMethod("setColor", Integer.class);
            method.invoke(tintFilter, color);

            method = tClass.getMethod("setMode", PorterDuff.Mode.class);
            method.invoke(tintFilter, tintMode);
            return tintFilter;
        } catch (Exception e) {
            return new PorterDuffColorFilter(color, tintMode);
        }
    }
}
