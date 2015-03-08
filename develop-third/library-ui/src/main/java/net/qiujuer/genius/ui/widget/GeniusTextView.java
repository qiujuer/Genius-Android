/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/30/2014
 * Changed 02/09/2015
 * Version 2.0.0
 * GeniusEditText
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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import net.qiujuer.genius.ui.GeniusUi;
import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.widget.attribute.Attributes;
import net.qiujuer.genius.ui.widget.attribute.BaseAttributes;
import net.qiujuer.genius.ui.widget.attribute.TextViewAttributes;

/**
 * GeniusTextView this is quickly set up color and theme
 */
public class GeniusTextView extends TextView implements Attributes.AttributeChangeListener {
    private TextViewAttributes mAttributes;

    public GeniusTextView(Context context) {
        super(context);
        init(null);
    }

    public GeniusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GeniusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @SuppressWarnings("deprecation")
    private void init(AttributeSet attrs) {

        if (mAttributes == null)
            mAttributes = new TextViewAttributes(this, getResources());

        if (attrs != null) {

            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusTextView);

            // Getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusTextView_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setTheme(customTheme, getResources());

            mAttributes.setFontFamily(BaseAttributes.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.GeniusTextView_g_fontFamily, 0)]);
            mAttributes.setFontWeight(BaseAttributes.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.GeniusTextView_g_fontWeight, 3)]);
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusTextView_g_fontExtension));
            mAttributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusTextView_g_borderWidth, BaseAttributes.DEFAULT_BORDER_WIDTH));

            // Set init Corners Radius
            mAttributes.initCornerRadius(a, R.styleable.GeniusTextView_g_cornerRadius,
                    R.styleable.GeniusTextView_g_cornerRadii_A, R.styleable.GeniusTextView_g_cornerRadii_B,
                    R.styleable.GeniusTextView_g_cornerRadii_C, R.styleable.GeniusTextView_g_cornerRadii_D);

            // Getting view specific attributes
            mAttributes.setTextColorStyle(a.getInt(R.styleable.GeniusTextView_g_textColor, mAttributes.getTextColorStyle()));
            mAttributes.setBackgroundColorStyle(a.getInt(R.styleable.GeniusTextView_g_backgroundColor, mAttributes.getBackgroundColorStyle()));
            mAttributes.setCustomBackgroundColor(a.getInt(R.styleable.GeniusTextView_g_customBackgroundColor, mAttributes.getCustomBackgroundColor()));

            a.recycle();
        }

        if (!mAttributes.isHasOwnBackground()) {
            // Get Color
            int color = Color.TRANSPARENT;
            if (mAttributes.getBackgroundColorStyle() != Attributes.INVALID) {
                color = mAttributes.getColor(mAttributes.getBackgroundColorStyle());
            } else if (mAttributes.getCustomBackgroundColor() != Attributes.INVALID) {
                color = mAttributes.getCustomBackgroundColor();
            }
            // Check
            if (!(color == Color.TRANSPARENT &&
                    mAttributes.isOuterRadiiZero() &&
                    mAttributes.getBorderWidth() == 0)) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setColor(color);
                gradientDrawable.setCornerRadii(mAttributes.getOuterRadiiNull());
                gradientDrawable.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(mAttributes.getTextColorStyle()));
                setBackgroundDrawable(gradientDrawable);
            }
        }

        // Setting the text color only if there is no android:textColor attribute used
        if (!mAttributes.isHasOwnTextColor())
            setTextColor(mAttributes.getColor(mAttributes.getTextColorStyle()));

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUi.getFont(getContext(), mAttributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    @Override
    public TextViewAttributes getAttributes() {
        return mAttributes;
    }

    @Override
    public void onThemeChange() {
        init(null);
    }
}
