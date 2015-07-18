package net.qiujuer.genius.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;


public class FloatDrawable extends TouchAnimateDrawable {
    private float mEndRadius;
    private float mRadius = 0;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private int mRippleColor;
    private int mRippleAlpha = 255;
    private int mEndRippleAlpha = 255;


    public FloatDrawable(int color) {
        super();
        mRippleColor = color;
        mPaint.setColor(mRippleColor);
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);

        mEndRippleAlpha = modulateAlpha(Color.alpha(mRippleColor));
        mRippleAlpha = mEndRippleAlpha;
    }

    @Override
    public void draw(Canvas canvas) {
        // Draw Ripple
        if (mRadius != 0) {
            mPaint.setAlpha(mRippleAlpha);

            // Canvas Clip
            canvas.save();
            canvas.clipRect(getBounds());
            canvas.drawCircle(mPaintX, mPaintY, mRadius, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onTouchDown(float x, float y) {
        // Set this start point
        modulatePaint(x, y);

        // This color alpha
        mRippleAlpha = mEndRippleAlpha;

        // Call start
        super.onTouchDown(x, y);
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

    @Override
    protected void onInAnimateUpdate(float factor) {
        mRadius = mEndRadius * factor;
        mPaintX = mDownX + (mCenterX - mDownX) * factor;
        mPaintY = mDownY + (mCenterY - mDownY) * factor;
    }

    @Override
    protected void onOutAnimateUpdate(float factor) {
        mRippleAlpha = mEndRippleAlpha - (int) (mEndRippleAlpha * factor);
    }

    @Override
    protected void onInAnimateEnd() {

    }

    @Override
    protected void onOutAnimateEnd() {

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mCenterX = bounds.centerX();
        mCenterY = bounds.centerY();

        mEndRadius = Math.min(bounds.right - bounds.left, bounds.bottom - bounds.top) >> 1;
    }
}
