package net.qiujuer.genius.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.os.SystemClock;

/**
 * <h1>SeekBarDrawable</h1>
 * <p>
 * Special {@link net.qiujuer.genius.drawable.SeekBarStatusDrawable} implementation
 * To draw the Track, Scrubber and Thumb circle.
 * </p>
 * <p>
 * It's special because it will stop drawing once the state is pressed/focused BUT only after a small delay.
 * </p>
 * <p>
 * This special delay is meant to help avoiding frame glitches while the {@link net.qiujuer.genius.drawable.MarkerDrawable} is added to the Window
 * </p>
 *
 * @hide
 */
public class SeekBarDrawable extends SeekBarStatusDrawable implements Animatable {
    public static final int DEFAULT_SIZE_DP = 12;

    private Point mPoint;
    private int mContentWidth;
    private int mNumSegments = 10;
    private float mTickDistance;
    private float mHotScale;

    private int mTrackSize;
    private int mScrubberSize;
    private int mThumbSize;
    private int mTickRadius;
    private int mTouchRadius;

    private boolean isRtl;
    private boolean isOpen;
    private boolean isRunning;

    public SeekBarDrawable(ColorStateList trackStateList, ColorStateList scrubberStateList, ColorStateList thumbStateList) {
        super(trackStateList, scrubberStateList, thumbStateList);
        mPoint = new Point();
    }

    public void setTouchRadius(int touchRadius) {
        this.mTouchRadius = touchRadius;
    }

    public void setHotScale(float scale) {
        mHotScale = scale;
        int hotWidth = getHotWidth();
        Rect bounds = getBounds();
        int x;
        if (isRtl) {
            x = bounds.right - mTouchRadius - hotWidth;
        } else {
            x = bounds.left + mTouchRadius + hotWidth;
        }
        mPoint.set(x, bounds.centerY());
    }

    public void setRtl(boolean isRtl) {
        this.isRtl = isRtl;
    }

    public void setTrackSize(int width) {
        this.mTrackSize = width;
    }

    public void setScrubberSize(int width) {
        this.mScrubberSize = width;
    }

    public void setThumbSize(int size) {
        this.mThumbSize = size;
    }

    public int getTouchRadius() {
        return mTouchRadius;
    }

    public float getHotScale() {
        return mHotScale;
    }

    public boolean isRtl() {
        return isRtl;
    }

    public int getTrackSize() {
        return mTrackSize;
    }

    public int getScrubberSize() {
        return mScrubberSize;
    }

    public int getThumbSize() {
        return mThumbSize;
    }

    public Point getPosPoint() {
        return mPoint;
    }

    public void copyTouchBounds(Rect rect) {
        Rect bounds = getBounds();
        int hotWidth = getHotWidth();
        int x;
        if (isRtl) {
            x = bounds.right - mTouchRadius - hotWidth;
        } else {
            x = bounds.left + mTouchRadius + hotWidth;
        }
        rect.set(x - mTouchRadius, bounds.top,
                x + mTouchRadius, bounds.bottom);
    }

    public void animateToPressed() {
        // Delay 100'''
        scheduleSelf(mOpener, SystemClock.uptimeMillis() + 100);
        isRunning = true;
    }

    public void animateToNormal() {
        isOpen = false;
        isRunning = false;
        unscheduleSelf(mOpener);
        invalidateSelf();
    }

    private Runnable mOpener = new Runnable() {
        @Override
        public void run() {
            isOpen = true;
            invalidateSelf();
            isRunning = false;
        }
    };

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        animateToNormal();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int trackColor, int trackAlpha, int scrubberColor, int scrubberAlpha, int thumbColor, int thumbAlpha) {
        float halfTrackSize = mTrackSize / 2;
        float halfScrubberSize = mScrubberSize / 2;

        if (isRtl) {
            draw(canvas, paint, thumbColor, thumbAlpha, trackColor, scrubberColor, trackAlpha, scrubberAlpha, halfTrackSize, halfScrubberSize);
        } else {
            draw(canvas, paint, thumbColor, thumbAlpha, scrubberColor, trackColor, scrubberAlpha, trackAlpha, halfScrubberSize, halfTrackSize);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mContentWidth = bounds.right - bounds.left - mTouchRadius - mTouchRadius;
        mTickDistance = mContentWidth / mNumSegments;
        setHotScale(mHotScale);
    }

    @Override
    public int getIntrinsicHeight() {
        return mTouchRadius * 2;
    }

    private int getHotWidth() {
        return (int) (mContentWidth * mHotScale);
    }

    private void draw(Canvas canvas, Paint paint, int thumbColor, int thumbAlpha, int colorLeft, int colorRight, int alphaLeft, int alphaRight, float halfLeft, float halfRight) {
        Rect bounds = getBounds();
        int thumbX = mPoint.x;
        int thumbY = mPoint.y;
        int startLeft = bounds.left + mTouchRadius;
        int startRight = bounds.right - mTouchRadius;

        mTickRadius = mScrubberSize;

        // Track
        paint.setColor(colorLeft);
        paint.setAlpha(alphaLeft);
        canvas.drawRect(startLeft, thumbY - halfLeft, thumbX, thumbY + halfLeft, paint);

        // Ticks
        for (int i = 0; i <= mNumSegments; i++) {
            float x = i * mTickDistance + startLeft;
            if (x > thumbX)
                break;
            canvas.drawCircle(x, thumbY, mTickRadius, paint);
        }

        // Scrubber
        paint.setColor(colorRight);
        paint.setAlpha(alphaRight);
        canvas.drawRect(thumbX, thumbY - halfRight, startRight, thumbY + halfRight, paint);

        // Ticks
        for (int i = 0; i <= mNumSegments; i++) {
            float x = startRight - i * mTickDistance;
            if (x < thumbX)
                break;
            canvas.drawCircle(x, thumbY, mTickRadius, paint);
        }

        // Thumb
        if (!isOpen) {
            paint.setColor(thumbColor);
            paint.setAlpha(thumbAlpha);
            float radius = (mThumbSize / 2f);
            canvas.drawCircle(thumbX, thumbY, radius, paint);
        }
    }
}
