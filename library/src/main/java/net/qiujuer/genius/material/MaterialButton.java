package net.qiujuer.genius.material;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.MaterialUI;
import net.qiujuer.genius.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


public class MaterialButton extends Button implements Attributes.AttributeChangeListener {
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    private Paint backgroundPaint;
    private static ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private float paintX, paintY, radius;
    private int bottom;
    private Attributes attributes;

    public MaterialButton(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @SuppressWarnings("deprecation")
    private void init(AttributeSet attrs, int defStyle) {
        // default values of specific attributes
        // saving padding values for using them after setting background drawable
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (attributes == null)
            attributes = new Attributes(this, getResources());

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialButton, defStyle, 0);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.MaterialButton_m_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());

            attributes.setFontFamily(a.getString(R.styleable.MaterialButton_m_fontFamily));
            attributes.setFontWeight(a.getString(R.styleable.MaterialButton_m_fontWeight));
            attributes.setFontExtension(a.getString(R.styleable.MaterialButton_m_fontExtension));

            attributes.setTextAppearance(a.getInt(R.styleable.MaterialButton_m_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            attributes.setRadius(a.getDimensionPixelSize(R.styleable.MaterialButton_m_cornerRadius, Attributes.DEFAULT_RADIUS_PX));

            attributes.setMaterial(a.getBoolean(R.styleable.MaterialButton_m_isMaterial, true));

            // getting view specific attributes
            bottom = a.getDimensionPixelSize(R.styleable.MaterialButton_m_blockButtonEffectHeight, bottom);

            a.recycle();
        }

        // creating normal state drawable
        ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalFront.getPaint().setColor(attributes.getColor(2));

        ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalBack.getPaint().setColor(attributes.getColor(1));

        normalBack.setPadding(0, 0, 0, bottom);

        Drawable[] d = {normalBack, normalFront};
        LayerDrawable normal = new LayerDrawable(d);

        // creating disabled state drawable
        ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledFront.getPaint().setColor(attributes.getColor(3));

        ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledBack.getPaint().setColor(attributes.getColor(2));

        Drawable[] d3 = {disabledBack, disabledFront};
        LayerDrawable disabled = new LayerDrawable(d3);

        // set StateListDrawable
        StateListDrawable states = new StateListDrawable();
        if (!attributes.isMaterial()) {
            // creating pressed state drawable
            ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedFront.getPaint().setColor(attributes.getColor(1));

            ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
            pressedBack.getPaint().setColor(attributes.getColor(0));
            if (bottom != 0) pressedBack.setPadding(0, 0, 0, bottom / 2);

            Drawable[] d2 = {pressedBack, pressedFront};
            LayerDrawable pressed = new LayerDrawable(d2);

            states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
            states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
        }

        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

        // set Background
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            setBackgroundDrawable(states);
        else
            setBackground(states);

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (attributes.getTextAppearance() == 1) setTextColor(attributes.getColor(0));
        else if (attributes.getTextAppearance() == 2) setTextColor(attributes.getColor(3));
        else setTextColor(Color.WHITE);

        backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(attributes.getColor(1));

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = MaterialUI.getFont(getContext(), attributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    @Override
    public void onThemeChange() {
        init(null, 0);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        canvas.drawCircle(paintX, paintY, radius, backgroundPaint);
        canvas.restore();

        //绘制父类
        super.onDraw(canvas);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (attributes.isMaterial() && event.getAction() == MotionEvent.ACTION_DOWN) {
            //按下
            paintX = event.getX();
            paintY = event.getY();
            startAnimator();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 启动动画
     */
    private void startAnimator() {
        int start, end;

        if (getHeight() < getWidth()) {
            start = getHeight();
            end = getWidth();
        } else {
            start = getWidth();
            end = getHeight();
        }

        float startRadius = (start / 2 > paintY ? start - paintY : paintY) * 1.15f;
        float endRadius = (end / 2 > paintX ? end - paintX : paintX) * 0.85f;

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(this, mRadiusProperty, startRadius, endRadius),
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, argbEvaluator, attributes.getColor(1), attributes.getColor(2))
        );
        // 设置时间
        set.setDuration((long) (1200 / end * endRadius));
        // 逐渐缓慢
        set.setInterpolator(ANIMATION_INTERPOLATOR);
        set.start();
    }

    //半径属性
    private Property<MaterialButton, Float> mRadiusProperty = new Property<MaterialButton, Float>(Float.class, "radius") {
        @Override
        public Float get(MaterialButton object) {
            return object.radius;
        }

        @Override
        public void set(MaterialButton object, Float value) {
            object.radius = value;
            invalidate();
        }
    };
    //颜色属性
    private Property<MaterialButton, Integer> mBackgroundColorProperty = new Property<MaterialButton, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(MaterialButton object) {
            return object.backgroundPaint.getColor();
        }

        @Override
        public void set(MaterialButton object, Integer value) {
            object.backgroundPaint.setColor(value);
        }
    };

}
