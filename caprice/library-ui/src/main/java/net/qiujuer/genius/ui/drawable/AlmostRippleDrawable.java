/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/04/2015
 * Changed 08/05/2015
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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.GeniusUi;

/**
 * This is a touch foreground ripple drawable extends to StatePaintDrawable
 */
public class AlmostRippleDrawable extends StatePaintDrawable implements Animatable {
    private static final int FRAME_DURATION = 16;
    private static final int ANIMATION_DURATION = 250;
    private static final float INACTIVE_SCALE = 0f;
    private static final float ACTIVE_SCALE = 1f;
    private float mCurrentScale = INACTIVE_SCALE;
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

    public AlmostRippleDrawable(ColorStateList tintStateList) {
        super(tintStateList);
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        final float scale = mCurrentScale;
        if (scale > INACTIVE_SCALE) {
            final Rect bounds = getBounds();
            float radius = (Math.min(bounds.width(), bounds.height()) / 2.0f);
            float radiusAnimated = radius * scale;

            if (radius > 0) {
                // Background
                int preAlpha = setPaintAlpha(paint);
                if (paint.getAlpha() > 0) {
                    canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint);
                }

                // Ripple
                if (radiusAnimated > 0) {
                    if (preAlpha < 255) {
                        preAlpha = getRippleAlpha(preAlpha, paint.getAlpha());
                    }
                    if (preAlpha > 0) {
                        paint.setAlpha(preAlpha);
                        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radiusAnimated, paint);
                    }
                }
            }
        }
    }

    private int setPaintAlpha(Paint paint) {
        // Set the background alpha 128
        final int prevAlpha = paint.getAlpha();
        paint.setAlpha(GeniusUi.modulateAlpha(prevAlpha, 128));
        return prevAlpha;
    }

    private int getRippleAlpha(int preAlpha, int nowAlpha) {
        if (nowAlpha > preAlpha)
            return 0;
        int dAlpha = preAlpha - nowAlpha;
        return (255 * dAlpha) / (255 - nowAlpha);
    }

    @Override
    public boolean setState(int[] stateSet) {
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
            mCurrentScale = ACTIVE_SCALE;
            invalidateSelf();
        } else {
            // Other none show
            mCurrentScale = INACTIVE_SCALE;
            invalidateSelf();
        }

        return status;
    }

    public void animateToPressed() {
        unscheduleSelf(mUpdater);
        if (mCurrentScale < ACTIVE_SCALE) {
            mReverse = false;
            mRunning = true;
            mAnimationInitialValue = mCurrentScale;
            float durationFactor = 1f - ((mAnimationInitialValue - INACTIVE_SCALE) / (ACTIVE_SCALE - INACTIVE_SCALE));
            mDuration = (int) (ANIMATION_DURATION * durationFactor);
            mStartTime = SystemClock.uptimeMillis();
            scheduleSelf(mUpdater, mStartTime + FRAME_DURATION);
        }
    }

    public void animateToNormal() {
        unscheduleSelf(mUpdater);
        if (mCurrentScale > INACTIVE_SCALE) {
            mReverse = true;
            mRunning = true;
            mAnimationInitialValue = mCurrentScale;
            float durationFactor = 1f - ((mAnimationInitialValue - ACTIVE_SCALE) / (INACTIVE_SCALE - ACTIVE_SCALE));
            mDuration = (int) (ANIMATION_DURATION * durationFactor);
            mStartTime = SystemClock.uptimeMillis();
            scheduleSelf(mUpdater, mStartTime + FRAME_DURATION);
        }
    }

    private void updateAnimation(float factor) {
        float initial = mAnimationInitialValue;
        float destination = mReverse ? INACTIVE_SCALE : ACTIVE_SCALE;
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
