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


public class MaterialRectButton extends Button implements Attributes.AttributeChangeListener {
    private static final long ANIMATION_DURATION = 600;
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final long MIN_LONG = 300;

    private Paint mBackgroundPaint;


    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();


    public float paintX, paintY;
    public float left, top, right, bottom;


    private Attributes attributes;


    public MaterialRectButton(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialRectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialRectButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        /*
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MaterialButton, defStyle, 0);
        a.recycle();
        */


        // default values of specific attributes
        int bottom = 0;
        // saving padding values for using them after setting background drawable
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (attributes == null)
            attributes = new Attributes(this, getResources());

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.fl_FlatButton);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.fl_FlatButton_fl_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());

            attributes.setFontFamily(a.getString(R.styleable.fl_FlatButton_fl_fontFamily));
            attributes.setFontWeight(a.getString(R.styleable.fl_FlatButton_fl_fontWeight));
            attributes.setFontExtension(a.getString(R.styleable.fl_FlatButton_fl_fontExtension));

            attributes.setTextAppearance(a.getInt(R.styleable.fl_FlatButton_fl_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            attributes.setRadius(a.getDimensionPixelSize(R.styleable.fl_FlatButton_fl_cornerRadius, Attributes.DEFAULT_RADIUS_PX));

            // getting view specific attributes
            bottom = a.getDimensionPixelSize(R.styleable.fl_FlatButton_fl_blockButtonEffectHeight, bottom);

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

        // creating pressed state drawable
        ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        pressedFront.getPaint().setColor(attributes.getColor(1));

        ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        pressedBack.getPaint().setColor(attributes.getColor(0));
        if (bottom != 0) pressedBack.setPadding(0, 0, 0, bottom / 2);

        Drawable[] d2 = {pressedBack, pressedFront};
        LayerDrawable pressed = new LayerDrawable(d2);

        // creating disabled state drawable
        ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledFront.getPaint().setColor(attributes.getColor(3));

        ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledBack.getPaint().setColor(attributes.getColor(2));

        Drawable[] d3 = {disabledBack, disabledFront};
        LayerDrawable disabled = new LayerDrawable(d3);

        StateListDrawable states = new StateListDrawable();

        //states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        //states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

        setBackgroundDrawable(states);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (attributes.getTextAppearance() == 1) setTextColor(attributes.getColor(0));
        else if (attributes.getTextAppearance() == 2) setTextColor(attributes.getColor(3));
        else setTextColor(Color.WHITE);

        mBackgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(attributes.getColor(1));

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //canvas.saveLayer(rect, mBackgroundPaint, Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        canvas.translate(paintX, paintY);
        /*
        // Left Top Right Bottom
        RectF rect = new RectF(left, top, right, bottom);
        float r = (bottom - top);
        //x y color
        canvas.drawRoundRect(rect, r / 3, r / 3, mBackgroundPaint);
        */
        canvas.drawCircle(0, 0, left, mBackgroundPaint);
        canvas.restore();

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paintX = event.getX();
                paintY = event.getY();
                animatePlus();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void animatePlus() {
        //mBackgroundColorProperty.set(this, attributes.getColor(1));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(this, mLeftProperty, getHeight() / 2, getWidth() / 2),
                /*ObjectAnimator.ofFloat(this, mTopProperty, -paintY, -paintY),
                ObjectAnimator.ofFloat(this, mRightProperty, MIN_LONG, getWidth() - paintX),
                ObjectAnimator.ofFloat(this, mBottomProperty, getHeight() - paintY, getHeight() - paintY),*/
                ObjectAnimator.ofObject(this, mBackgroundColorProperty, mArgbEvaluator, attributes.getColor(1), attributes.getColor(2))
        );


        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(ANIMATION_INTERPOLATOR);
        set.start();
    }

    private Property<MaterialRectButton, Float> mBottomProperty = new Property<MaterialRectButton, Float>(Float.class, "bottom") {
        @Override
        public Float get(MaterialRectButton object) {
            return object.bottom;
        }

        @Override
        public void set(MaterialRectButton object, Float value) {
            object.bottom = value;
            invalidate();
        }
    };
    private Property<MaterialRectButton, Float> mRightProperty = new Property<MaterialRectButton, Float>(Float.class, "right") {
        @Override
        public Float get(MaterialRectButton object) {
            return object.right;
        }

        @Override
        public void set(MaterialRectButton object, Float value) {
            object.right = value;
            invalidate();
        }
    };
    private Property<MaterialRectButton, Float> mTopProperty = new Property<MaterialRectButton, Float>(Float.class, "top") {
        @Override
        public Float get(MaterialRectButton object) {
            return object.top;
        }

        @Override
        public void set(MaterialRectButton object, Float value) {
            object.top = value;
            invalidate();
        }
    };
    private Property<MaterialRectButton, Float> mLeftProperty = new Property<MaterialRectButton, Float>(Float.class, "left") {
        @Override
        public Float get(MaterialRectButton object) {
            return object.left;
        }

        @Override
        public void set(MaterialRectButton object, Float value) {
            object.left = value;
            invalidate();
        }
    };
    private Property<MaterialRectButton, Integer> mBackgroundColorProperty = new Property<MaterialRectButton, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(MaterialRectButton object) {
            return object.mBackgroundPaint.getColor();
        }

        @Override
        public void set(MaterialRectButton object, Integer value) {
            object.mBackgroundPaint.setColor(value);
        }
    };

}
