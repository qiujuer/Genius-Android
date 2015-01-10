/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 12/30/2014
 * Changed 01/01/2015
 * Version 2.0.0
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
 * Created by Qiujuer
 * on 2014/12/30.
 */
public class GeniusTextView extends TextView implements Attributes.AttributeChangeListener {
    private Attributes attributes;

    private int textColor = 2;
    private int backgroundColor = Attributes.INVALID;
    private int customBackgroundColor = Attributes.INVALID;

    private boolean hasOwnTextColor;
    private boolean hasOwnBackground;

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

        if (attributes == null)
            attributes = new Attributes(this, getResources());

        if (attrs != null) {

            // getting android default tags for textColor and textColorHint
            String textColorAttribute = attrs.getAttributeValue(GeniusUI.androidStyleNameSpace, "textColor");
            if (textColorAttribute == null) {
                int styleId = attrs.getStyleAttribute();
                int[] attributesArray = new int[]{android.R.attr.textColor};

                if (!this.isInEditMode()) {
                    TypedArray styleTextColorTypedArray = getContext().obtainStyledAttributes(styleId, attributesArray);
                    // color might have values from the entire integer range, so to find out if there is any color set,
                    // checking if default value is returned is not enough. Thus we get color with two different
                    // default values - if returned value is the same, it means color is set
                    int styleTextColor1 = styleTextColorTypedArray.getColor(0, -1);
                    int styleTextColor2 = styleTextColorTypedArray.getColor(0, 1);
                    hasOwnTextColor = styleTextColor1 == styleTextColor2;
                    styleTextColorTypedArray.recycle();
                }
            } else {
                hasOwnTextColor = true;
            }

            // getting android default tags for background
            String backgroundAttribute = attrs.getAttributeValue(GeniusUI.androidStyleNameSpace, "background");
            hasOwnBackground = backgroundAttribute != null;


            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GeniusTextView);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.GeniusTextView_g_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());

            attributes.setFontFamily(a.getString(R.styleable.GeniusTextView_g_fontFamily));
            attributes.setFontWeight(a.getString(R.styleable.GeniusTextView_g_fontWeight));
            attributes.setFontExtension(a.getString(R.styleable.GeniusTextView_g_fontExtension));

            attributes.setRadius(a.getDimensionPixelSize(R.styleable.GeniusTextView_g_cornerRadius, Attributes.DEFAULT_RADIUS));
            attributes.setBorderWidth(a.getDimensionPixelSize(R.styleable.GeniusTextView_g_borderWidth, Attributes.DEFAULT_BORDER_WIDTH));

            // getting view specific attributes
            textColor = a.getInt(R.styleable.GeniusTextView_g_textColor, textColor);
            backgroundColor = a.getInt(R.styleable.GeniusTextView_g_backgroundColor, backgroundColor);
            customBackgroundColor = a.getInt(R.styleable.GeniusTextView_g_customBackgroundColor, customBackgroundColor);

            a.recycle();
        }

        if (!hasOwnBackground) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            if (backgroundColor != Attributes.INVALID) {
                gradientDrawable.setColor(attributes.getColor(backgroundColor));
            } else if (customBackgroundColor != Attributes.INVALID) {
                gradientDrawable.setColor(customBackgroundColor);
            } else {
                gradientDrawable.setColor(Color.TRANSPARENT);
            }
            gradientDrawable.setCornerRadius(attributes.getRadius());
            gradientDrawable.setStroke(attributes.getBorderWidth(), attributes.getColor(textColor));
            setBackgroundDrawable(gradientDrawable);
        }

        // setting the text color only if there is no android:textColor attribute used
        if (!hasOwnTextColor) setTextColor(attributes.getColor(textColor));

        // check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = GeniusUI.getFont(getContext(), attributes);
            if (typeface != null) setTypeface(typeface);
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void onThemeChange() {
        init(null);
    }
}
