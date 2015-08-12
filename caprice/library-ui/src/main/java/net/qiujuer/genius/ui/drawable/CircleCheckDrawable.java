/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/10/2015
 * Changed 08/12/2015
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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.Ui;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * This is CheckBox drawable
 * The draw is Circle by animation.
 */
public class CircleCheckDrawable extends CheckStateDrawable implements Animatable {
    private Paint mCirclePaint;
    private Paint mRingPaint;
    private RectF mOval = new RectF();
    private int mCenterX, mCenterY;

    private final Interpolator mInterpolator = new DecelerateInterpolator(0.8f);
    private long mStartTime;
    private int mDuration = ANIMATION_DURATION;
    private boolean mRunning = false;

    private int mBorderSize = 4;
    private int mIntervalSize = 4;
    private int mMarkSize = 50;
    private boolean mCustomMarkSize;

    private int mCircleRadius;

    private float mCurAngle;
    private int mCurColor;
    private int mCurColorAlpha;
    private int mUnCheckedColor;

    private float mCurrentScale = 0;
    private float mAnimationInitialValue;


    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     *
     * @param tintStateList ColorStateList
     */
    public CircleCheckDrawable(ColorStateList tintStateList) {
        super(tintStateList);

        mCirclePaint = new Paint(ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);

        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeJoin(Paint.Join.ROUND);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setDither(true);
        mRingPaint.setStrokeWidth(mBorderSize);

        onStateChange(getState());
        onStateChange(getColor(), mChecked, mChecked);
        initIntrinsic();
    }

    public void setBorderSize(int size) {
        if (mBorderSize != size) {
            mBorderSize = size;
            mRingPaint.setStrokeWidth(mBorderSize);
            initIntrinsic();
            invalidateSelf();
        }
    }

    public void setIntervalSize(int size) {
        if (mIntervalSize != size) {
            mIntervalSize = size;
            initIntrinsic();
            invalidateSelf();
        }
    }

    public void setMarkSize(int size, boolean isCustom) {
        if (mMarkSize != size || mCustomMarkSize != isCustom) {
            mMarkSize = size;
            mCustomMarkSize = isCustom;
            initIntrinsic();
            invalidateSelf();
        }
    }

    public int getBorderSize() {
        return mBorderSize;
    }

    public int getIntervalSize() {
        return mIntervalSize;
    }

    public int getMarkSize() {
        return mMarkSize;
    }

    private void initIntrinsic() {
        if (!mCustomMarkSize) {
            int minSize = (mBorderSize + mIntervalSize) * 2;
            Rect bounds = getBounds();
            int w = bounds.width();
            int h = bounds.height();

            if (w <= 0 || h <= 0) {
                int size = Math.max(w, h);
                if (size > 0)
                    mMarkSize = size;
            } else
                mMarkSize = Math.min(w, h);

            mMarkSize = Math.max(mMarkSize, minSize);
        }
    }

    @Override
    protected void onStateChange(int color, boolean oldChecked, boolean newChecked) {
        if (oldChecked != newChecked) {
            if (newChecked) {
                mCurColor = color;
            } else {
                mUnCheckedColor = color;
            }

            start();

        } else {
            mCurColor = mUnCheckedColor = color;

            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (!mCustomMarkSize) {
            initIntrinsic();
        }

        if (bounds.isEmpty())
            return;

        mCenterX = bounds.centerX();
        mCenterY = bounds.centerY();

        int minCenter = Math.min(bounds.width(), bounds.height()) >> 1;
        int areRadius = minCenter - ((mBorderSize + 1) >> 1);

        mOval.set(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
        mCircleRadius = minCenter - mBorderSize - mIntervalSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return mMarkSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return mMarkSize;
    }

    @Override
    public void start() {
        unscheduleSelf(mUpdater);
        mRunning = true;
        mAnimationInitialValue = mCurrentScale;
        float durationFactor = mChecked ? 1f - mCurrentScale : mCurrentScale;
        mDuration = (int) (ANIMATION_DURATION * durationFactor);
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mUpdater, mStartTime + FRAME_DURATION);
    }

    @Override
    public void stop() {
        unscheduleSelf(mUpdater);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        mRingPaint.setColor(mUnCheckedColor);
        canvas.drawArc(mOval, 0, 360, false, mRingPaint);

        if (mCurAngle > 0 && mCurColor != 0) {
            mRingPaint.setColor(mCurColor);
            canvas.drawArc(mOval, 0, mCurAngle, false, mRingPaint);

            mCirclePaint.setColor(mCurColor);
            mCirclePaint.setAlpha(mCurColorAlpha);
            canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mCirclePaint);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        final Paint circlePaint = mCirclePaint;
        final Paint ringPaint = mRingPaint;
        boolean needRefresh = false;
        if (circlePaint != null && circlePaint.getColorFilter() != cf) {
            circlePaint.setColorFilter(cf);
            needRefresh = true;
        }

        if (ringPaint != null && ringPaint.getColorFilter() != cf) {
            ringPaint.setColorFilter(cf);
            needRefresh = true;
        }

        if (needRefresh)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        final Paint circlePaint = mCirclePaint;
        final Paint ringPaint = mRingPaint;
        if (circlePaint.getXfermode() == null && ringPaint.getXfermode() == null) {
            final int alpha = Color.alpha(getColor());
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


    private final Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff < mDuration) {
                float interpolation = mInterpolator.getInterpolation((float) diff / (float) mDuration);
                scheduleSelf(mUpdater, currentTime + FRAME_DURATION);
                updateAnimation(interpolation);
            } else {
                unscheduleSelf(mUpdater);
                mRunning = false;
                updateAnimation(1f);
            }
        }
    };

    private void updateAnimation(float factor) {
        float initial = mAnimationInitialValue;
        float destination = mChecked ? 1f : 0f;
        mCurrentScale = initial + (destination - initial) * factor;

        // Values
        mCurAngle = 360 * mCurrentScale;
        int alpha = (int) (255 * mCurrentScale);
        mCurColorAlpha = Ui.modulateAlpha(Color.alpha(mCurColor), alpha);

        invalidateSelf();
    }
}
