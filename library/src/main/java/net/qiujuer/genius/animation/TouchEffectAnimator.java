/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/06/2015
 * Changed 01/06/2015
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
package net.qiujuer.genius.animation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by Qiujuer
 * on 2015/01/06.
 * <p/>
 * This class adds touch effects to the given View. The effect animation is triggered by onTouchEvent
 * of the View and this class is injected into the onDraw function of the View to perform animation.
 * You should in your View onMeasure() call to this class.
 */
public class TouchEffectAnimator {
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.8f);
    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final int EASE_ANIM_DURATION = 200;
    private static final int RIPPLE_ANIM_DURATION = 300;
    private static final int MAX_RIPPLE_ALPHA = (int) (255 * 0.8);

    private View mView;
    private int mClipRadius;
    private int mAnimDuration = RIPPLE_ANIM_DURATION;
    private TouchEffect mTouchEffect = TouchEffect.Move;
    private Animation mAnimation = null;

    private float mMaxRadius;
    private float mRadius;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private Paint mPaint = new Paint();
    private RectF mRectRectR = new RectF();
    private Path mRectPath = new Path();
    private int mRectAlpha = 0;

    private boolean isTouchReleased = false;
    private boolean isAnimatingFadeIn = false;

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimatingFadeIn = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimatingFadeIn = false;
            // Is un touch auto fadeOutEffect()
            if (isTouchReleased) fadeOutEffect();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    public TouchEffectAnimator(View mView) {
        this.mView = mView;
        onMeasure();
    }

    public void setAnimDuration(int animDuration) {
        this.mAnimDuration = animDuration;
    }

    public TouchEffect getTouchEffect() {
        return mTouchEffect;
    }

    public void setTouchEffect(TouchEffect touchEffect) {
        mTouchEffect = touchEffect;
        if (mTouchEffect == TouchEffect.Ease)
            mAnimDuration = EASE_ANIM_DURATION;
    }

    public void setEffectColor(int effectColor) {
        mPaint.setColor(effectColor);
    }

    public void setClipRadius(int mClipRadius) {
        this.mClipRadius = mClipRadius;
    }

    public void onMeasure() {
        mCenterX = mView.getWidth() / 2;
        mCenterY = mView.getHeight() / 2;

        mRectRectR.set(0, 0, mView.getWidth(), mView.getHeight());

        mRectPath.reset();
        mRectPath.addRoundRect(mRectRectR, mClipRadius, mClipRadius, Path.Direction.CW);
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
            // Gets the bigger value (width or height) to fit the circle
            mMaxRadius = mCenterX > mCenterY ? mCenterX : mCenterY;
            // This circle radius is 75% or fill all
            if (mTouchEffect == TouchEffect.Move)
                mMaxRadius *= 0.75;
            else
                mMaxRadius *= 2.5;

            // Set default operation to fadeOutEffect()
            isTouchReleased = false;
            isAnimatingFadeIn = true;

            // Set this start point
            mPaintX = mDownX = event.getX();
            mPaintY = mDownY = event.getY();

            // This color alpha
            mRectAlpha = 0;

            // Cancel and Start new animation
            cancelAnimation();
            startAnimation();
        }
    }

    public void onDraw(final Canvas canvas) {
        // Draw Area
        mPaint.setAlpha(mRectAlpha);
        canvas.drawPath(mRectPath, mPaint);

        // Draw Ripple
        if (isAnimatingFadeIn && (mTouchEffect == TouchEffect.Move
                || mTouchEffect == TouchEffect.Ripple)) {
            // Canvas Clip
            canvas.clipPath(mRectPath);
            mPaint.setAlpha(MAX_RIPPLE_ALPHA);
            canvas.drawCircle(mPaintX, mPaintY, mRadius, mPaint);
        }
    }

    private void startAnimation() {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (mTouchEffect == TouchEffect.Move) {
                    mRadius = mMaxRadius * interpolatedTime;
                    mPaintX = mDownX + (mCenterX - mDownX) * interpolatedTime;
                    mPaintY = mDownY + (mCenterY - mDownY) * interpolatedTime;
                } else if (mTouchEffect == TouchEffect.Ripple) {
                    mRadius = mMaxRadius * interpolatedTime;
                }

                mRectAlpha = (int) (interpolatedTime * MAX_RIPPLE_ALPHA);
                mView.invalidate();
            }
        };
        animation.setInterpolator(DECELERATE_INTERPOLATOR);
        animation.setDuration(mAnimDuration);
        animation.setAnimationListener(mAnimationListener);
        mView.startAnimation(animation);
    }

    private void cancelAnimation() {
        if (mAnimation != null) {
            mAnimation.cancel();
            mAnimation.setAnimationListener(null);
        }
    }

    private void fadeOutEffect() {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mRectAlpha = (int) (MAX_RIPPLE_ALPHA - (MAX_RIPPLE_ALPHA * interpolatedTime));
                mView.invalidate();
            }
        };
        animation.setInterpolator(ACCELERATE_INTERPOLATOR);
        animation.setDuration(EASE_ANIM_DURATION);
        mView.startAnimation(animation);
    }
}

