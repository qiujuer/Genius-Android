/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 01/23/2015
 * Changed 01/30/2015
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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;

import static android.graphics.Color.TRANSPARENT;

/**
 * GeniusEditText this have a title from hint
 */
public class GeniusEditText extends EditText implements Attributes.AttributeChangeListener, TextWatcher {
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int THUMB_ANIMATION_DURATION = 255;
    private static final int HINT_PADDING_TOP_DP = 4;
    private static final int HINT_TEXT_SIZE_SP = 10;

    private Attributes mAttributes;
    private int mStyle = 0;

    private TextPaint mHintPaint;
    private TitleProperty mTitleProperty;
    private TitleProperty mCurrentTitleProperty;

    private boolean isHaveText;
    private boolean isShowTitle;
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
            mAttributes = new Attributes(this, getResources());

        TitleProperty titleProperty = null;
        boolean showTitle = isShowTitle;

        if (attrs != null) {
            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusEditText);

            // getting common mAttributes
            int customTheme = a.getResourceId(R.styleable.GeniusEditText_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setThemeSilent(customTheme, getResources());

            mAttributes.setFontFamily(Attributes.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.GeniusEditText_g_fontFamily, 0)]);
            mAttributes.setFontWeight(Attributes.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.GeniusEditText_g_fontWeight, 3)]);
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusEditText_g_fontExtension));

            mAttributes.setTextAppearance(a.getInt(R.styleable.GeniusEditText_g_textAppearance, Attributes.DEFAULT_TEXT_APPEARANCE));
            mAttributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusEditText_g_borderWidth, Attributes.DEFAULT_BORDER_WIDTH));

            // Set init Corners Radius
            mAttributes.initCornerRadius(a, R.styleable.GeniusEditText_g_cornerRadius,
                    R.styleable.GeniusEditText_g_cornerRadii_A, R.styleable.GeniusEditText_g_cornerRadii_B,
                    R.styleable.GeniusEditText_g_cornerRadii_C, R.styleable.GeniusEditText_g_cornerRadii_D);

            // getting view specific mAttributes
            mStyle = a.getInt(R.styleable.GeniusEditText_g_fieldStyle, mStyle);

            // Set HintProperty
            titleProperty = new TitleProperty();
            titleProperty.textSize = a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titleTextSize, (int) GeniusUI.spToPx(getContext(), HINT_TEXT_SIZE_SP));
            titleProperty.paddingLeft = a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titlePaddingLeft, getPaddingLeft());
            titleProperty.paddingTop = a.getDimensionPixelSize(R.styleable.GeniusEditText_g_titlePaddingTop, (int) GeniusUI.spToPx(getContext(), HINT_PADDING_TOP_DP));
            titleProperty.textColor = a.getColor(R.styleable.GeniusEditText_g_titleTextColor, Color.TRANSPARENT);
            showTitle = a.getBoolean(R.styleable.GeniusEditText_g_showTitle, mTitleProperty == null || showTitle);

            a.recycle();
        }

        GradientDrawable backgroundDrawable = null;
        if (!mAttributes.isHasOwnBackground()) {
            backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setCornerRadii(mAttributes.getOuterRadii());
        }

        if (mStyle == 0) {             // fill
            if (!mAttributes.isHasOwnTextColor()) setTextColor(mAttributes.getColor(3));
            if (backgroundDrawable != null) {
                backgroundDrawable.setColor(mAttributes.getColor(2));
                backgroundDrawable.setStroke(0, mAttributes.getColor(2));
            }
        } else if (mStyle == 1) {      // box
            if (!mAttributes.isHasOwnTextColor()) setTextColor(mAttributes.getColor(2));
            if (backgroundDrawable != null) {
                backgroundDrawable.setColor(Color.WHITE);
                backgroundDrawable.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(2));
            }

        } else if (mStyle == 2) {      // transparent
            if (!mAttributes.isHasOwnTextColor()) setTextColor(mAttributes.getColor(1));
            if (backgroundDrawable != null) {
                backgroundDrawable.setColor(TRANSPARENT);
                backgroundDrawable.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(2));
            }
        }

        // Set Background
        if (backgroundDrawable != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                setBackgroundDrawable(backgroundDrawable);
            else
                setBackground(backgroundDrawable);
        }

        // Set HintTextColor
        if (!mAttributes.isHasOwnHintColor()) setHintTextColor(mAttributes.getColor(3));


        // Set TextColor
        if (!mAttributes.isHasOwnTextColor()) {
            if (mAttributes.getTextAppearance() == 1) setTextColor(mAttributes.getColor(0));
            else if (mAttributes.getTextAppearance() == 2) setTextColor(mAttributes.getColor(3));
        }

        // Init Title
        if (titleProperty == null) {
            if (mTitleProperty == null) {
                mTitleProperty = new TitleProperty();
            }
        } else {
            mTitleProperty = titleProperty;
        }
        if (mTitleProperty.textColor == Color.TRANSPARENT)
            mTitleProperty.textColor = getCurrentHintTextColor();
        setShowTitle(showTitle);

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), mAttributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    private void invalidateTitle() {
        // Padding
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (isShowTitle) {
            mCurrentTitleProperty = mTitleProperty.clone();

            // Alpha
            Editable editable = getText();
            if (editable == null || editable.length() <= 0)
                mCurrentTitleProperty.textColor = Color.TRANSPARENT;

            // Set up a default TextPaint object
            if (mHintPaint == null) {
                mHintPaint = new TextPaint();
                mHintPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mHintPaint.setTextAlign(Paint.Align.LEFT);
            }
            mHintPaint.setTextSize(mCurrentTitleProperty.textSize);
            mHintPaint.setColor(mCurrentTitleProperty.textColor);

            // mHintPaint.getFontMetrics().bottom;
            setPadding(paddingLeft, paddingTop + mTitleProperty.textSize, paddingRight, paddingBottom);
        } else {

            mHintPaint = null;
            mCurrentTitleProperty = null;

            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }

    private void showTitle(boolean show) {
        TitleProperty property = mTitleProperty;
        if (!show) {
            property = new TitleProperty();
            property.textColor = getCurrentHintTextColor();
            int a = 0;
            int r = (property.textColor >> 16) & 0xff;
            int g = (property.textColor >> 8) & 0xff;
            int b = property.textColor & 0xff;
            property.textColor = a << 24 | r << 16 | g << 8 | b;
            property.textSize = (int) getTextSize();
            property.paddingLeft = getPaddingLeft();
            property.paddingTop = getPaddingTop();
        }

        if (isAttachWindow) {
            ObjectAnimator circleColorAnimator = ObjectAnimator.ofObject(this, TITLE_PROPERTY, TitleEvaluator.getInstance(), property);
            circleColorAnimator.setDuration(THUMB_ANIMATION_DURATION);
            circleColorAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
            circleColorAnimator.start();
        } else {
            changeTitleProperty(property.clone());
        }
    }

    private void changeTitleProperty(TitleProperty value) {
        mCurrentTitleProperty = value;
        mHintPaint.setTextSize(mCurrentTitleProperty.textSize);
        mHintPaint.setColor(mCurrentTitleProperty.textColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isShowTitle && mHintPaint != null && mCurrentTitleProperty != null) {
            CharSequence buf = getHint();
            if (buf != null) {
                canvas.drawText(buf, 0, buf.length(),
                        mCurrentTitleProperty.paddingLeft,
                        mCurrentTitleProperty.paddingTop + mCurrentTitleProperty.textSize,
                        mHintPaint);
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
        removeTextChangedListener(this);
    }

    @Override
    public void onThemeChange() {
        init(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mHintPaint != null) {
            boolean have = s != null && s.length() > 0;
            if (have != isHaveText) {
                isHaveText = have;
                showTitle(isHaveText);
            }
        }
    }

    public Attributes getAttributes() {
        return mAttributes;
    }

    public TitleProperty getTitleProperty() {
        return mTitleProperty;
    }

    public void setTitleProperty(TitleProperty titleProperty) {
        if (!mTitleProperty.equals(titleProperty)) {
            mTitleProperty = titleProperty.clone();
            invalidateTitle();
        }
    }

    public void setShowTitle(boolean isShow) {
        if (isShow != isShowTitle) {
            isShowTitle = isShow;
            if (isShowTitle)
                addTextChangedListener(this);
            else
                removeTextChangedListener(this);

            invalidateTitle();
        }
    }

    public static class TitleProperty {
        private int paddingLeft;
        private int paddingTop;
        private int textSize;
        private int textColor;

        @Override
        public TitleProperty clone() {
            TitleProperty titleProperty = new TitleProperty();
            titleProperty.paddingLeft = this.paddingLeft;
            titleProperty.paddingTop = this.paddingTop;
            titleProperty.textSize = this.textSize;
            titleProperty.textColor = this.textColor;
            return titleProperty;
        }

        @Override
        public boolean equals(Object o) {
            TitleProperty titleProperty = (TitleProperty) o;
            return titleProperty.paddingLeft == this.paddingLeft
                    && titleProperty.paddingTop == this.paddingTop
                    && titleProperty.textSize == this.textSize
                    && titleProperty.textColor == this.textColor;
        }
    }

    private static class TitleEvaluator implements TypeEvaluator<TitleProperty> {
        private static final TitleEvaluator sInstance = new TitleEvaluator();

        public static TitleEvaluator getInstance() {
            return sInstance;
        }

        @Override
        public TitleProperty evaluate(float fraction, TitleProperty startValue, TitleProperty endValue) {
            TitleProperty value = new TitleProperty();
            // Values
            value.paddingLeft = (int) (startValue.paddingLeft + (endValue.paddingLeft - startValue.paddingLeft) * fraction);
            value.paddingTop = (int) (startValue.paddingTop + (endValue.paddingTop - startValue.paddingTop) * fraction);
            value.textSize = (int) (startValue.textSize + (endValue.textSize - startValue.textSize) * fraction);

            // Color
            int startA = (startValue.textColor >> 24) & 0xff;
            int startR = (startValue.textColor >> 16) & 0xff;
            int startG = (startValue.textColor >> 8) & 0xff;
            int startB = startValue.textColor & 0xff;

            int endA = (endValue.textColor >> 24) & 0xff;
            int endR = (endValue.textColor >> 16) & 0xff;
            int endG = (endValue.textColor >> 8) & 0xff;
            int endB = endValue.textColor & 0xff;

            value.textColor = (startA + (int) (fraction * (endA - startA))) << 24 |
                    (startR + (int) (fraction * (endR - startR))) << 16 |
                    (startG + (int) (fraction * (endG - startG))) << 8 |
                    (startB + (int) (fraction * (endB - startB)));

            return value;
        }
    }

    private static final Property<GeniusEditText, TitleProperty> TITLE_PROPERTY = new Property<GeniusEditText, TitleProperty>(TitleProperty.class, "titleProperty") {
        @Override
        public TitleProperty get(GeniusEditText object) {
            return object.mCurrentTitleProperty;
        }

        @Override
        public void set(GeniusEditText object, TitleProperty value) {
            object.changeTitleProperty(value);
        }
    };
}
