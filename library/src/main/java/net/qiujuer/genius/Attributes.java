/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 9/3/2014
 * Changed 12/30/2014
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

import android.content.res.Resources;
import android.graphics.Color;

/**
 * Created by QiuJu
 * on 2014/9/3.
 */
public class Attributes {
    public static final int INVALID = -1;

    public static int DEFAULT_THEME = R.array.StrawberryIce;
    public static final int DEFAULT_TOUCH_EFFECT = 0;
    public static final int EASE_TOUCH_EFFECT = 1;
    public static final int RIPPLE_TOUCH_EFFECT = 2;

    public static final String DEFAULT_FONT_FAMILY = "roboto";
    public static final String DEFAULT_FONT_WEIGHT = "light";
    public static final String DEFAULT_FONT_EXTENSION = "ttf";
    public static final int DEFAULT_TEXT_APPEARANCE = 0;

    public static final int DEFAULT_RADIUS_DP = 4;
    public static final int DEFAULT_BORDER_WIDTH_DP = 2;
    public static final int DEFAULT_SIZE_DP = 10;

    public static int DEFAULT_RADIUS_PX = 8;
    public static int DEFAULT_BORDER_WIDTH_PX = 4;
    public static int DEFAULT_SIZE_PX = 20;

    public static final int[] DEFAULT_COLORS = new int[]{
            Color.parseColor("#ffc26165"), Color.parseColor("#ffdb6e77"),
            Color.parseColor("#ffef7e8b"), Color.parseColor("#fff7c2c8"),
            Color.parseColor("#ffc2cbcb"), Color.parseColor("#ffe2e7e7")};

    /**
     * Color related fields
     */
    private int[] colors;
    private int theme = INVALID;
    private int touchEffect = DEFAULT_TOUCH_EFFECT;

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
    private int radius = DEFAULT_RADIUS_PX;
    private int size = DEFAULT_SIZE_PX;
    private int borderWidth = DEFAULT_BORDER_WIDTH_PX;

    /**
     * Attribute change listener. Used to redraw the view when attributes are changed.
     */
    private AttributeChangeListener attributeChangeListener;

    public Attributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        this.attributeChangeListener = attributeChangeListener;
        setThemeSilent(DEFAULT_THEME, resources);
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public int getTouchEffect() {
        return touchEffect;
    }

    public void setTouchEffect(int touchEffect) {
        this.touchEffect = touchEffect;
    }

    public boolean hasTouchEffect() {
        return this.touchEffect != Attributes.DEFAULT_TOUCH_EFFECT;
    }

    public interface AttributeChangeListener {
        public void onThemeChange();

        public Attributes getAttributes();
    }
}
