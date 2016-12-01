/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/24/2015
 * Changed 05/10/2016
 * Version 2.0.0
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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.drawable.effect.Effect;

import java.lang.ref.WeakReference;

/**
 * This is touch effect drawable
 * This drawable is can use background or other draw call
 */
public class TouchEffectDrawable extends StatePaintDrawable implements Animatable {
    public static final int INTERCEPT_EVENT_CLICK = 0x0001;
    public static final int INTERCEPT_EVENT_LONG_CLICK = 0x0010;
    // Time
    public static final int ANIM_ENTER_DURATION = 280;
    public static final int ANIM_EXIT_DURATION = 160;
    public static final int ANIM_DELAY_START_TIME = 90;

    // Speed
    private final static int SPEED_NORMAL = 1;
    private final static int SPEED_FAST = 2;
    private final static int SPEED_SLOW = 3;

    // Base
    private TouchEffectState mState;
    private boolean mMutated;

    // Touch
    protected boolean isTouchReleased = false;
    private WeakReference<PerformClicker> mPerformClicker = null;
    private WeakReference<PerformLongClicker> mPerformLongClicker = null;

    // Animation
    private boolean isRealRunning = false;
    private float mAnimSpeed = SPEED_NORMAL * 1.0f;
    private int mAnimSpeedType = SPEED_NORMAL;

    // Intercept
    private int mInterceptEvent = 0x0001;

    public TouchEffectDrawable() {
        this(new TouchEffectState(null), null, null);
    }

    public TouchEffectDrawable(Effect s) {
        this(new TouchEffectState(null), null, null);
        mState.mEffect = s;
    }

    public TouchEffectDrawable(Effect s, ColorStateList color) {
        this(new TouchEffectState(null), null, color);
        mState.mEffect = s;
    }

    /**
     * Returns the Effect of this EffectDrawable.
     */
    public Effect getEffect() {
        return mState.mEffect;
    }

    /**
     * Sets the Effect of this EffectDrawable.
     */
    public void setEffect(Effect s) {
        mState.mEffect = s;
        updateEffect();
    }

    /**
     * Set the click intercept to widget,
     * this value will be used {@link #mPerformClicker} and {@link #mPerformLongClicker}
     * eg:
     * 0x0000: not intercept
     * 0x0001: intercept click
     * 0x0010: intercept longClick
     * 0x0011: intercept click and longClick
     *
     * @param interceptEvent intercept type (0x0000,0x0001,0x0010,0x0011)
     */
    public void setInterceptEvent(int interceptEvent) {
        mInterceptEvent = interceptEvent;
    }

    /**
     * Sets a ShaderFactory to which requests for a
     * {@link Shader} object will be made.
     *
     * @param fact an instance of your ShaderFactory implementation
     */
    public void setShaderFactory(ShaderFactory fact) {
        mState.mShaderFactory = fact;
    }

    /**
     * Returns the ShaderFactory used by this TouchEffectDrawable for requesting a
     * {@link Shader}.
     */
    public ShaderFactory getShaderFactory() {
        return mState.mShaderFactory;
    }

    /**
     * Sets a ClipFactory to which requests for
     * {@link Canvas} clip.. method object will be made.
     *
     * @param fact an instance of your ClipFactory implementation
     */
    public void setClipFactory(ClipFactory fact) {
        mState.mClipFactory = fact;
    }

    /**
     * Returns the ClipFactory used by this TouchEffectDrawable Canvas.clip.. method
     */
    public ClipFactory getClipFactory() {
        return mState.mClipFactory;
    }

    /**
     * Sets padding for the shape.
     *
     * @param left   padding for the left side (in pixels)
     * @param top    padding for the top (in pixels)
     * @param right  padding for the right side (in pixels)
     * @param bottom padding for the bottom (in pixels)
     */
    public void setPadding(int left, int top, int right, int bottom) {
        if ((left | top | right | bottom) == 0) {
            mState.mPadding = null;
        } else {
            if (mState.mPadding == null) {
                mState.mPadding = new Rect();
            }
            mState.mPadding.set(left, top, right, bottom);
        }
        invalidateSelf();
    }

    /**
     * Sets padding for this shape, defined by a Rect object. Define the padding
     * in the Rect object as: left, top, right, bottom.
     */
    public void setPadding(Rect padding) {
        if (padding == null) {
            mState.mPadding = null;
        } else {
            if (mState.mPadding == null) {
                mState.mPadding = new Rect();
            }
            mState.mPadding.set(padding);
        }
        invalidateSelf();
    }

    /**
     * Sets the intrinsic (default) width for this shape.
     *
     * @param width the intrinsic width (in pixels)
     */
    public void setIntrinsicWidth(int width) {
        mState.mIntrinsicWidth = width;
        invalidateSelf();
    }

    /**
     * Sets the intrinsic (default) height for this shape.
     *
     * @param height the intrinsic height (in pixels)
     */
    public void setIntrinsicHeight(int height) {
        mState.mIntrinsicHeight = height;
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return mState.mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mState.mIntrinsicHeight;
    }

    @Override
    public boolean getPadding(Rect padding) {
        if (mState.mPadding != null) {
            padding.set(mState.mPadding);
            return true;
        } else {
            return super.getPadding(padding);
        }
    }

    /**
     * Called from the drawable's draw() method after the canvas has been set to
     * draw the shape at (0,0). Subclasses can override for special effects such
     * as multiple layers, stroking, etc.
     */
    protected void onDraw(Effect shape, Canvas canvas, Paint paint) {
        shape.draw(canvas, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        // In this, we check the animation state before call draw
        if (isRunning())
            super.draw(canvas);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        final Rect r = getBounds();
        final TouchEffectState state = mState;

        if (state.mEffect != null) {
            // need the save both for the translate, and for the (unknown)
            // Effect
            final int count = canvas.save();

            // Translate
            canvas.translate(r.left, r.top);

            // Clip the canvas
            if (state.mClipFactory != null) {
                state.mClipFactory.clip(canvas);
            } else {
                canvas.clipRect(0, 0, r.width(), r.height());
            }

            // On draw
            onDraw(state.mEffect, canvas, paint);
            // Restore
            canvas.restoreToCount(count);
        } else {
            canvas.drawRect(r, paint);
        }
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations()
                | mState.mChangingConfigurations;
    }

    @Override
    public int getOpacity() {
        if (mState.mEffect == null) {
            return super.getOpacity();
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateEffect();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        if (mState.mEffect != null) {
            mState.mEffect.getOutline(outline);
            outline.setAlpha(getAlpha() / 255.0f);
        }
    }

    @Override
    public ConstantState getConstantState() {
        mState.mChangingConfigurations = getChangingConfigurations();
        return mState;
    }

    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            if (mState.mPadding != null) {
                mState.mPadding = new Rect(mState.mPadding);
            } else {
                mState.mPadding = new Rect();
            }
            try {
                if (mState.mEffect != null)
                    mState.mEffect = mState.mEffect.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
            mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        mMutated = false;
    }

    public boolean onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL: {
                isTouchReleased = true;
                onTouchCancel(event.getX(), event.getY());
            }
            break;
            case MotionEvent.ACTION_UP: {
                isTouchReleased = true;
                onTouchReleased(event.getX(), event.getY());
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                isTouchReleased = false;
                onTouchDown(event.getX(), event.getY());
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                onTouchMove(event.getX(), event.getY());
            }
            break;
            default:
                return false;
        }
        return true;
    }

    protected void onTouchCancel(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            if (x > r.right) x = r.width();
            else x = x - r.left;

            if (y > r.bottom) y = r.height();
            else y = y - r.top;

            mState.mEffect.touchCancel(x, y);

            // Cancel
            cancelAnim();
        }
    }

    protected void onTouchDown(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            if (x > r.right) x = r.width();
            else x = x - r.left;

            if (y > r.bottom) y = r.height();
            else y = y - r.top;

            mState.mEffect.touchDown(x, y);

            // Start new animation so we should call stop
            stop();
            startEnterAnim();
        }
    }

    protected void onTouchReleased(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            if (x > r.right) x = r.width();
            else x = x - r.left;

            if (y > r.bottom) y = r.height();
            else y = y - r.top;
            mState.mEffect.touchReleased(x, y);

            // change the animation speed
            changeSpeed(SPEED_NORMAL);

            // Start Exit animation
            startExitAnim();
        }
    }

    protected void onTouchMove(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            if (x > r.right) x = r.width();
            else x = x - r.left;

            if (y > r.bottom) y = r.height();
            else y = y - r.top;
            mState.mEffect.touchMove(x, y);

            // change the animation speed
            changeSpeed(SPEED_SLOW);
        }
    }


    private void updateEffect() {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            final int w = r.width();
            final int h = r.height();

            mState.mEffect.resize(w, h);
            if (mState.mShaderFactory != null) {
                mPaint.setShader(mState.mShaderFactory.resize(w, h));
            }

            if (mState.mClipFactory != null) {
                mState.mClipFactory.resize(w, h);
            }
        }
        invalidateSelf();
    }

    /**
     * The one constructor to rule them all. This is called by all public
     * constructors to set the state and initialize local properties.
     */
    private TouchEffectDrawable(TouchEffectState state, Resources res, ColorStateList color) {
        super(color);
        mState = state;
    }

    static TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    /**
     * This drawable call view perform click by interface
     */
    public interface PerformClicker {
        void postPerformClick();
    }

    /**
     * This drawable call view perform longClick by interface
     */
    public interface PerformLongClicker {
        void postPerformLongClick();
    }

    public boolean performClick(PerformClicker clicker) {
        if ((mInterceptEvent & INTERCEPT_EVENT_CLICK) != INTERCEPT_EVENT_CLICK)
            return true;

        PerformClicker saveValue = getPerformClicker();
        if (saveValue == null) {
            savePerformClicker(clicker);
            return false;
        } else {
            if (mEnterAnimate.isRunning())
                return false;
            else {
                clearPerformClicker();
                return true;
            }
        }
    }

    public boolean performLongClick(PerformLongClicker clicker) {
        if ((mInterceptEvent & INTERCEPT_EVENT_LONG_CLICK) != INTERCEPT_EVENT_LONG_CLICK)
            return true;

        PerformLongClicker saveValue = getPerformLongClicker();
        if (saveValue == null) {
            savePerformLongClicker(clicker);
            return false;
        } else {
            if (mEnterAnimate.isRunning())
                return false;
            else {
                clearPerformLongClicker();
                return true;
            }
        }
    }

    protected void postPerformClick() {
        PerformClicker clicker = getPerformClicker();
        if (clicker != null) {
            clicker.postPerformClick();
        }
    }

    protected void postPerformLongClick() {
        if (mAnimSpeedType != SPEED_SLOW)
            return;
        PerformLongClicker longClicker = getPerformLongClicker();
        if (longClicker != null) {
            longClicker.postPerformLongClick();
        }
    }

    private void savePerformClicker(PerformClicker clicker) {
        synchronized (this) {
            mPerformClicker = new WeakReference<PerformClicker>(clicker);
        }
    }

    private void savePerformLongClicker(PerformLongClicker clicker) {
        synchronized (this) {
            mPerformLongClicker = new WeakReference<PerformLongClicker>(clicker);
        }
    }

    private PerformClicker getPerformClicker() {
        synchronized (this) {
            if (mPerformClicker != null) {
                return mPerformClicker.get();
            }
            return null;
        }
    }

    private PerformLongClicker getPerformLongClicker() {
        synchronized (this) {
            if (mPerformLongClicker != null) {
                return mPerformLongClicker.get();
            }
            return null;
        }
    }

    private void clearPerformClicker() {
        synchronized (this) {
            if (mPerformClicker != null) {
                mPerformClicker.clear();
                mPerformClicker = null;
            }
        }
    }

    private void clearPerformLongClicker() {
        synchronized (this) {
            if (mPerformLongClicker != null) {
                mPerformLongClicker.clear();
                mPerformLongClicker = null;
            }
        }
    }

    @Override
    public void start() {
        startEnterAnim();
    }

    @Override
    public void stop() {
        mEnterAnimate.stop();
        mExitAnimate.stop();
        changeSpeed(SPEED_NORMAL);
    }

    /**
     * Return this draw animation is running
     *
     * @return isRunning
     */
    @Override
    public boolean isRunning() {
        return isRealRunning;
    }

    public int getEnterDuration() {
        return mEnterAnimate.mDuration;
    }

    public int getExitDuration() {
        return mExitAnimate.mDuration;
    }

    /**
     * Set the touch animation duration.
     * This setting about enter animation.
     * <p/>
     * Default:
     * EnterDuration: 280ms
     * FactorRate: 1.0
     * <p/>
     * This set will calculation: factor * duration
     * This factor need > 0
     *
     * @param factor Touch duration rate
     */
    public void setEnterDuration(float factor) {
        if (factor > 0) {
            mEnterAnimate.mDuration = (int) (factor * ANIM_ENTER_DURATION);
        }
    }

    /**
     * Set the touch animation duration.
     * This setting about exit animation.
     * <p/>
     * Default:
     * ExitDuration: 160ms
     * FactorRate: 1.0
     * <p/>
     * This set will calculation: factor * duration
     * This factor need > 0
     *
     * @param factor Touch duration rate
     */
    public void setExitDuration(float factor) {
        if (factor > 0) {
            mExitAnimate.mDuration = (int) (factor * ANIM_EXIT_DURATION);
        }
    }

    public Interpolator getEnterInterpolator() {
        return mEnterAnimate.mInterpolator;
    }

    public Interpolator getExitInterpolator() {
        return mExitAnimate.mInterpolator;
    }

    public void setEnterInterpolator(Interpolator interpolator) {
        if (interpolator == null)
            return;
        mEnterAnimate.mInterpolator = interpolator;
    }

    public void setExitInterpolator(Interpolator interpolator) {
        if (interpolator == null)
            return;
        mExitAnimate.mInterpolator = interpolator;
    }

    private void startEnterAnim() {
        // onStart Anim we need clear old PerformClicker
        clearPerformClicker();
        clearPerformLongClicker();
        // Start animation by delay
        // the delay time use to can cancel the animation 64ms
        mEnterAnimate.start(ANIM_DELAY_START_TIME);
    }

    private void startExitAnim() {
        // check is running
        if (mEnterAnimate.isRunning())
            return;

        // Post Click before exit animate running
        postPerformClick();

        // Start animation by not delay
        mExitAnimate.start(0);
    }

    private void cancelAnim() {
        if (isRealRunning) {
            changeSpeed(SPEED_FAST);
            startExitAnim();
        } else {
            stop();
        }
    }

    private void changeSpeed(int speedType) {
        if (mAnimSpeedType == speedType)
            return;
        // Change speed type and real speed
        mAnimSpeedType = speedType;
        float speed;
        switch (speedType) {
            case SPEED_FAST:
                speed = 2f;
                break;
            case SPEED_SLOW:
                speed = 0.28f;
                break;
            default:
                speed = 1f;
                break;
        }

        if (mAnimSpeed != speed) {
            mAnimSpeed = speed;
            mEnterAnimate.reckonIncremental();
            mExitAnimate.reckonIncremental();
        }
    }


    final static class TouchEffectState extends ConstantState {
        int[] mThemeAttrs;
        int mChangingConfigurations;
        Effect mEffect;
        Rect mPadding;
        int mIntrinsicWidth;
        int mIntrinsicHeight;
        ShaderFactory mShaderFactory;
        ClipFactory mClipFactory;


        TouchEffectState(TouchEffectState orig) {
            if (orig != null) {
                mThemeAttrs = orig.mThemeAttrs;
                mEffect = orig.mEffect;
                mPadding = orig.mPadding;
                mIntrinsicWidth = orig.mIntrinsicWidth;
                mIntrinsicHeight = orig.mIntrinsicHeight;
                mShaderFactory = orig.mShaderFactory;
                mClipFactory = orig.mClipFactory;
            }
        }

        @Override
        public boolean canApplyTheme() {
            return mThemeAttrs != null;
        }

        @Override
        public Drawable newDrawable() {
            return new TouchEffectDrawable(this, null, null);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new TouchEffectDrawable(this, res, null);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }


    public static abstract class ClipFactory {
        /**
         * The dimensions of the Drawable are passed because they may be needed to
         * adjust how the Canvas.clip.. is configured for drawing. This is called by
         * EffectDrawable.updateEffect().
         *
         * @param width  the width of the Drawable being drawn
         * @param height the height of the Drawable being drawn
         */
        public abstract void resize(int width, int height);

        /**
         * Returns the Canvas clip to be drawn when a Drawable is drawn.
         *
         * @param canvas The drawable Canvas
         * @return The Canvas clip.. status
         */
        public abstract boolean clip(Canvas canvas);
    }

    /**
     * Base class defines a factory object that is called each time the drawable
     * is resized (has a new width or height). Its resize() method returns a
     * corresponding shader, or null. Implement this class if you'd like your
     * EffectDrawable to use a special {@link Shader}, such as a
     * {@link android.graphics.LinearGradient}.
     */
    public static abstract class ShaderFactory {
        /**
         * Returns the Shader to be drawn when a Drawable is drawn. The
         * dimensions of the Drawable are passed because they may be needed to
         * adjust how the Shader is configured for drawing. This is called by
         * TouchEffectDrawable.updateEffect().
         *
         * @param width  the width of the Drawable being drawn
         * @param height the height of the Drawable being drawn
         * @return the Shader to be drawn
         */
        public abstract Shader resize(int width, int height);
    }

    /**
     * A animation post runnable {@link Runnable}
     * The class have animation status
     */
    abstract class AnimRunnable implements Runnable {
        private boolean mDone = true;
        // The draw progress
        private float mProgress = 0;
        // Incremental value
        private float mInc = 0;

        // This is user init animation running time
        int mDuration;
        // This is animation running Interpolator type
        Interpolator mInterpolator;

        AnimRunnable() {
            init();
        }

        abstract void init();

        abstract void onAnimateUpdate(float interpolation);

        abstract void onAnimateEnd();

        public boolean isRunning() {
            return !mDone;
        }

        public void reckonIncremental() {
            mInc = (FRAME_DURATION / (float) mDuration) * mAnimSpeed;
        }

        public void start(int delay) {
            // Change the enter status
            mDone = false;
            // Set start time after delay time
            long startTime = SystemClock.uptimeMillis() + delay;

            // Progress added unit
            mInc = (FRAME_DURATION / (float) mDuration) * mAnimSpeed;
            mProgress = 0;
            // notify run
            scheduleSelf(this, startTime);
        }

        public void stop() {
            unscheduleSelf(this);
            mDone = true;
        }

        @Override
        public void run() {
            // check the anim is finish
            if (mDone)
                return;

            // In this we can set the state to running
            isRealRunning = true;

            // check time
            mProgress += mInc;

            if (mProgress < 1) {
                final float interpolation = mInterpolator.getInterpolation(mProgress);
                // Notify
                onAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                final long currentTime = SystemClock.uptimeMillis();
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {
                mDone = true;
                unscheduleSelf(this);

                // Notify
                onAnimateUpdate(1f);
                invalidateSelf();

                // Call end
                onAnimateEnd();
            }
        }

    }

    private final AnimRunnable mEnterAnimate = new AnimRunnable() {
        @Override
        void init() {
            // init config
            mDuration = ANIM_ENTER_DURATION;
            mInterpolator = new DecelerateInterpolator(2.6f);
        }

        @Override
        void onAnimateUpdate(float interpolation) {
            // call to enter
            onEnterAnimateUpdate(interpolation);
        }

        @Override
        void onAnimateEnd() {
            onEnterAnimateEnd();
        }
    };

    private final AnimRunnable mExitAnimate = new AnimRunnable() {
        @Override
        void init() {
            mDuration = ANIM_EXIT_DURATION;
            mInterpolator = new AccelerateInterpolator();
        }

        @Override
        void onAnimateUpdate(float interpolation) {
            // call to enter
            onExitAnimateUpdate(interpolation);
        }

        @Override
        void onAnimateEnd() {
            onExitAnimateEnd();
        }
    };

    protected void onEnterAnimateUpdate(float factor) {
        if (mState.mEffect != null)
            mState.mEffect.animationEnter(factor);
    }

    protected void onExitAnimateUpdate(float factor) {
        if (mState.mEffect != null)
            mState.mEffect.animationExit(factor);
    }

    protected void onEnterAnimateEnd() {
        if (isTouchReleased) {
            // Is un touch auto startExitAnim
            startExitAnim();
        } else {
            // try perform long click
            postPerformLongClick();
        }
    }

    protected void onExitAnimateEnd() {
        // doing something
        isRealRunning = false;
    }
}
