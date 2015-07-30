package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import net.qiujuer.genius.ui.GeniusUi;
import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.FloatEffect;

public class FloatActionButton extends ImageView implements TouchEffectDrawable.PerformClicker {
    private int mShadowRadius;
    private TouchEffectDrawable mTouchDrawable;
    private ColorStateList mBackgroundColor;

    public FloatActionButton(Context context) {
        super(context);
        init(null, 0);
    }

    public FloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(FloatActionButton.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(FloatActionButton.class.getName());
    }


    private void init(AttributeSet attrs, int defStyle) {
        final Context context = getContext();

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FloatActionButton, defStyle, 0);

        ColorStateList bgColor = a.getColorStateList(R.styleable.FloatActionButton_gColorBackground);
        int touchColor = a.getColor(R.styleable.FloatActionButton_gColorTouch, GeniusUi.TOUCH_PRESS_COLOR);

        a.recycle();

        // Enable
        boolean enable = GeniusUi.isEnableAttr(context, attrs);
        setEnabled(enable);

        // BackgroundColor
        if (bgColor == null) {
            bgColor = getResources().getColorStateList(R.color.g_defaulf_background);
        }

        // Background drawable
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * GeniusUi.Y_OFFSET);
        final int shadowXOffset = (int) (density * GeniusUi.X_OFFSET);
        final int maxShadowOffset = Math.max(shadowXOffset, shadowYOffset);

        mShadowRadius = (int) (density * GeniusUi.SHADOW_RADIUS);
        mShadowRadius += maxShadowOffset;

        ShapeDrawable background;
        if (GeniusUi.elevationSupported) {
            background = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, GeniusUi.SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new OvalShadowShape(mShadowRadius);
            background = new ShapeDrawable(oval);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, background.getPaint());
            background.getPaint().setShadowLayer(mShadowRadius - maxShadowOffset, shadowXOffset, shadowYOffset,
                    GeniusUi.KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        setBackgroundDrawable(background);
        setBackgroundColor(bgColor);

        // TouchDrawable
        mTouchDrawable = new TouchEffectDrawable(new FloatEffect(), ColorStateList.valueOf(touchColor));
        mTouchDrawable.setCallback(this);
        mTouchDrawable.setPerformClicker(this);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mBackgroundColor != null) {
            setBackgroundColor(mBackgroundColor.getColorForState(getDrawableState(), mBackgroundColor.getDefaultColor()));
        }
    }

    public void setBackgroundColor(ColorStateList colorStateList) {
        if (colorStateList != null && mBackgroundColor != colorStateList) {
            mBackgroundColor = colorStateList;
            setBackgroundColor(mBackgroundColor.getColorForState(getDrawableState(), mBackgroundColor.getDefaultColor()));
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(getContext().getResources().getColor(colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    public void setPressColor(int color) {
        mTouchDrawable.getPaint().setColor(color);
        invalidate();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

        left = Math.max(mShadowRadius, left);
        top = Math.max(mShadowRadius, top);
        right = Math.max(mShadowRadius, right);
        bottom = Math.max(mShadowRadius, bottom);

        super.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!GeniusUi.elevationSupported) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTouchDrawable.setBounds(mShadowRadius, mShadowRadius, getWidth() - mShadowRadius, getHeight() - mShadowRadius);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mTouchDrawable || super.verifyDrawable(who);
    }


    @Override
    public boolean performClick() {
        final TouchEffectDrawable d = mTouchDrawable;

        if (d != null) {
            return d.isPerformClick() && super.performClick();
        } else
            return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null && isEnabled()) {
            d.onTouch(event);
            super.onTouchEvent(event);
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        final TouchEffectDrawable d = mTouchDrawable;
        if (d != null) {
            d.draw(canvas);
        }

        super.onDraw(canvas);
    }

    @Override
    public void perform() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                performClick();
            }
        };

        if (!this.post(runnable)) {
            performClick();
        }
    }

    private static class OvalShadowShape extends OvalShape {
        private Paint mShadowPaint;
        private float mCenterX;
        private float mCenterY;
        private float mRadius;
        private int mShadowRadius;


        public OvalShadowShape(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            mCenterX = width / 2;
            mCenterY = height / 2;
            mRadius = Math.min(mCenterX, mCenterY);

            RadialGradient radialGradient = new RadialGradient(mCenterX, mCenterY,
                    mShadowRadius, new int[]{GeniusUi.FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(radialGradient);

        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mShadowPaint);
            canvas.drawCircle(mCenterX, mCenterY, mRadius - mShadowRadius, paint);
        }
    }
}
