package net.qiujuer.genius.ui.drawable;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.GeniusUi;
import net.qiujuer.genius.ui.drawable.effect.Effect;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * This is touch effect drawable
 * This drawable is can use background or other draw call
 */
public class TouchEffectDrawable extends Drawable {
    /**
     * This is drawable animation
     */
    static final long FRAME_DURATION = 16;
    // Time
    static final int IN_ANIM_DURATION = 280;
    static final int OUT_ANIM_DURATION = 160;
    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;

    // Base
    private TouchEffectState mState;
    private PorterDuffColorFilter mTintFilter;
    private boolean mMutated;

    // Touch
    protected boolean isTouchReleased = false;
    protected boolean isPerformClick = false;
    private WeakReference<PerformClicker> mPerformClicker = null;


    // Animation
    private boolean isRunning = false;
    private boolean isAnimatingIn = false;
    private long mStartTime;
    private Interpolator mInInterpolator = new DecelerateInterpolator(2.8f);
    private Interpolator mOutInterpolator = new AccelerateInterpolator();
    private int mInDuration = IN_ANIM_DURATION;
    private int mOutDuration = OUT_ANIM_DURATION;


    public TouchEffectDrawable() {
        this(new TouchEffectState(null), null);
    }


    public TouchEffectDrawable(Effect s) {
        this(new TouchEffectState(null), null);

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
     * Returns the ShaderFactory used by this EffectDrawable for requesting a
     * {@link android.graphics.Shader}.
     */
    public ShaderFactory getShaderFactory() {
        return mState.mShaderFactory;
    }

    /**
     * Returns the Paint used to draw the shape.
     */
    public Paint getPaint() {
        return mState.mPaint;
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


    private static int modulateAlpha(int paintAlpha, int alpha) {
        int scale = alpha + (alpha >>> 7); // convert to 0..256
        return paintAlpha * scale >>> 8;
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
        final Rect r = getBounds();
        final TouchEffectState state = mState;
        final Paint paint = state.mPaint;

        final int prevAlpha = paint.getAlpha();
        paint.setAlpha(GeniusUi.modulateAlpha(prevAlpha, state.mAlpha));

        // only draw shape if it may affect output
        if (paint.getAlpha() != 0 || paint.getXfermode() != null /*|| paint.hasShadowLayer()*/) {
            final boolean clearColorFilter;
            if (mTintFilter != null && paint.getColorFilter() == null) {
                paint.setColorFilter(mTintFilter);
                clearColorFilter = true;
            } else {
                clearColorFilter = false;
            }

            if (state.mEffect != null) {
                // need the save both for the translate, and for the (unknown)
                // Effect
                final int count = canvas.save();
                // Translate
                canvas.translate(r.left, r.top);
                // Clip the canvas
                if (state.mClipPath != null)
                    canvas.clipPath(state.mClipPath);
                onDraw(state.mEffect, canvas, paint);
                canvas.restoreToCount(count);
            } else {
                canvas.drawRect(r, paint);
            }

            if (clearColorFilter) {
                paint.setColorFilter(null);
            }
        }

        // restore
        paint.setAlpha(prevAlpha);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations()
                | mState.mChangingConfigurations;
    }


    /**
     * Set the alpha level for this drawable [0..255]. Note that this drawable
     * also has a color in its paint, which has an alpha as well. These two
     * values are automatically combined during drawing. Thus if the color's
     * alpha is 75% (i.e. 192) and the drawable's alpha is 50% (i.e. 128), then
     * the combined alpha that will be used during drawing will be 37.5% (i.e.
     * 96).
     */
    @Override
    public void setAlpha(int alpha) {
        mState.mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return mState.mAlpha;
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mState.mTint = tint;
        mTintFilter = updateTintFilter(mTintFilter, tint, mState.mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        mState.mTintMode = tintMode;
        mTintFilter = updateTintFilter(mTintFilter, mState.mTint, tintMode);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mState.mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        if (mState.mEffect == null) {
            final Paint p = mState.mPaint;
            if (p.getXfermode() == null) {
                final int alpha = p.getAlpha();
                if (alpha == 0) {
                    return PixelFormat.TRANSPARENT;
                }
                if (alpha == 255) {
                    return PixelFormat.OPAQUE;
                }
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setDither(boolean dither) {
        mState.mPaint.setDither(dither);
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateEffect();
        updateClipRectPath();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        final TouchEffectState state = mState;
        if (state.mTint != null && state.mTintMode != null) {
            mTintFilter = updateTintFilter(mTintFilter, state.mTint, state.mTintMode);
            return true;
        }
        return false;
    }

    @Override
    public boolean isStateful() {
        final TouchEffectState s = mState;
        return super.isStateful() || (s.mTint != null && s.mTint.isStateful());
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
            if (mState.mPaint != null) {
                mState.mPaint = new Paint(mState.mPaint);
            } else {
                mState.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
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

    public void setClipRadius(float radius) {
        this.mState.mRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        updateClipRectPath();
    }

    public void setClipRadii(float[] radii) {
        if (radii == null || radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii must have >= 8 values");
        }
        this.mState.mRadii = radii;
        updateClipRectPath();
    }

    public void onTouch(MotionEvent event) {
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
        }
    }

    protected void onTouchDown(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            mState.mEffect.touchDown(x - r.left, y - r.top);

            // Cancel and Start new animation
            cancelAnim();
            startInAnim();
        }
    }

    protected void onTouchReleased(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            mState.mEffect.touchReleased(x - r.left, y - r.top);

            // StartOutAnim
            if (!isAnimatingIn) {
                startOutAnim();
            }
        }
    }

    protected void onTouchMove(float x, float y) {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            mState.mEffect.touchMove(x - r.left, y - r.top);
        }
    }


    private void updateEffect() {
        if (mState.mEffect != null) {
            final Rect r = getBounds();
            final int w = r.width();
            final int h = r.height();

            mState.mEffect.resize(w, h);
            if (mState.mShaderFactory != null) {
                mState.mPaint.setShader(mState.mShaderFactory.resize(w, h));
            }
        }
        invalidateSelf();
    }

    private void updateClipRectPath() {
        if (mState.mRadii != null) {
            if (mState.mClipPath == null)
                mState.mClipPath = new Path();
            else
                mState.mClipPath.reset();

            final Rect r = getBounds();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                mState.mClipPath.addRoundRect(new RectF(0, 0, r.width(), r.height()), mState.mRadii, Path.Direction.CW);
            else
                mState.mClipPath.addRoundRect(0, 0, r.width(), r.height(), mState.mRadii, Path.Direction.CW);
        } else {
            mState.mClipPath = null;
        }
    }

    final static class TouchEffectState extends ConstantState {
        int[] mThemeAttrs;
        int mChangingConfigurations;
        Paint mPaint;
        Effect mEffect;
        ColorStateList mTint = null;
        PorterDuff.Mode mTintMode = DEFAULT_TINT_MODE;
        Rect mPadding;
        int mIntrinsicWidth;
        int mIntrinsicHeight;
        int mAlpha = 255;
        ShaderFactory mShaderFactory;
        float[] mRadii;
        Path mClipPath;

        TouchEffectState(TouchEffectState orig) {
            if (orig != null) {
                mThemeAttrs = orig.mThemeAttrs;
                mPaint = orig.mPaint;
                mEffect = orig.mEffect;
                mTint = orig.mTint;
                mTintMode = orig.mTintMode;
                mPadding = orig.mPadding;
                mIntrinsicWidth = orig.mIntrinsicWidth;
                mIntrinsicHeight = orig.mIntrinsicHeight;
                mAlpha = orig.mAlpha;
                mShaderFactory = orig.mShaderFactory;
                mRadii = orig.mRadii;
            } else {
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
        }

        @Override
        public boolean canApplyTheme() {
            return mThemeAttrs != null;
        }

        @Override
        public Drawable newDrawable() {
            return new TouchEffectDrawable(this, null);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new TouchEffectDrawable(this, res);
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
    private TouchEffectDrawable(TouchEffectState state, Resources res) {
        mState = state;

        initializeWithState(state, res);
    }

    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     */
    private void initializeWithState(TouchEffectState state, Resources res) {
        mTintFilter = updateTintFilter(mTintFilter, state.mTint, state.mTintMode);
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
         * EffectDrawable.setEffect().
         *
         * @param width  the width of the Drawable being drawn
         * @param height the heigh of the Drawable being drawn
         * @return the Shader to be drawn
         */
        public abstract Shader resize(int width, int height);
    }

    // other subclass could wack the Shader's localmatrix based on the
    // resize params (e.g. scaletofit, etc.). This could be used to scale
    // a bitmap to fill the bounds without needing any other special casing.


    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter tintFilter, ColorStateList tint,
                                           PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }

        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        if (tintFilter == null) {
            return new PorterDuffColorFilter(color, tintMode);
        }

        //tintFilter.setColor(color);
        //tintFilter.setMode(tintMode);

        try {
            Class<PorterDuffColorFilter> tClass = (Class<PorterDuffColorFilter>) tintFilter.getClass();
            Method method = tClass.getMethod("setColor", Integer.class);
            method.invoke(tintFilter, color);

            method = tClass.getMethod("setMode", PorterDuff.Mode.class);
            method.invoke(tintFilter, tintMode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tintFilter;
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
            return !isRunning;
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


    /**
     * Return this draw animation is running
     *
     * @return isRunning
     */
    public boolean isRunning() {
        return isRunning;
    }


    public void setInDuration(int duration) {
        mInDuration = duration;
    }

    public void setOutDuration(int duration) {
        mOutDuration = duration;
    }

    public void setInInterpolator(Interpolator inInterpolator) {
        this.mInInterpolator = inInterpolator;
    }

    public void setOutInterpolator(Interpolator inInterpolator) {
        this.mOutInterpolator = inInterpolator;
    }

    private void startInAnim() {
        isAnimatingIn = true;
        isRunning = true;

        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mInAnim, mStartTime);
    }

    private void startOutAnim() {
        // Start animation
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mOutAnim, mStartTime);
    }

    private void cancelAnim() {
        unscheduleSelf(mInAnim);
        unscheduleSelf(mOutAnim);
        isRunning = false;
    }

    private final Runnable mInAnim = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff < mInDuration) {
                float interpolation = mInInterpolator.getInterpolation((float) diff / (float) mInDuration);
                // Notify
                onInAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onInAnimateUpdate(1f);
                invalidateSelf();

                // Call end
                onInAnimateEnd();
            }
        }
    };

    private final Runnable mOutAnim = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            if (diff < mOutDuration) {
                float interpolation = mOutInterpolator.getInterpolation((float) diff / (float) mOutDuration);
                // Notify
                onOutAnimateUpdate(interpolation);
                invalidateSelf();

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {

                unscheduleSelf(this);

                // Notify
                onOutAnimateUpdate(1f);
                invalidateSelf();

                // Call end
                onOutAnimateEnd();
            }
        }
    };

    protected void onInAnimateUpdate(float factor) {
        mState.mEffect.animationIn(factor);
    }

    protected void onOutAnimateUpdate(float factor) {
        mState.mEffect.animationOut(factor);
    }

    protected void onInAnimateEnd() {
        // End
        isAnimatingIn = false;
        // Is un touch auto startOutAnim()
        if (isTouchReleased) startOutAnim();

    }

    protected void onOutAnimateEnd() {
        // End
        isRunning = false;
        // Click
        performClick();
    }
}
