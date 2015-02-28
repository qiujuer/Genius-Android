package net.qiujuer.genius.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * A drawable that changes it's Paint and color depending on the Drawable State
 * For SeekBarDrawable {@link net.qiujuer.genius.drawable.SeekBarDrawable}
 *
 * @Hide
 */
public abstract class SeekBarStatusDrawable extends Drawable {
    private final Paint mPaint;
    private int mAlpha = 255;

    private ColorStateList mTrackStateList;
    private int mTrackColor;

    private ColorStateList mScrubberStateList;
    private int mScrubberColor;

    private ColorStateList mThumbStateList;
    private int mThumbColor;


    public SeekBarStatusDrawable(ColorStateList trackStateList, ColorStateList scrubberStateList, ColorStateList thumbStateList) {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTrackStateList = trackStateList;
        mTrackColor = mTrackStateList.getDefaultColor();

        mScrubberStateList = scrubberStateList;
        mScrubberColor = mScrubberStateList.getDefaultColor();

        mThumbStateList = thumbStateList;
        mThumbColor = mThumbStateList.getDefaultColor();

    }

    @Override
    public boolean isStateful() {
        return (mTrackStateList.isStateful() && mScrubberStateList.isStateful() && mThumbStateList.isStateful()) || super.isStateful();
    }


    @Override
    public void draw(Canvas canvas) {
        final int trackAlpha = modulateAlpha(Color.alpha(mTrackColor));
        final int scrubberAlpha = modulateAlpha(Color.alpha(mScrubberColor));
        final int thumbAlpha = modulateAlpha(Color.alpha(mThumbColor));

        draw(canvas, mPaint, mTrackColor, trackAlpha, mScrubberColor, scrubberAlpha, mThumbColor, thumbAlpha);

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
    }

    /**
     * Set the Scrubber ColorStateList
     *
     * @param stateList ColorStateList
     */
    public void setScrubberColor(ColorStateList stateList) {
        mScrubberStateList = stateList;
    }

    /**
     * Set the Thumb ColorStateList
     *
     * @param stateList ColorStateList
     */
    public void setThumbColor(ColorStateList stateList) {
        mThumbStateList = stateList;
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
        return new int[]{mTrackColor, mScrubberColor, mThumbColor};
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
     * @param paint  The {@link android.graphics.Paint} the Paint object that defines with the current
     *               {@link android.content.res.ColorStateList} color
     */
    public abstract void draw(Canvas canvas, Paint paint, int trackColor, int trackAlpha, int scrubberColor, int scrubberAlpha, int thumbColor, int thumbAlpha);


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

            invalidateSelf();
            return true;
        }

        return false;
    }
}
