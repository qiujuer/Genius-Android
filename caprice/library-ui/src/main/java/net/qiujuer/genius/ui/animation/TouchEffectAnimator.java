/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/06/2015
 * Changed 03/08/2015
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
package net.qiujuer.genius.ui.animation;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import net.qiujuer.genius.ui.GeniusUi;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * This class adds touch effects to the given View. The effect animation is triggered by onTouchEvent
 * of the View and this class is injected into the onDraw function of the View to perform animation.
 * You should in your View onMeasure() call to this class.
 */
public class TouchEffectAnimator {
    // Touch Enum
    public static final int EASE = 1;
    public static final int RIPPLE = 2;
    public static final int MOVE = 3;
    public static final int PRESS = 4;
    // Base Values
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.8f);
    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final int IN_ANIM_DURATION = 250;
    private static final int OUT_ANIM_DURATION = 160;
    // MAX_RIPPLE_ALPHA * 0.70
    private static final int MAX_BACK_ALPHA = 180;
    private static final int MAX_RIPPLE_ALPHA = 255;

    private View mView;
    private float[] mRadii = new float[8];
    private float mAnimDurationFactor = 1;
    private int mFadeInAnimDuration = IN_ANIM_DURATION;
    private int mFadeOutAnimDuration = OUT_ANIM_DURATION;
    private int mTouchEffect = MOVE;
    private Animation mFadeInAnimation = null;
    private Animation mFadeOutAnimation = null;

    private float mStartRadius;
    private float mEndRadius;
    private float mRadius;

    private float mDownX, mDownY;
    private float mCenterX, mCenterY;
    private float mPaintX, mPaintY;

    private Paint mPaint = new Paint(ANTI_ALIAS_FLAG);
    private Path mRectPath = new Path();
    private int mEndBackAlpha = MAX_BACK_ALPHA;
    private int mEndRippleAlpha = MAX_RIPPLE_ALPHA;
    private int mBackAlpha = 0;
    private int mRippleAlpha = 0;

    private boolean isTouchReleased = false;
    private boolean isAnimatingFadeIn = false;
    private boolean isInterceptClick = false;

    // To call view performClick
    private Runnable mPerformClick;

    public TouchEffectAnimator(View view) {
        mView = view;
        mPaint = new Paint(ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        initTouch();
    }

    public float getAnimDurationFactor() {
        return mAnimDurationFactor;
    }

    public void setAnimDurationFactor(float factor) {
        mAnimDurationFactor = factor;
        setTouchEffect(mTouchEffect);
    }

    public int getTouchEffect() {
        return mTouchEffect;
    }

    public void setTouchEffect(int touchEffect) {
        mTouchEffect = touchEffect;
        if (mTouchEffect == EASE) {
            mFadeInAnimDuration = OUT_ANIM_DURATION;
            mFadeOutAnimDuration = OUT_ANIM_DURATION;
        } else if (mTouchEffect == PRESS) {
            mFadeInAnimDuration = (int) (IN_ANIM_DURATION * 0.6);
            mFadeOutAnimDuration = (int) (OUT_ANIM_DURATION * 1.3);
        } else {
            mFadeInAnimDuration = IN_ANIM_DURATION;
            mFadeOutAnimDuration = OUT_ANIM_DURATION;
        }
        this.mFadeInAnimDuration = (int) (this.mFadeInAnimDuration * mAnimDurationFactor);
        this.mFadeOutAnimDuration = (int) (this.mFadeOutAnimDuration * mAnimDurationFactor);
    }

    public void setEffectColor(int effectColor) {
        // Get Alpha
        int alpha = Color.alpha(effectColor);
        if (alpha != 255) {
            mEndBackAlpha = GeniusUi.modulateAlpha(alpha, MAX_BACK_ALPHA);
            mEndRippleAlpha = GeniusUi.modulateAlpha(alpha, MAX_RIPPLE_ALPHA);
        }
        // Set Color
        mPaint.setColor(effectColor);
    }

    public void setClipRadius(float radius) {
        this.mRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public void setClipRadii(float[] radii) {
        if (radii == null || radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii must have >= 8 values");
        }
        this.mRadii = radii;
    }

    public boolean interceptClick() {
        isInterceptClick = !isInterceptClick;
        return isInterceptClick;
    }

    public void onTouchEvent(final MotionEvent event) {
        // On disEnable return
        if (!mView.isEnabled())
            return;

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
            // Init
            initTouch();

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
                case RIPPLE:
                    float x = mDownX < mCenterX ? 2 * mCenterX : 0;
                    float y = mDownY < mCenterY ? 2 * mCenterY : 0;
                    mEndRadius = (float) Math.sqrt((x - mDownX) * (x - mDownX) + (y - mDownY) * (y - mDownY));
                    break;
                case MOVE:
                    mStartRadius = 0;
                    mEndRadius *= 0.78;
                    break;
                case PRESS:
                    mStartRadius = mEndRadius * 0.68f;
                    mEndRadius *= 0.98;
                    mPaintX = mCenterX;
                    mPaintY = mCenterY;
                    break;
            }

            // Cancel and Start new animation
            cancelEffect();
            fadeInEffect();
        }
    }

    public void onDraw(final Canvas canvas) {
        // Draw Background
        if (mTouchEffect != PRESS && mBackAlpha != 0) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTouch() {
        // Initializes the height width
        int x = mView.getWidth() / 2;
        int y = mView.getHeight() / 2;

        if (x == mCenterX && y == mCenterY)
            return;

        mCenterX = x;
        mCenterY = y;

        mRectPath.reset();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mRectPath.addRoundRect(new RectF(0, 0, mView.getWidth(), mView.getHeight()), mRadii, Path.Direction.CW);
        else
            mRectPath.addRoundRect(0, 0, mView.getWidth(), mView.getHeight(), mRadii, Path.Direction.CW);
    }

    private void fadeInEffect() {
        if (mFadeInAnimation == null) {
            mFadeInAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    switch (mTouchEffect) {
                        case EASE:
                            mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                            break;
                        case RIPPLE:
                            mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                            mRadius = mStartRadius + (mEndRadius - mStartRadius) * interpolatedTime;
                            break;
                        case MOVE:
                            mBackAlpha = (int) (interpolatedTime * mEndBackAlpha);
                            mRadius = mEndRadius * interpolatedTime;
                            mPaintX = mDownX + (mCenterX - mDownX) * interpolatedTime;
                            mPaintY = mDownY + (mCenterY - mDownY) * interpolatedTime;
                            break;
                        case PRESS:
                            mRadius = mStartRadius + (mEndRadius - mStartRadius) * interpolatedTime;
                            mRippleAlpha = (int) (interpolatedTime * mEndRippleAlpha);
                            break;
                    }
                    mView.invalidate();
                }
            };
            mFadeInAnimation.setInterpolator(DECELERATE_INTERPOLATOR);
            mFadeInAnimation.setDuration(mFadeInAnimDuration);
            mFadeInAnimation.setAnimationListener(new TouchEffectAnimatorListener() {
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
            });
        }
        mView.startAnimation(mFadeInAnimation);
    }

    private void fadeOutEffect() {
        if (mFadeOutAnimation == null) {
            mFadeOutAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    mBackAlpha = (int) (mEndBackAlpha - (mEndBackAlpha * interpolatedTime));
                    if (mTouchEffect == PRESS) {
                        mRippleAlpha = (int) (mEndRippleAlpha - (mEndRippleAlpha * interpolatedTime));
                        mRadius = mEndRadius + (mStartRadius - mEndRadius) * interpolatedTime;
                    } else {
                        mRadius = 0;
                    }
                    mView.invalidate();
                }
            };
            mFadeOutAnimation.setInterpolator(ACCELERATE_INTERPOLATOR);
            mFadeOutAnimation.setDuration(mFadeOutAnimDuration);
            mFadeOutAnimation.setAnimationListener(new TouchEffectAnimatorListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isInterceptClick) {
                        performClick();
                    }
                }
            });
        } else {
            mFadeOutAnimation.cancel();
        }
        mView.startAnimation(mFadeOutAnimation);
    }

    private void cancelEffect() {
        if (mFadeInAnimation != null) {
            mFadeInAnimation.cancel();
        }

        if (mFadeOutAnimation != null) {
            mFadeOutAnimation.cancel();
        }
    }

    private void performClick() {
        if (mPerformClick == null) {
            mPerformClick = new Runnable() {
                @Override
                public void run() {
                    mView.performClick();
                }
            };
        }
        if (!mView.post(mPerformClick)) {
            mView.performClick();
        }
    }
}

