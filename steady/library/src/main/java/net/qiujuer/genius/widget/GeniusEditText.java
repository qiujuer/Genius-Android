/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/23/2015
 * Changed 02/09/2015
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
package net.qiujuer.genius.widget;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;

import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;
import net.qiujuer.genius.drawable.shape.BorderShape;
import net.qiujuer.genius.widget.attribute.Attributes;
import net.qiujuer.genius.widget.attribute.BaseAttributes;
import net.qiujuer.genius.widget.attribute.EditTextAttributes;

/**
 * GeniusEditText this have a title from hint
 */
public class GeniusEditText extends EditText implements Attributes.AttributeChangeListener {
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANIMATION_DURATION = 300;

    private EditTextAttributes mAttributes;
    private TextPaint mTitlePaint;
    private TextWatcher mTextWatcher;
    private TitleProperty mCurTitleProperty;
    private ObjectAnimator mAnimator;

    private boolean isHaveText;
    private boolean isAttachWindow;

    public GeniusEditText(Context context) {
        super(context);
        init(null);
    }

    public GeniusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GeniusEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @SuppressWarnings("deprecation")
    private void init(AttributeSet attrs) {

        if (mAttributes == null)
            mAttributes = new EditTextAttributes(this, getResources());

        boolean showTitle = mAttributes.isShowTitle();

        if (attrs != null) {
            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusEditText);

            // getting common mAttributes
            int customTheme = a.getResourceId(R.styleable.GeniusEditText_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setTheme(customTheme, getResources());

            mAttributes.setFontFamily(BaseAttributes.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.GeniusEditText_g_fontFamily, 0)]);
            mAttributes.setFontWeight(BaseAttributes.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.GeniusEditText_g_fontWeight, 3)]);
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusEditText_g_fontExtension));

            mAttributes.setTextAppearance(a.getInt(R.styleable.GeniusEditText_g_textAppearance, BaseAttributes.DEFAULT_TEXT_APPEARANCE));
            mAttributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusEditText_g_borderWidth, BaseAttributes.DEFAULT_BORDER_WIDTH));

            // Set init Corners Radius
            mAttributes.initCornerRadius(a, R.styleable.GeniusEditText_g_cornerRadius,
                    R.styleable.GeniusEditText_g_cornerRadii_A, R.styleable.GeniusEditText_g_cornerRadii_B,
                    R.styleable.GeniusEditText_g_cornerRadii_C, R.styleable.GeniusEditText_g_cornerRadii_D);

            // getting view specific mAttributes
            mAttributes.setStyle(a.getInt(R.styleable.GeniusEditText_g_fieldStyle, mAttributes.getStyle()));

            // Set HintProperty
            mAttributes.setTitleTextColor(a.getColorStateList(R.styleable.GeniusEditText_g_titleTextColor));
            mAttributes.setTitleTextSize(a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titleTextSize, mAttributes.getTitleTextSize()));
            mAttributes.setTitleTextPaddingLeft(a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titlePaddingLeft, getPaddingLeft()));
            mAttributes.setTitleTextPaddingTop(a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titlePaddingTop, mAttributes.getTitleTextPaddingTop()));
            showTitle = a.getBoolean(R.styleable.GeniusEditText_g_showTitle, mCurTitleProperty == null || showTitle);
            a.recycle();
        }

        // Init methods
        if (!mAttributes.isHasOwnBackground())
            initBackground();
        if (!mAttributes.isHasOwnTextColor())
            initTextColor();
        if (!mAttributes.isHasOwnHintTextColor())
            initHintTextColor();

        initTitleText(showTitle);

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), mAttributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    private void initTitleText(boolean isShow) {
        if (isShow != mAttributes.isShowTitle()) {
            mAttributes.setShowTitle(isShow);
            // Padding
            final int paddingTop = getPaddingTop();
            final int paddingRight = getPaddingRight();
            final int paddingLeft = getPaddingLeft();
            final int paddingBottom = getPaddingBottom();
            if (isShow) {
                mCurTitleProperty = new TitleProperty();

                // Set up a default TextPaint object
                if (mTitlePaint == null) {
                    mTitlePaint = new TextPaint();
                    mTitlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                    mTitlePaint.setTextAlign(Paint.Align.LEFT);
                }

                setPadding(paddingLeft, paddingTop + mAttributes.getTitleTextSize(), paddingRight, paddingBottom);

                // Add Watcher
                if (mTextWatcher == null) {
                    mTextWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (mAttributes.isShowTitle() && mTitlePaint != null) {
                                boolean have = s != null && s.length() > 0;
                                if (have != isHaveText) {
                                    isHaveText = have;
                                    animateShowTitle(isHaveText);
                                }
                            }
                        }
                    };
                    addTextChangedListener(mTextWatcher);
                }

                // Show
                Editable editable = getText();
                animateShowTitle(editable != null && editable.length() > 0);
            } else {
                if (mTextWatcher != null) {
                    removeTextChangedListener(mTextWatcher);
                    mTextWatcher = null;
                }

                mTitlePaint = null;
                mCurTitleProperty = null;
                mAnimator = null;

                setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initBackground() {
        Drawable[] drawable = null;
        switch (mAttributes.getStyle()) {
            case EditTextAttributes.STYLE_FILL: {
                // Creating normal state drawable
                ShapeDrawable normal = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadiiNull(), null, null));
                normal.getPaint().setColor(mAttributes.getColor(2));

                // Creating pressed state drawable
                ShapeDrawable pressed = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadiiNull(), null, null));
                pressed.getPaint().setColor(mAttributes.getColor(1));

                // Creating disabled state drawable
                ShapeDrawable disabled = new ShapeDrawable(new RoundRectShape(mAttributes.getOuterRadiiNull(), null, null));
                disabled.getPaint().setColor(mAttributes.getColor(3));
                // disabled.getPaint().setAlpha(0xA0);

                drawable = new Drawable[]{pressed, normal, disabled};
            }
            break;
            case EditTextAttributes.STYLE_BOX: {
                // Creating normal state drawable
                GradientDrawable normal = new GradientDrawable();
                normal.setCornerRadii(mAttributes.getOuterRadiiNull());
                normal.setColor(Color.WHITE);
                normal.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(2));

                // Creating pressed state drawable
                GradientDrawable pressed = new GradientDrawable();
                pressed.setCornerRadii(mAttributes.getOuterRadiiNull());
                pressed.setColor(Color.WHITE);
                pressed.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(1));

                // Creating disabled state drawable
                GradientDrawable disabled = new GradientDrawable();
                disabled.setCornerRadii(mAttributes.getOuterRadiiNull());
                disabled.setColor(0x00ffffff | 0x1E000000);
                disabled.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(3));

                drawable = new Drawable[]{pressed, normal, disabled};
            }
            break;
            case EditTextAttributes.STYLE_TRANSPARENT: {
                // Creating normal state drawable
                GradientDrawable normal = new GradientDrawable();
                normal.setCornerRadii(mAttributes.getOuterRadiiNull());
                normal.setColor(Color.TRANSPARENT);
                normal.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(2));

                // Creating pressed state drawable
                GradientDrawable pressed = new GradientDrawable();
                pressed.setCornerRadii(mAttributes.getOuterRadiiNull());
                pressed.setColor(Color.TRANSPARENT);
                pressed.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(1));

                // Creating disabled state drawable
                GradientDrawable disabled = new GradientDrawable();
                disabled.setCornerRadii(mAttributes.getOuterRadiiNull());
                disabled.setColor(Color.TRANSPARENT);
                disabled.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(3));

                drawable = new Drawable[]{pressed, normal, disabled};
            }
            break;
            case EditTextAttributes.STYLE_LINE: {
                // Creating normal state drawable
                ShapeDrawable normal = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, mAttributes.getBorderWidth())));
                normal.getPaint().setColor(mAttributes.getColor(2));

                // Creating pressed state drawable
                ShapeDrawable pressed = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, mAttributes.getBorderWidth() > 0 ? mAttributes.getBorderWidth() +
                        getContext().getResources().getDimensionPixelSize(R.dimen.genius_editText_lineStyle_selectBorder) : 0)));
                pressed.getPaint().setColor(mAttributes.getColor(1));

                // Creating disabled state drawable
                ShapeDrawable disabled = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, mAttributes.getBorderWidth() / 2), mAttributes.getBorderWidth() / 2, mAttributes.getBorderWidth()));
                disabled.getPaint().setColor(mAttributes.getColor(3));
                // disabled.getPaint().setAlpha(0xA0);

                drawable = new Drawable[]{pressed, normal, disabled};
            }
            break;
        }

        if (drawable != null) {
            Drawable states = createStateListDrawable(drawable);
            // Set Background
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                setBackgroundDrawable(states);
            else
                setBackground(states);
        }
    }

    private void initTextColor() {
        ColorStateList colors = null;
        switch (mAttributes.getStyle()) {
            case EditTextAttributes.STYLE_FILL:
                if (mAttributes.getTextAppearance() == 1)
                    colors = (createColorStateList(Color.BLACK, 0x2E000000));
                else if (mAttributes.getTextAppearance() == 2)
                    colors = (createColorStateList(Color.WHITE, 0x2E000000));
                else colors = (createColorStateList(0xEEFFFFFF, 0x2E000000));
                break;
            case EditTextAttributes.STYLE_BOX:
                if (mAttributes.getTextAppearance() == 1)
                    colors = (createColorStateList(mAttributes.getColor(1), mAttributes.getColor(1) & 0x2E000000));
                else if (mAttributes.getTextAppearance() == 2)
                    colors = (createColorStateList(mAttributes.getColor(3), mAttributes.getColor(3) & 0x2E000000));
                else
                    colors = (createColorStateList(mAttributes.getColor(2), mAttributes.getColor(2) & 0x2E000000));
                break;
            case EditTextAttributes.STYLE_TRANSPARENT:
                if (mAttributes.getTextAppearance() == 1)
                    colors = (createColorStateList(mAttributes.getColor(0), mAttributes.getColor(0) & 0x2E000000));
                else if (mAttributes.getTextAppearance() == 2)
                    colors = (createColorStateList(mAttributes.getColor(2), mAttributes.getColor(2) & 0x2E000000));
                else
                    colors = (createColorStateList(mAttributes.getColor(1), mAttributes.getColor(1) & 0x2E000000));
                break;
            case EditTextAttributes.STYLE_LINE:
                if (mAttributes.getTextAppearance() == 1)
                    colors = (createColorStateList(Color.BLACK, 0x5E000000));
                else if (mAttributes.getTextAppearance() == 2)
                    colors = (createColorStateList(Color.WHITE, 0x5EFFFFFF));
                else
                    colors = (createColorStateList(mAttributes.getColor(0), mAttributes.getColor(0) & 0x2E000000));
                break;
        }
        if (colors != null) {
            setTextColor(colors);
        }
    }

    private void initHintTextColor() {
        ColorStateList colors;
        switch (mAttributes.getStyle()) {
            case EditTextAttributes.STYLE_FILL:
                colors = (createColorStateList(mAttributes.getColor(3), 0xEEFFFFFF, 0x40000000));
                break;
            case EditTextAttributes.STYLE_BOX:
                colors = (createColorStateList(mAttributes.getColor(3), mAttributes.getColor(2), mAttributes.getColor(3) & 0x00ffffff | 0x90000000));
                break;
            default:
                colors = (createColorStateList(mAttributes.getColor(3), mAttributes.getColor(2), mAttributes.getColor(3) & 0x00ffffff | 0x90000000));
                break;
        }

        if (colors != null) {
            setHintTextColor(colors);
        }
    }

    private void setTitleProperty(TitleProperty value) {
        mCurTitleProperty = value;
        invalidate();
    }

    private int getCurrentTitleTextColor() {
        if (mAttributes.getTitleTextColor() == null) {
            ColorStateList colors = getHintTextColors();
            if (colors == null)
                return getCurrentHintTextColor();
            else
                return colors.getColorForState(getDrawableState(), 0);
        } else
            return mAttributes.getTitleTextColor().getColorForState(getDrawableState(), 0);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onDraw(Canvas canvas) {

        // Draw Title Text
        if (mAttributes.isShowTitle() && mTitlePaint != null && mCurTitleProperty != null && mCurTitleProperty.mAlpha != 0) {
            CharSequence buf = getHint();
            if (buf != null) {
                mTitlePaint.setTextSize(mCurTitleProperty.mTextSize);

                int color = getCurrentTitleTextColor();
                int alpha = GeniusUI.modulateAlpha(Color.alpha(color), mCurTitleProperty.mAlpha);

                mTitlePaint.setColor(color);
                mTitlePaint.setAlpha(alpha);

                canvas.drawText(buf, 0, buf.length(),
                        mCurTitleProperty.mPaddingLeft,
                        mCurTitleProperty.mPaddingTop + mCurTitleProperty.mTextSize,
                        mTitlePaint);
            }
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachWindow = false;
        if (mTextWatcher != null) {
            removeTextChangedListener(mTextWatcher);
            mTextWatcher = null;
        }
    }

    @Override
    public void onThemeChange() {
        init(null);
    }

    @Override
    public EditTextAttributes getAttributes() {
        return mAttributes;
    }

    /**
     * =============================================================================================
     * Init State List Drawable and Color
     * =============================================================================================
     */

    private StateListDrawable createStateListDrawable(Drawable drawable[]) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, drawable[0]);
        states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, drawable[0]);
        states.addState(new int[]{android.R.attr.state_enabled}, drawable[1]);
        states.addState(new int[]{-android.R.attr.state_enabled}, drawable[2]);
        return states;
    }

    private ColorStateList createColorStateList(int normal, int unable) {
        int[] colors = new int[]{normal, unable};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }

    private ColorStateList createColorStateList(int normal, int pressed, int unable) {
        int[] colors = new int[]{pressed, pressed, normal, unable};
        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_focused, android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }

    /**
     * =============================================================================================
     * The Animate
     * =============================================================================================
     */

    private void animateShowTitle(boolean show) {
        TitleProperty property;
        if (show)
            property = new TitleProperty(mAttributes);
        else {
            property = new TitleProperty();
            property.mAlpha = 0;
            property.mTextSize = (int) getTextSize();
            property.mPaddingLeft = getPaddingLeft();
            property.mPaddingTop = getPaddingTop();
        }

        if (isAttachWindow) {
            if (mAnimator == null) {
                mAnimator = ObjectAnimator.ofObject(this, TITLE_PROPERTY, new TitleEvaluator(mCurTitleProperty), property);
                mAnimator.setDuration(ANIMATION_DURATION);
                mAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
            } else {
                mAnimator.cancel();
                mAnimator.setObjectValues(property);
            }
            mAnimator.start();
        } else {
            setTitleProperty(property);
        }
    }

    /**
     * =============================================================================================
     * The custom properties
     * =============================================================================================
     */

    public final static class TitleProperty {
        private int mPaddingLeft;
        private int mPaddingTop;
        private int mTextSize;
        private int mAlpha;

        public TitleProperty() {
        }

        public TitleProperty(EditTextAttributes attributes) {
            mPaddingLeft = attributes.getTitleTextPaddingLeft();
            mPaddingTop = attributes.getTitleTextPaddingTop();
            mTextSize = attributes.getTitleTextSize();
            mAlpha = 255;
        }
    }

    private final static class TitleEvaluator implements TypeEvaluator<TitleProperty> {

        private final TitleProperty mProperty;

        public TitleEvaluator(TitleProperty property) {
            mProperty = property;
        }

        @Override
        public TitleProperty evaluate(float fraction, TitleProperty startValue, TitleProperty endValue) {
            // Values
            mProperty.mPaddingLeft = (int) (startValue.mPaddingLeft + (endValue.mPaddingLeft - startValue.mPaddingLeft) * fraction);
            mProperty.mPaddingTop = (int) (startValue.mPaddingTop + (endValue.mPaddingTop - startValue.mPaddingTop) * fraction);
            mProperty.mTextSize = (int) (startValue.mTextSize + (endValue.mTextSize - startValue.mTextSize) * fraction);
            mProperty.mAlpha = (int) (startValue.mAlpha + (endValue.mAlpha - startValue.mAlpha) * fraction);
            return mProperty;
        }
    }

    private final static Property<GeniusEditText, TitleProperty> TITLE_PROPERTY = new Property<GeniusEditText, TitleProperty>(TitleProperty.class, "titleProperty") {
        @Override
        public TitleProperty get(GeniusEditText object) {
            return object.mCurTitleProperty;
        }

        @Override
        public void set(GeniusEditText object, TitleProperty value) {
            object.setTitleProperty(value);
        }
    };

}
