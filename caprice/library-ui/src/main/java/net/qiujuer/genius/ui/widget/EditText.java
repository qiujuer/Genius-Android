/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/12/2015
 * Changed 08/12/2015
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
package net.qiujuer.genius.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

/**
 * EditText
 * This have a title from hint
 */
public class EditText extends android.widget.EditText {
    private TextPaint mTitlePaint;
    private TextWatcher mTextWatcher;
    private TitleProperty mCurTitleProperty;

    private ObjectAnimator mAnimator;
    private boolean isHaveText;
    private boolean isAttachWindow;

    private int mLineSize;
    private ColorStateList mLineColor;
    private ColorStateList mTitleTextColor;
    private int mTitleTextSize;
    private int mTitleTextPaddingLeft;
    private int mTitleTextPaddingTop;
    private int mTitleStyle;


    public EditText(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.gEditTextStyle, 0);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null)
            return;

        final Context context = getContext();
        final Resources resources = getResources();

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.EditText, defStyleAttr, defStyleRes);

        String fontFile = a.getString(R.styleable.EditText_gFont);
        int lineSize = a.getDimensionPixelSize(R.styleable.EditText_gLineSize, resources.getDimensionPixelSize(R.dimen.genius_editText_lineSize));
        ColorStateList lineColor = a.getColorStateList(R.styleable.EditText_gLineColor);

        // Set HintProperty
        int titleStyle = a.getInt(R.styleable.EditText_gTitle, 1);
        ColorStateList titleTextColor = a.getColorStateList(R.styleable.EditText_gTitleTextColor);
        int titleTextSize = a.getDimensionPixelSize(R.styleable.EditText_gTitleTextSize, resources.getDimensionPixelSize(R.dimen.genius_editText_titleTextSize));
        int titleTextPaddingLeft = a.getDimensionPixelSize(R.styleable.EditText_gTitlePaddingLeft, getPaddingLeft());
        int titleTextPaddingTop = a.getDimensionPixelSize(R.styleable.EditText_gTitlePaddingTop, resources.getDimensionPixelSize(R.dimen.genius_editText_titlePaddingTop));

        a.recycle();

        if (lineColor == null)
            lineColor = resources.getColorStateList(R.color.g_default_edit_view_line);
        if (titleTextColor == null) {
            if (attrs.getAttributeValue(Ui.androidStyleNameSpace, "textColorHint") != null)
                titleTextColor = getHintTextColors();
            else
                titleTextColor = resources.getColorStateList(R.color.g_default_edit_view_title);
        }


        setLineSize(lineSize);
        setLineColor(lineColor);
        setTitleStyle(titleStyle);
        setTitleTextColor(titleTextColor);
        setTitleTextPaddingTop(titleTextPaddingTop);
        setTitleTextPaddingLeft(titleTextPaddingLeft);
        setTitleTextSize(titleTextSize);

        // check for IDE preview render
        if (!this.isInEditMode()) {
            // Font
            if (fontFile != null && fontFile.length() > 0) {
                Typeface typeface = Ui.getFont(context, fontFile);
                if (typeface != null) setTypeface(typeface);
            }
        }

        initBackground();
        initTitleText();
    }

    private void initTitleText() {
        if (isShowTitle()) {
            mCurTitleProperty = new TitleProperty();

            // Set up a default TextPaint object
            if (mTitlePaint == null) {
                mTitlePaint = new TextPaint();
                mTitlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
                mTitlePaint.setTextAlign(Paint.Align.LEFT);
                mTitlePaint.setTypeface(getTypeface());
            }

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
                        if (isShowTitle() && mTitlePaint != null) {
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
        }

        // Padding
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    private void initBackground() {
        final int lineSize = getLineSize();
        if (lineSize == 0)
            return;

        // Creating normal state drawable
        ShapeDrawable normal = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, lineSize)));
        normal.getPaint().setColor(getLineColor(new int[]{android.R.attr.state_enabled}));

        // Creating pressed state drawable
        ShapeDrawable pressed = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, lineSize > 0 ? lineSize + getResources().getDimensionPixelSize(R.dimen.genius_editText_lineStyle_selectBorder) : 0)));
        pressed.getPaint().setColor(getLineColor(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}));

        // Creating disabled state drawable
        ShapeDrawable disabled = new ShapeDrawable(new BorderShape(new RectF(0, 0, 0, lineSize / 2), lineSize / 2, lineSize));
        disabled.getPaint().setColor(getLineColor(new int[]{-android.R.attr.state_enabled}));
        // disabled.getPaint().setAlpha(0xA0);

        Drawable[] drawable = new Drawable[]{pressed, normal, disabled};
        Drawable states = Ui.createStateListDrawable(drawable);
        // Set Background
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            //noinspection deprecation
            setBackgroundDrawable(states);
        else
            setBackground(states);

    }

    private boolean isShowTitle() {
        return mTitleStyle != 0;
    }

    public void setLineSize(int lineSize) {
        if (mLineSize != lineSize) {
            this.mLineSize = lineSize;
            invalidate();
        }
    }

    public void setLineColor(ColorStateList lineColor) {
        if (mLineColor != lineColor) {
            this.mLineColor = lineColor;
            invalidate();
        }
    }

    public void setTitleStyle(int titleStyle) {
        if (mTitleStyle != titleStyle) {
            this.mTitleStyle = titleStyle;
            invalidate();
        }
    }

    public void setTitleTextPaddingTop(int titleTextPaddingTop) {
        if (mTitleTextPaddingTop != titleTextPaddingTop) {
            this.mTitleTextPaddingTop = titleTextPaddingTop;
            invalidate();
        }
    }

    public void setTitleTextPaddingLeft(int titleTextPaddingLeft) {
        if (mTitleTextPaddingLeft != titleTextPaddingLeft) {
            this.mTitleTextPaddingLeft = titleTextPaddingLeft;
            invalidate();
        }
    }

    public void setTitleTextSize(int titleTextSize) {
        if (mTitleTextSize != titleTextSize) {
            this.mTitleTextSize = titleTextSize;
            invalidate();
        }
    }

    public void setTitleTextColor(ColorStateList titleTextColor) {
        if (mTitleTextColor != titleTextColor) {
            this.mTitleTextColor = titleTextColor;
            invalidate();
        }
    }

    public int getLineSize() {
        return mLineSize;
    }

    public ColorStateList getLineColor() {
        return mLineColor;
    }

    private int getLineColor(int[] status) {
        ColorStateList colors = getLineColor();
        if (colors == null)
            return 0;
        return colors.getColorForState(status, colors.getDefaultColor());
    }

    public ColorStateList getTitleTextColor() {
        return mTitleTextColor;
    }

    public int getTitleStyle() {
        return mTitleStyle;
    }

    public int getTitleTextPaddingLeft() {
        return mTitleTextPaddingLeft;
    }

    public int getTitleTextPaddingTop() {
        return mTitleTextPaddingTop;
    }

    public int getTitleTextSize() {
        return mTitleTextSize;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (isShowTitle()) {
            top = top + mTitleTextSize;
        }

        super.setPadding(left, top, right, bottom);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onDraw(Canvas canvas) {

        // Draw Title Text
        if (isShowTitle() && mTitlePaint != null && mCurTitleProperty != null && mCurTitleProperty.mAlpha != 0) {
            CharSequence buf = getHint();
            if (buf != null) {
                mTitlePaint.setTextSize(mCurTitleProperty.mTextSize);

                int color = getCurrentTitleTextColor();
                int alpha = Ui.modulateAlpha(Color.alpha(color), mCurTitleProperty.mAlpha);

                if (color != 0 && alpha != 0) {
                    mTitlePaint.setColor(color);
                    mTitlePaint.setAlpha(alpha);

                    canvas.drawText(buf, 0, buf.length(),
                            mCurTitleProperty.mPaddingLeft,
                            mCurTitleProperty.mPaddingTop + mCurTitleProperty.mTextSize,
                            mTitlePaint);
                }
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isAttachWindow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return isAttachWindow;
        else
            return isAttachedToWindow();
    }

    @Override
    public void invalidate() {
        if (isAttachWindow())
            super.invalidate();
    }

    private void setTitleProperty(TitleProperty value) {
        mCurTitleProperty = value;
        invalidate();
    }

    private int getCurrentTitleTextColor() {
        if (getTitleTextColor() == null) {
            ColorStateList colors = getHintTextColors();
            if (colors == null)
                return getCurrentHintTextColor();
            else
                return colors.getColorForState(getDrawableState(), 0);
        } else
            return getTitleTextColor().getColorForState(getDrawableState(), 0);
    }

    /**
     * =============================================================================================
     * The Animate
     * =============================================================================================
     */

    private void animateShowTitle(boolean show) {
        TitleProperty property;
        if (show)
            property = new TitleProperty(mTitleTextPaddingLeft, mTitleTextPaddingTop, mTitleTextSize);
        else {
            property = new TitleProperty();
            property.mAlpha = 0;
            property.mTextSize = (int) getTextSize();
            property.mPaddingLeft = getPaddingLeft();
            property.mPaddingTop = getPaddingTop();
        }

        if (isAttachWindow()) {
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

        public TitleProperty(int left, int top, int size) {
            mPaddingLeft = left;
            mPaddingTop = top;
            mTextSize = size;
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

    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANIMATION_DURATION = 250;

    private final static Property<EditText, TitleProperty> TITLE_PROPERTY = new Property<EditText, TitleProperty>(TitleProperty.class, "titleProperty") {
        @Override
        public TitleProperty get(EditText object) {
            return object.mCurTitleProperty;
        }

        @Override
        public void set(EditText object, TitleProperty value) {
            object.setTitleProperty(value);
        }
    };
}
