/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/04/2015
 * Changed 08/23/2015
 * Version 3.0.0
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
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import net.qiujuer.genius.ui.Ui;

/**
 * A drawable that changes it's Paint and color depending on the Drawable State
 * For SeekBarDrawable {@link net.qiujuer.genius.ui.drawable.SeekBarDrawable}
 *
 * @Hide
 */
public abstract class SeekBarStateDrawable extends Drawable {
    private final Paint mPaint;
    private int mAlpha = 255;

    private ColorStateList mTrackStateList;
    private int mTrackColor;

    private ColorStateList mScrubberStateList;
    private int mScrubberColor;

    private ColorStateList mThumbStateList;
    private int mThumbColor;

    private int mCurTrackColor;
    private int mCurScrubberColor;
    private int mCurThumbColor;


    public SeekBarStateDrawable(ColorStateList trackStateList, ColorStateList scrubberStateList, ColorStateList thumbStateList) {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setTrackColor(trackStateList);
        setScrubberColor(scrubberStateList);
        setThumbColor(thumbStateList);

    }

    @Override
    public boolean isStateful() {
        return (mTrackStateList.isStateful() || mScrubberStateList.isStateful() || mThumbStateList.isStateful()) || super.isStateful();
    }


    @Override
    public void draw(Canvas canvas) {
        draw(canvas, mPaint, mCurTrackColor, mCurScrubberColor, mCurThumbColor);
    }

    @Override
    public boolean setState(int[] stateSet) {
        if (stateSet == null)
            return false;

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

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        updateCurColor();
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    /**
     * Set the Track ColorStateList
     *
     * @param stateList ColorStateList
     */
    public void setTrackColor(ColorStateList stateList) {
        mTrackStateList = stateList;
        mTrackColor = mTrackStateList.getDefaultColor();
        if (mAlpha < 255) {
            mCurTrackColor = Ui.modulateColorAlpha(mTrackColor, mAlpha);
        } else {
            mCurTrackColor = mTrackColor;
        }
    }

    /**
     * Get the Track ColorStateList
     */
    public ColorStateList getTrackColor() {
        return mTrackStateList;
    }

    /**
     * Set the Scrubber ColorStateList
     *
     * @param stateList ColorStateList
     */
    public void setScrubberColor(ColorStateList stateList) {
        mScrubberStateList = stateList;
        mScrubberColor = mScrubberStateList.getDefaultColor();

        if (mAlpha < 255) {
            mCurScrubberColor = Ui.modulateColorAlpha(mScrubberColor, mAlpha);
        } else {
            mCurScrubberColor = mScrubberColor;
        }
    }

    /**
     * Get the Scrubber ColorStateList
     */
    public ColorStateList getScrubberColor() {
        return mScrubberStateList;
    }


    /**
     * Set the Thumb ColorStateList
     *
     * @param stateList ColorStateList
     */
    public void setThumbColor(ColorStateList stateList) {
        mThumbStateList = stateList;
        mThumbColor = mThumbStateList.getDefaultColor();

        if (mAlpha < 255) {
            mCurThumbColor = Ui.modulateColorAlpha(mThumbColor, mAlpha);
        } else {
            mCurThumbColor = mThumbColor;
        }
    }


    /**
     * Get the Thumb ColorStateList
     */
    public ColorStateList getThumbColor() {
        return mThumbStateList;
    }


    /**
     * Get the Tint ColorStateList
     *
     * @return mTintStateList
     */
    public ColorStateList[] getColor() {
        return new ColorStateList[]{mTrackStateList, mScrubberStateList, mThumbStateList};
    }

    /**
     * Get The CurrentColor
     *
     * @return mCurrentColor
     */
    public int[] getCurrentColor() {
        return new int[]{mCurTrackColor, mCurScrubberColor, mCurThumbColor};
    }


    /**
     * Subclasses should implement this method to do the actual drawing
     *
     * @param canvas The current {@link android.graphics.Canvas} to draw into
     * @param paint  The {@link android.graphics.Paint} the Paint object that defines with the current
     *               {@link android.content.res.ColorStateList} color
     */
    public abstract void draw(Canvas canvas, Paint paint, int trackColor, int scrubberColor, int thumbColor);


    /**
     * Update tint color
     *
     * @param state tint stats
     * @return is changed
     */
    private boolean updateTintColor(int[] state) {
        final int trackColor = mTrackStateList.getColorForState(state, mTrackColor);
        final int scrubberColor = mScrubberStateList.getColorForState(state, mScrubberColor);
        final int thumbColor = mThumbStateList.getColorForState(state, mThumbColor);

        if (trackColor != mTrackColor || scrubberColor != mScrubberColor || thumbColor != mThumbColor) {
            mTrackColor = trackColor;
            mScrubberColor = scrubberColor;
            mThumbColor = thumbColor;
            updateCurColor();
            invalidateSelf();
            return true;
        }

        return false;
    }

    private void updateCurColor() {
        if (mAlpha < 255) {
            mCurTrackColor = Ui.modulateColorAlpha(mTrackColor, mAlpha);
            mCurScrubberColor = Ui.modulateColorAlpha(mScrubberColor, mAlpha);
            mCurThumbColor = Ui.modulateColorAlpha(mThumbColor, mAlpha);
        } else {
            mCurTrackColor = mTrackColor;
            mCurScrubberColor = mScrubberColor;
            mCurThumbColor = mThumbColor;
        }
    }
}
