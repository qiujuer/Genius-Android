/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/30/2014
 * Changed 12/30/2014
 * Version 1.0.0
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
package net.qiujuer.genius.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * Created by Qiujuer
 * on 2014/12/30.
 * <p/>
 * This class adds touch effects to the given View. The effect animation is triggered by onTouchEvent
 * of the View and this class is injected into the onDraw function of the View to perform animation.
 */
public class TouchEffectAnimator {

    private final int EASE_ANIM_DURATION = 200;
    private final int RIPPLE_ANIM_DURATION = 300;
    private final int MAX_RIPPLE_ALPHA = 255;

    private View mView;
    private int mClipRadius;
    private boolean hasRippleEffect = false;
    private int animDuration = EASE_ANIM_DURATION;

    private int requiredRadius;
    private float mDownX;
    private float mDownY;
    private float mRadius;
    private int mCircleAlpha = MAX_RIPPLE_ALPHA;
    private int mRectAlpha = 0;
    private Paint mCirclePaint = new Paint();
    private Paint mRectPaint = new Paint();
    private Path mCirclePath = new Path();
    private Path mRectPath = new Path();
    private boolean isTouchReleased = false;
    private boolean isAnimatingFadeIn = false;

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimatingFadeIn = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimatingFadeIn = false;
            if (isTouchReleased) fadeOutEffect();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    public TouchEffectAnimator(View mView) {
        this.mView = mView;
    }

    public void setHasRippleEffect(boolean hasRippleEffect) {
        this.hasRippleEffect = hasRippleEffect;
        if (hasRippleEffect) animDuration = RIPPLE_ANIM_DURATION;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public void setEffectColor(int effectColor) {
        mCirclePaint.setColor(effectColor);
        mCirclePaint.setAlpha(mCircleAlpha);
        mRectPaint.setColor(effectColor);
        mRectPaint.setAlpha(mRectAlpha);
    }

    public void setClipRadius(int mClipRadius) {
        this.mClipRadius = mClipRadius;
    }

    public void onTouchEvent(final MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            isTouchReleased = true;
            if (!isAnimatingFadeIn) {
                fadeOutEffect();
            }
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            isTouchReleased = true;
            if (!isAnimatingFadeIn) {
                fadeOutEffect();
            }
        } else if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            // gets the bigger value (width or height) to fit the circle
            requiredRadius = mView.getWidth() > mView.getHeight() ? mView.getWidth() : mView.getHeight();

            // increasing radius for circle to reach from corner to other corner
            requiredRadius *= 1.2;

            isTouchReleased = false;
            mDownX = event.getX();
            mDownY = event.getY();

            mCircleAlpha = MAX_RIPPLE_ALPHA;
            mRectAlpha = 0;

            ValueGeneratorAnim valueGeneratorAnim = new ValueGeneratorAnim(new InterpolatedTimeCallback() {
                @Override
                public void onTimeUpdate(float interpolatedTime) {
                    if (hasRippleEffect)
                        mRadius = requiredRadius * interpolatedTime;
                    mRectAlpha = (int) (interpolatedTime * MAX_RIPPLE_ALPHA);
                    mView.invalidate();
                }
            });
            valueGeneratorAnim.setInterpolator(new DecelerateInterpolator());
            valueGeneratorAnim.setDuration(animDuration);
            valueGeneratorAnim.setAnimationListener(animationListener);
            mView.startAnimation(valueGeneratorAnim);
        }
    }

    public void onDraw(final Canvas canvas) {

        if (hasRippleEffect) {
            mCirclePath.reset();
            mCirclePaint.setAlpha(mCircleAlpha);
            mCirclePath.addRoundRect(new RectF(0, 0, mView.getWidth(), mView.getHeight()),
                    mClipRadius, mClipRadius, Path.Direction.CW);

            canvas.clipPath(mCirclePath);
            canvas.drawCircle(mDownX, mDownY, mRadius, mCirclePaint);
        }

        mRectPath.reset();
        if (hasRippleEffect && mCircleAlpha != 255) mRectAlpha = mCircleAlpha / 2;
        mRectPaint.setAlpha(mRectAlpha);
        canvas.drawRoundRect(new RectF(0, 0, mView.getWidth(), mView.getHeight()), mClipRadius,
                mClipRadius, mRectPaint);
    }

    private void fadeOutEffect() {
        ValueGeneratorAnim valueGeneratorAnim = new ValueGeneratorAnim(new InterpolatedTimeCallback() {
            @Override
            public void onTimeUpdate(float interpolatedTime) {
                mCircleAlpha = (int) (MAX_RIPPLE_ALPHA - (MAX_RIPPLE_ALPHA * interpolatedTime));
                mRectAlpha = mCircleAlpha;
                mView.invalidate();
            }
        });
        valueGeneratorAnim.setDuration(animDuration);
        mView.startAnimation(valueGeneratorAnim);
    }

    class ValueGeneratorAnim extends Animation {

        private InterpolatedTimeCallback interpolatedTimeCallback;

        ValueGeneratorAnim(InterpolatedTimeCallback interpolatedTimeCallback) {
            this.interpolatedTimeCallback = interpolatedTimeCallback;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            this.interpolatedTimeCallback.onTimeUpdate(interpolatedTime);
        }
    }

    interface InterpolatedTimeCallback {
        public void onTimeUpdate(float interpolatedTime);
    }
}

