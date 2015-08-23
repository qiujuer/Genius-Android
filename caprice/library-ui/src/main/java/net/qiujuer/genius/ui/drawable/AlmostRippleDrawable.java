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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.Ui;

/**
 * This is a touch foreground ripple drawable extends to StatePaintDrawable
 */
public class AlmostRippleDrawable extends StatePaintDrawable implements Animatable {
    private float mCurrentScale = 0;
    private Interpolator mInterpolator;
    private long mStartTime;
    private boolean mReverse = false;
    private boolean mRunning = false;
    private int mDuration = ANIMATION_DURATION;
    private float mAnimationInitialValue;
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

    private int mBackgroundAlpha;
    private int mRippleAlpha;

    public AlmostRippleDrawable(ColorStateList tintStateList) {
        super(tintStateList);
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        final float scale = mCurrentScale;
        if (scale > 0) {
            Rect bounds = getBounds();

            float radius = (Math.min(bounds.width(), bounds.height()) / 2.0f);
            float x = bounds.centerX();
            float y = bounds.centerY();

            // Background
            if (scale != 1f && mBackgroundAlpha > 0) {
                paint.setAlpha(mBackgroundAlpha);
                canvas.drawCircle(x, y, radius, paint);
            }

            // Ripple
            if (mRippleAlpha > 0) {
                paint.setAlpha(mRippleAlpha);
                canvas.drawCircle(x, y, radius * scale, paint);
            }
        }
    }

    @Override
    protected void onColorChange(int color) {
        super.onColorChange(color);
        final int preAlpha = Color.alpha(color);
        mBackgroundAlpha = (Ui.modulateAlpha(preAlpha, 128));

        if (preAlpha < 255) {
            mRippleAlpha = getRippleAlpha(preAlpha, mBackgroundAlpha);
        } else {
            mRippleAlpha = preAlpha;
        }
    }

    private int getRippleAlpha(int preAlpha, int nowAlpha) {
        if (nowAlpha > preAlpha)
            return 0;
        int dAlpha = preAlpha - nowAlpha;
        return (255 * dAlpha) / (255 - nowAlpha);
    }

    @Override
    public boolean setState(int[] stateSet) {
        if (stateSet == null)
            return false;

        int[] oldState = getState();
        boolean oldPressed = false;
        for (int i : oldState) {
            if (i == android.R.attr.state_pressed) {
                oldPressed = true;
            }
        }
        // Call super
        boolean status = super.setState(stateSet);

        boolean focused = false;
        boolean pressed = false;
        for (int i : stateSet) {
            if (i == android.R.attr.state_focused) {
                focused = true;
            } else if (i == android.R.attr.state_pressed) {
                pressed = true;
            }
        }

        if (pressed) {
            animateToPressed();
        } else if (oldPressed) {
            animateToNormal();
        } else if (focused) {
            mCurrentScale = 1f;
            invalidateSelf();
        } else {
            // Other none show
            mCurrentScale = 0;
            invalidateSelf();
        }

        return status;
    }

    public void animateToPressed() {
        unscheduleSelf(mUpdater);
        if (mCurrentScale < 1f) {
            mReverse = false;
            mRunning = true;
            mAnimationInitialValue = mCurrentScale;
            float durationFactor = 1f - mAnimationInitialValue;
            mDuration = (int) (ANIMATION_DURATION * durationFactor);
            mStartTime = SystemClock.uptimeMillis();
            scheduleSelf(mUpdater, mStartTime + FRAME_DURATION);
        }
    }

    public void animateToNormal() {
        unscheduleSelf(mUpdater);
        if (mCurrentScale > 0) {
            mReverse = true;
            mRunning = true;
            mAnimationInitialValue = mCurrentScale;
            float durationFactor = mAnimationInitialValue;
            mDuration = (int) (ANIMATION_DURATION * durationFactor);
            mStartTime = SystemClock.uptimeMillis();
            scheduleSelf(mUpdater, mStartTime + FRAME_DURATION);
        }
    }

    private void updateAnimation(float factor) {
        float initial = mAnimationInitialValue;
        float destination = mReverse ? 0 : 1f;
        mCurrentScale = initial + (destination - initial) * factor;
        invalidateSelf();
    }

    @Override
    public void start() {
        //No-Op. We control our own animation
    }

    @Override
    public void stop() {
        unscheduleSelf(mUpdater);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }
}
