/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/06/2015
 * Changed 01/06/2015
 * Version 2.0.0
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

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

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
    private static final int EASE_ANIM_DURATION = 180;
    private static final int RIPPLE_ANIM_DURATION = 250;
    // 255*0.70
    private static final int MAX_BACK_ALPHA = 180;
    private static final int MAX_RIPPLE_ALPHA = 255;

    private View mView;
    private int mClipRadius;
    private int mAnimDuration = RIPPLE_ANIM_DURATION;
    private TouchEffect mTouchEffect = TouchEffect.Move;
    private Animation mAnimation = null;

    private float mStartRadius;
    private float mEndRadius;
    private float mRadius;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private Paint mPaint = new Paint(ANTI_ALIAS_FLAG);
    private RectF mRectRectR = new RectF();
    private Path mRectPath = new Path();
    private int mEndBackAlpha = MAX_BACK_ALPHA;
    private int mEndRippleAlpha = MAX_RIPPLE_ALPHA;
    private int mBackAlpha = 0;
    private int mRippleAlpha = 0;

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
        mPaint = new Paint(ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
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

        onMeasure();
    }

    public void setEffectColor(int effectColor) {
        mPaint.setColor(effectColor);

        int alpha = (effectColor >> 24) & 0xff;
        if (alpha != 255) {
            mEndBackAlpha = (int) (MAX_BACK_ALPHA * (alpha / 255.0f));
            mEndRippleAlpha = (int) (MAX_RIPPLE_ALPHA * (alpha / 255.0f));
        }
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
            // Set default operation to fadeOutEffect()
            isTouchReleased = false;
            isAnimatingFadeIn = true;

            // Set this start point
            mPaintX = mDownX = event.getX();
            mPaintY = mDownY = event.getY();

            // This color alpha
            mBackAlpha = 0;
            mRippleAlpha = 255;

            // Gets the bigger value (width or height) to fit the circle
            mEndRadius = mCenterX > mCenterY ? mCenterX : mCenterY;
            mStartRadius = 0;
            mRadius = 0;

            // This circle radius is 78% 90% or fill all
            switch (mTouchEffect) {
                case Ripple:
                    float x = mDownX < mCenterX ? 2 * mCenterX : 0;
                    float y = mDownY < mCenterY ? 2 * mCenterY : 0;
                    mEndRadius = (float) Math.sqrt((x - mDownX) * (x - mDownX) + (y - mDownY) * (y - mDownY));
                    break;
                case Move:
                    mStartRadius = 0;
                    mEndRadius *= 0.78;
                    break;
                case Press:
                    mStartRadius = mEndRadius * 0.6f;
                    mEndRadius *= 0.9;
                    mPaintX = mCenterX;
                    mPaintY = mCenterY;
                    break;
            }

            // Cancel and Start new animation
            cancelAnimation();
            startAnimation();
        }
    }

    public void onDraw(final Canvas canvas) {
        // Draw Background
        if (mTouchEffect != TouchEffect.Press && mBackAlpha != 0) {
            mPaint.setAlpha(mBackAlpha);
            canvas.drawPath(mRectPath, mPaint);
        }

        // Draw Ripple
        if (mRadius != 0) {
            // Canvas Clip
            canvas.save();
            canvas.clipPath(mRectPath);
            mPaint.setAlpha(mRippleAlpha);
            canvas.drawCircle(mPaintX, mPaintY, mRadius, mPaint);
            canvas.restore();
        }
    }

    private void startAnimation() {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                switch (mTouchEffect) {
                    case Ease:
                        mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                        break;
                    case Ripple:
                        mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                        mRadius = mStartRadius + (mEndRadius - mStartRadius) * interpolatedTime;
                        break;
                    case Move:
                        mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                        mRadius = mEndRadius * interpolatedTime;
                        mPaintX = mDownX + (mCenterX - mDownX) * interpolatedTime;
                        mPaintY = mDownY + (mCenterY - mDownY) * interpolatedTime;
                        break;
                    case Press:
                        mRadius = mStartRadius + (mEndRadius - mStartRadius) * interpolatedTime;
                        mRippleAlpha = (int) (interpolatedTime * mEndRippleAlpha);
                        break;
                }
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
                mBackAlpha = (int) (mEndBackAlpha - (mEndBackAlpha * interpolatedTime));
                if (mTouchEffect == TouchEffect.Press) {
                    mRippleAlpha = (int) (mEndRippleAlpha - (mEndRippleAlpha * interpolatedTime));
                    mRadius = mEndRadius + (mStartRadius - mEndRadius) * interpolatedTime;
                } else {
                    mRadius = 0;
                }
                mView.invalidate();
            }
        };
        animation.setInterpolator(ACCELERATE_INTERPOLATOR);
        animation.setDuration(EASE_ANIM_DURATION);
        mView.startAnimation(animation);
    }
}

