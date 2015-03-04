/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/09/2015
 * Changed 02/16/2015
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
package net.qiujuer.genius.widget.attribute;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import net.qiujuer.genius.GeniusUI;


public class BaseAttributes extends Attributes {
    public static final String[] DEFAULT_FONT_FAMILY = new String[]{"roboto", "opensans"};
    public static final String[] DEFAULT_FONT_WEIGHT = new String[]{"bold", "extrabold", "extralight", "light", "regular"};
    public static final String DEFAULT_FONT_EXTENSION = "ttf";
    public static final int DEFAULT_TEXT_APPEARANCE = 0;

    public static final int DEFAULT_RADIUS = 0;
    public static final int DEFAULT_BORDER_WIDTH = 0;

    /**
     * Font related fields
     */
    private String fontFamily = DEFAULT_FONT_FAMILY[0];
    private String fontWeight = DEFAULT_FONT_WEIGHT[3];
    private String fontExtension = DEFAULT_FONT_EXTENSION;
    private int textAppearance = DEFAULT_TEXT_APPEARANCE;

    /**
     * Size related fields
     */
    private float radius = DEFAULT_RADIUS;
    private float[] radiusArray = null;
    private int borderWidth = DEFAULT_BORDER_WIDTH;

    /**
     * Is has own set
     */
    private boolean hasOwnTextColor;
    private boolean hasOwnBackground;
    private boolean hasHintTextColor;


    public BaseAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);
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


        // getting android default tags for textColorHint
        hasHintTextColor = attrs.getAttributeValue(GeniusUI.androidStyleNameSpace, "textColorHint") != null;
    }

    public void initCornerRadius(TypedArray a, int indexRadius, int indexRadiiA, int indexRadiiB, int indexRadiiC, int indexRadiiD) {
        // Set Radius
        setRadius(a.getDimension(indexRadius, DEFAULT_RADIUS));

        // Set Radii[] array
        float rA, rB, rC, rD, r = getRadius();
        rA = a.getDimension(indexRadiiA, r);
        rB = a.getDimension(indexRadiiB, r);
        rC = a.getDimension(indexRadiiC, r);
        rD = a.getDimension(indexRadiiD, r);

        if (rA == r && rB == r && rC == r && rD == r)
            return;

        setRadii(new float[]{rA, rA, rB, rB, rC, rC, rD, rD});
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

    public void setRadius(float radius) {
        if (radius < 0) {
            radius = 0;
        }
        this.radius = radius;
        this.radiusArray = null;
    }

    public void setRadii(float[] radii) {
        radiusArray = radii;
        if (radii == null) {
            radius = 0;
        }
    }

    public float getRadius() {
        return radius;
    }

    public float[] getOuterRadii() {
        if (radiusArray != null)
            return radiusArray;
        return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public boolean isOuterRadiiZero() {
        if (radiusArray == null)
            return radius == 0;
        else {
            boolean isZero = true;
            for (float i : radiusArray) {
                if (i != 0) {
                    isZero = false;
                    break;
                }
            }
            return isZero;
        }
    }

    public float[] getOuterRadiiNull() {
        if (isOuterRadiiZero())
            return null;
        else
            return getOuterRadii();
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

    public void setHasOwnBackground(boolean isHave) {
        hasOwnBackground = isHave;
    }

    public void setHasOwnTextColor(boolean isHave) {
        hasOwnTextColor = isHave;
    }

    public void setHasOwnHintTextColor(boolean isHave) {
        hasHintTextColor = isHave;
    }

    public boolean isHasOwnBackground() {
        return hasOwnBackground;
    }

    public boolean isHasOwnTextColor() {
        return hasOwnTextColor;
    }

    public boolean isHasOwnHintTextColor() {
        return hasHintTextColor;
    }
}
