package net.qiujuer.genius.ui.drawable.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Draw effect
 */
public class FloatEffect extends Effect {
    private float mEndRadius;
    private float mRadius = 0;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private int mRippleAlpha = 255;
    private int mEndRippleAlpha = 255;


    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mRadius != 0) {
            setPaintAlpha(paint, mRippleAlpha);
            canvas.drawCircle(mPaintX, mPaintY, mRadius, paint);
        }
    }

    @Override
    public void touchDown(float x, float y) {
        // Set this start point
        modulatePaint(x, y);

        // This color alpha
        mRippleAlpha = mEndRippleAlpha;
    }

    @Override
    public void animationIn(float factor) {
        mRadius = mEndRadius * factor;
        mPaintX = mDownX + (mCenterX - mDownX) * factor;
        mPaintY = mDownY + (mCenterY - mDownY) * factor;
    }

    @Override
    public void animationOut(float factor) {
        mRippleAlpha = mEndRippleAlpha - (int) (mEndRippleAlpha * factor);
    }

    @Override
    protected void onResize(float width, float height) {
        mCenterX = width / 2;
        mCenterY = height / 2;

        mEndRadius = Math.min(width, height) / 2;
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
}
