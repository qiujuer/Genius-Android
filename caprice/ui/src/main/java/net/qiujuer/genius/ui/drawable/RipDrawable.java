package net.qiujuer.genius.ui.drawable;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class RipDrawable extends Drawable {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    private int mMaxDeepness = 24;
    private int mMinDeepness = 4;

    private Rect mFluCount = new Rect();

    private int mColor;

    private boolean mSmooth = true;

    public RipDrawable() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);

        mFluCount.top = 36;
        mFluCount.left = 25;
        mFluCount.right = 25;
        mFluCount.bottom = 36;
    }

    public void setFluCount(int left, int top, int right, int bottom) {
        mFluCount.set(left, top, right, bottom);
    }

    public void setDeepness(int min, int max) {
        mMinDeepness = min;
        mMaxDeepness = max;
    }

    public void setIsRandom(boolean random) {
        if (random) {
            if (mRandom == null)
                mRandom = new Random();
        } else {
            mRandom = null;
        }
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        if (mColor == color)
            return;
        mColor = color;
        mPaint.setColor(color);
        invalidateSelf();
    }

    public void setColorUnInvalidate(int color) {
        if (mColor == color)
            return;
        mColor = color;
        mPaint.setColor(color);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setSmooth(boolean smooth) {
        if (this.mSmooth != smooth) {
            this.mSmooth = smooth;
            initPath(getBounds());
        }
    }

    public boolean isSmooth() {
        return mSmooth;
    }

    public void setRandom(boolean isRandom) {
        if (isRandom) {
            if (mRandom == null)
                mRandom = new Random();
        } else {
            mRandom = null;
        }
    }

    public boolean isRandom() {
        return mRandom != null;
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, mPath, mPaint);
    }

    protected void draw(Canvas canvas, Path path, Paint paint) {
        canvas.drawPath(path, paint);
    }

    @Override
    public int getAlpha() {
        return mPaint.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        initPath(bounds);
    }

    protected void initPath(Rect bounds) {
        if (mSmooth)
            initPathSmooth(bounds.left, bounds.top, bounds.right, bounds.bottom);
        else
            initPath(bounds.left, bounds.top, bounds.right, bounds.bottom);
        invalidateSelf();
    }

    protected void initPath(int left, int top, int right, int bottom) {
        mPath.reset();
        mPath.moveTo(left, top);

        float tagSize;
        int flu = mFluCount.left;
        if (flu > 0) {
            tagSize = (bottom - top) / (float) flu;
            for (int i = 0; i < flu; i++) {
                if (i % 2 == 0) {
                    mPath.lineTo(left, top + i * tagSize);
                } else {
                    mPath.lineTo(left + getDeepness(), top + i * tagSize);
                }
            }
        }

        mPath.lineTo(left, bottom);

        flu = mFluCount.bottom;
        if (flu > 0) {
            tagSize = (right - left) / (float) flu;
            for (int i = 0; i < flu; i++) {
                if (i % 2 == 0) {
                    mPath.lineTo(left + i * tagSize, bottom);
                } else {
                    mPath.lineTo(left + i * tagSize, bottom - getDeepness());
                }
            }
        }

        mPath.lineTo(right, bottom);

        flu = mFluCount.right;
        if (flu > 0) {
            tagSize = (bottom - top) / (float) flu;
            for (int i = 0; i < flu; i++) {
                if (i % 2 == 0) {
                    mPath.lineTo(right, bottom - (i * tagSize));
                } else {
                    mPath.lineTo(right - getDeepness(), bottom - (i * tagSize));
                }
            }
        }

        mPath.lineTo(right, top);

        flu = mFluCount.top;
        if (flu > 0) {
            tagSize = (right - left) / (float) flu;
            for (int i = 0; i < flu; i++) {
                if (i % 2 == 0) {
                    mPath.lineTo(right - (i * tagSize), top);
                } else {
                    mPath.lineTo(right - (i * tagSize), top + getDeepness());
                }
            }
        }

        mPath.lineTo(left, top);
        mPath.close();
    }

    protected void initPathSmooth(int left, int top, int right, int bottom) {
        final int maxDeep = getMaxDeepness();
        left += (mFluCount.left > 0 ? maxDeep : 0);
        top += (mFluCount.top > 0 ? maxDeep : 0);
        right -= (mFluCount.right > 0 ? maxDeep : 0);
        bottom -= (mFluCount.bottom > 0 ? maxDeep : 0);

        float x, y;
        float deep = 0, tagSize;

        mPath.reset();
        mPath.moveTo(left, top);
        x = left;
        y = top;

        int flu = mFluCount.left;
        if (flu > 0) {
            tagSize = (bottom - top) / (float) (flu * 2);
            flu--;
            for (int i = 0; i < flu; i++) {

                if (i % 2 == 0) {
                    deep = -getDeepness();
                } else {
                    deep = getDeepness();
                }

                y += tagSize;
                mPath.quadTo(x + deep, y, x, y += tagSize);
            }
            mPath.quadTo(left + deep, bottom, left, bottom);
        } else {
            mPath.lineTo(left, bottom);
        }


        y = bottom;
        flu = mFluCount.bottom;
        if (flu > 0) {
            tagSize = (right - left) / (float) (flu * 2);
            flu--;
            for (int i = 0; i < flu; i++) {

                if (i % 2 == 0) {
                    deep = getDeepness();
                } else {
                    deep = -getDeepness();
                }

                x += tagSize;
                mPath.quadTo(x, y + deep, x += tagSize, y);
            }
            mPath.quadTo(right, bottom + deep, right, bottom);
        } else {
            mPath.lineTo(right, bottom);
        }


        x = right;

        flu = mFluCount.right;
        if (flu > 0) {
            tagSize = (bottom - top) / (float) (flu * 2);
            flu--;
            for (int i = 0; i < flu; i++) {

                if (i % 2 == 0) {
                    deep = getDeepness();
                } else {
                    deep = -getDeepness();
                }

                y -= tagSize;
                mPath.quadTo(x + deep, y, x, y -= tagSize);
            }
            mPath.quadTo(right + deep, top, right, top);
        } else {
            mPath.lineTo(right, top);
        }


        y = top;

        flu = mFluCount.top;
        if (flu > 0) {
            tagSize = (right - left) / (float) (flu * 2);
            flu--;
            for (int i = 0; i < flu; i++) {

                if (i % 2 == 0) {
                    deep = -getDeepness();
                } else {
                    deep = getDeepness();
                }

                x -= tagSize;
                mPath.quadTo(x, y + deep, x -= tagSize, y);
            }
            mPath.quadTo(left, top + deep, left, top);
        } else {
            mPath.lineTo(left, top);
        }


        mPath.close();
    }

    private Random mRandom = new Random();

    private int getDeepness() {
        if (mRandom != null) {
            return mMinDeepness + mRandom.nextInt(mMaxDeepness - mMinDeepness);
        } else {
            return (mMaxDeepness + mMinDeepness) / 2;
        }
    }

    private int getMaxDeepness() {
        if (mRandom != null) {
            return mMaxDeepness;
        } else {
            return (mMaxDeepness + mMinDeepness) / 2;
        }
    }
}
