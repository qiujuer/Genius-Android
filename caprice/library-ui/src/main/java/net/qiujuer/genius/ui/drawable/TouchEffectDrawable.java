/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/24/2015
 * Changed 07/27/2015
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
    // Time
    public static final int ANIM_ENTER_DURATION = 280;
    public static final int ANIM_Exit_DURATION = 160;

    // Base
    private TouchEffectState mState;
    private boolean mMutated;

    // Touch
    protected boolean isTouchReleased = false;
    protected boolean isPerformClick = false;
    private WeakReference<PerformClicker> mPerformClicker = null;


    // Animation
    private boolean isRunning = false;
    private boolean isEnterAnimating = false;
    private long mStartTime;
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(2.6f);
    private Interpolator mExitInterpolator = new AccelerateInterpolator();
    private int mEnterDuration = ANIM_ENTER_DURATION;
    private int mExitDuration = ANIM_Exit_DURATION;


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
     * Sets a ShaderFactory to which requests for a
     * {@link android.graphics.Shader} object will be made.
     *
     * @param fact an instance of your ShaderFactory implementation
     */
    public void setShaderFactory(ShaderFactory fact) {
        mState.mShaderFactory = fact;
    }

    /**
     * Returns the ShaderFactory used by this TouchEffectDrawable for requesting a
     * {@link android.graphics.Shader}.
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
            case MotionEvent.ACTION_CANCEL:
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
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
            default:
                return false;
        }
        return true;
    }

    protected void onTouchDown(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            if (x > r.right) x = r.width();
            else x = x - r.left;

            if (y > r.bottom) y = r.height();
            else y = y - r.top;

            mState.mEffect.touchDown(x, y);

            // Cancel and Start new animation
            cancelAnim();
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

            // Start Exit animation
            if (!isEnterAnimating) {
                startExitAnim();
            }
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

    /**
     * The one constructor to rule them all. This is called by all public
     * constructors to set the state and initialize local properties.
     */
    private TouchEffectDrawable(TouchEffectState state, Resources res, ColorStateList color) {
        super(color);
        mState = state;
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
     * EffectDrawable to use a special {@link android.graphics.Shader}, such as a
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

    static TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    /**
     * This drawable call view perform by interface
     */
    public interface PerformClicker {
        void perform();
    }

    public boolean isPerformClick() {
        if (!isPerformClick) {
            isPerformClick = true;
            return false;
        } else {
            if (isRunning)
                return false;
            else {
                isPerformClick = false;
                return true;
            }
        }
    }

    protected void performClick() {
        if (isPerformClick) {
            PerformClicker clicker = getPerformClicker();
            if (clicker != null) {
                clicker.perform();
            }
        }
    }

    public final void setPerformClicker(PerformClicker clicker) {
        mPerformClicker = new WeakReference<PerformClicker>(clicker);
    }

    public PerformClicker getPerformClicker() {
        if (mPerformClicker != null) {
            return mPerformClicker.get();
        }
        return null;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {
        unscheduleSelf(mEnterAnimate);
        unscheduleSelf(mExitAnimate);
    }

    /**
     * Return this draw animation is running
     *
     * @return isRunning
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public int getEnterDuration() {
        return mEnterDuration;
    }

    public int getExitDuration() {
        return mExitDuration;
    }

    public void setEnterDuration(float factor) {
        mEnterDuration = (int) (factor * ANIM_ENTER_DURATION);
    }

    public void setExitDuration(float factor) {
        mExitDuration = (int) (factor * ANIM_Exit_DURATION);
    }

    public Interpolator getEnterInterpolator() {
        return mEnterInterpolator;
    }

    public Interpolator getExitInterpolator() {
        return mExitInterpolator;
    }

    public void setEnterInterpolator(Interpolator inInterpolator) {
        this.mEnterInterpolator = inInterpolator;
    }

    public void setExitInterpolator(Interpolator inInterpolator) {
        this.mExitInterpolator = inInterpolator;
    }

    private void startEnterAnim() {
        isEnterAnimating = true;
        isRunning = true;

        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mEnterAnimate, mStartTime);
    }

    private void startExitAnim() {
        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mExitAnimate, mStartTime);
    }

    private void cancelAnim() {
        unscheduleSelf(mEnterAnimate);
        unscheduleSelf(mExitAnimate);
        isRunning = false;
    }

    private final Runnable mEnterAnimate = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff < mEnterDuration) {
                float interpolation = mEnterInterpolator.getInterpolation((float) diff / (float) mEnterDuration);
                // Notify
                onEnterAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onEnterAnimateUpdate(1f);
                invalidateSelf();

                // Call end
                onEnterAnimateEnd();
            }
        }
    };

    private final Runnable mExitAnimate = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff < mExitDuration) {
                float interpolation = mExitInterpolator.getInterpolation((float) diff / (float) mExitDuration);
                // Notify
                onExitAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onExitAnimateUpdate(1f);
                invalidateSelf();

                // Call end
                onExitAnimateEnd();
            }
        }
    };

    protected void onEnterAnimateUpdate(float factor) {
        mState.mEffect.animationEnter(factor);
    }

    protected void onExitAnimateUpdate(float factor) {
        mState.mEffect.animationExit(factor);
    }

    protected void onEnterAnimateEnd() {
        // End
        isEnterAnimating = false;
        // Is un touch auto startExitAnim()
        if (isTouchReleased) startExitAnim();

    }

    protected void onExitAnimateEnd() {
        // End
        isRunning = false;
        // Click
        performClick();
    }
}
