/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/03/2014
 * Changed 01/14/2015
 * Version 1.0.0
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
package net.qiujuer.genius;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by QiuJu
 * on 2014/9/3.
 */
public class Attributes {
    public static final int INVALID = -1;
    public static int DEFAULT_THEME = R.array.StrawberryIce;

    public static final String DEFAULT_FONT_FAMILY = "roboto";
    public static final String DEFAULT_FONT_WEIGHT = "light";
    public static final String DEFAULT_FONT_EXTENSION = "ttf";
    public static final int DEFAULT_TEXT_APPEARANCE = 0;

    public static final int DEFAULT_RADIUS = 0;
    public static final int DEFAULT_BORDER_WIDTH = 0;

    public static final int[] DEFAULT_COLORS = new int[]{
            Color.parseColor("#ffc26165"), Color.parseColor("#ffdb6e77"),
            Color.parseColor("#ffef7e8b"), Color.parseColor("#fff7c2c8"),
            Color.parseColor("#ffc2cbcb"), Color.parseColor("#ffe2e7e7")};

    /**
     * Color related fields
     */
    private int[] colors;
    private int theme = INVALID;

    /**
     * Font related fields
     */
    private String fontFamily = DEFAULT_FONT_FAMILY;
    private String fontWeight = DEFAULT_FONT_WEIGHT;
    private String fontExtension = DEFAULT_FONT_EXTENSION;
    private int textAppearance = DEFAULT_TEXT_APPEARANCE;

    /**
     * Size related fields
     */
    private int radius = DEFAULT_RADIUS;
    private int borderWidth = DEFAULT_BORDER_WIDTH;

    /**
     * Is has own set
     */
    private boolean hasOwnTextColor;
    private boolean hasOwnBackground;

    /**
     * Attribute change listener. Used to redraw the view when attributes are changed.
     */
    private AttributeChangeListener attributeChangeListener;

    public Attributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        this.attributeChangeListener = attributeChangeListener;
        setThemeSilent(DEFAULT_THEME, resources);
    }

    public void initHasOwnAttrs(Context context, AttributeSet attrs) {
        // getting android default tags for textColor and textColorHint
        String textColorAttribute = attrs.getAttributeValue(GeniusUI.androidStyleNameSpace, "textColor");
        if (textColorAttribute == null) {
            int styleId = attrs.getStyleAttribute();
            int[] attributesArray = new int[]{android.R.attr.textColor};

            try {
                TypedArray styleTextColorTypedArray = context.obtainStyledAttributes(styleId, attributesArray);
                // color might have values from the entire integer range, so to find out if there is any color set,
                // checking if default value is returned is not enough. Thus we get color with two different
                // default values - if returned value is the same, it means color is set
                int styleTextColor1 = styleTextColorTypedArray.getColor(0, -1);
                int styleTextColor2 = styleTextColorTypedArray.getColor(0, 1);
                hasOwnTextColor = styleTextColor1 == styleTextColor2;
                styleTextColorTypedArray.recycle();
            } catch (Resources.NotFoundException e) {
                hasOwnTextColor = false;
            }
        } else {
            hasOwnTextColor = true;
        }

        // getting android default tags for background
        String backgroundAttribute = attrs.getAttributeValue(GeniusUI.androidStyleNameSpace, "background");
        hasOwnBackground = backgroundAttribute != null;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme, Resources resources) {
        setThemeSilent(theme, resources);
        attributeChangeListener.onThemeChange();
    }

    public void setThemeSilent(int theme, Resources resources) {
        try {
            this.theme = theme;
            colors = resources.getIntArray(theme);
        } catch (Resources.NotFoundException e) {
            // setting theme blood if exception occurs (especially used for preview rendering by IDE)
            colors = DEFAULT_COLORS;
        }
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        attributeChangeListener.onThemeChange();
    }

    public int getColor(int colorPos) {
        return colors[colorPos];
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        if (fontFamily != null && !fontFamily.equals("") && !fontFamily.equals("null"))
            this.fontFamily = fontFamily;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        if (fontWeight != null && !fontWeight.equals("") && !fontWeight.equals("null"))
            this.fontWeight = fontWeight;
    }

    public String getFontExtension() {
        return fontExtension;
    }

    public void setFontExtension(String fontExtension) {
        if (fontExtension != null && !fontExtension.equals("") && !fontExtension.equals("null"))
            this.fontExtension = fontExtension;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public float[] getOuterRadius() {
        return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getTextAppearance() {
        return textAppearance;
    }

    public void setTextAppearance(int textAppearance) {
        this.textAppearance = textAppearance;
    }

    public boolean isHasOwnBackground() {
        return hasOwnBackground;
    }

    public boolean isHasOwnTextColor() {
        return hasOwnTextColor;
    }

    public interface AttributeChangeListener {
        public void onThemeChange();

        public Attributes getAttributes();
    }
}
