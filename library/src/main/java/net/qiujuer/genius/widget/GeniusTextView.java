/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/30/2014
 * Changed 01/30/2015
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
package net.qiujuer.genius.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import net.qiujuer.genius.Attributes;
import net.qiujuer.genius.GeniusUI;
import net.qiujuer.genius.R;

/**
 * GeniusTextView this is quickly set up color and theme
 */
public class GeniusTextView extends TextView implements Attributes.AttributeChangeListener {
    private int mTextColor = 2;
    private int mBackgroundColor = Attributes.INVALID;
    private int mCustomBackgroundColor = Attributes.INVALID;
    private Attributes mAttributes;

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
            mAttributes = new Attributes(this, getResources());

        if (attrs != null) {

            // Set if has own attrs
            mAttributes.initHasOwnAttrs(getContext(), attrs);

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusTextView);

            // Getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusTextView_g_theme, Attributes.DEFAULT_THEME);
            mAttributes.setThemeSilent(customTheme, getResources());

            mAttributes.setFontFamily(Attributes.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.GeniusTextView_g_fontFamily, 0)]);
            mAttributes.setFontWeight(Attributes.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.GeniusTextView_g_fontWeight, 3)]);
            mAttributes.setFontExtension(a.getString(R.styleable.GeniusTextView_g_fontExtension));
            mAttributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusTextView_g_borderWidth, Attributes.DEFAULT_BORDER_WIDTH));

            // Set init Corners Radius
            mAttributes.initCornerRadius(a, R.styleable.GeniusTextView_g_cornerRadius,
                    R.styleable.GeniusTextView_g_cornerRadii_A, R.styleable.GeniusTextView_g_cornerRadii_B,
                    R.styleable.GeniusTextView_g_cornerRadii_C, R.styleable.GeniusTextView_g_cornerRadii_D);

            // Getting view specific attributes
            mTextColor = a.getInt(R.styleable.GeniusTextView_g_textColor, mTextColor);
            mBackgroundColor = a.getInt(R.styleable.GeniusTextView_g_backgroundColor, mBackgroundColor);
            mCustomBackgroundColor = a.getInt(R.styleable.GeniusTextView_g_customBackgroundColor, mCustomBackgroundColor);

            a.recycle();
        }

        if (!mAttributes.isHasOwnBackground()) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            if (mBackgroundColor != Attributes.INVALID) {
                gradientDrawable.setColor(mAttributes.getColor(mBackgroundColor));
            } else if (mCustomBackgroundColor != Attributes.INVALID) {
                gradientDrawable.setColor(mCustomBackgroundColor);
            } else {
                gradientDrawable.setColor(Color.TRANSPARENT);
            }
            gradientDrawable.setCornerRadii(mAttributes.getOuterRadii());
            gradientDrawable.setStroke(mAttributes.getBorderWidth(), mAttributes.getColor(mTextColor));
            setBackgroundDrawable(gradientDrawable);
        }

        // Setting the text color only if there is no android:textColor attribute used
        if (!mAttributes.isHasOwnTextColor()) setTextColor(mAttributes.getColor(mTextColor));

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), mAttributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    @Override
    public Attributes getAttributes() {
        return mAttributes;
    }

    @Override
    public void onThemeChange() {
        init(null);
    }
}
