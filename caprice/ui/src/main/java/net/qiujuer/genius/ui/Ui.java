/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Author qiujuer
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
package net.qiujuer.genius.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by QiuJu
 * This is Genius UI Center
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Ui {
    public static boolean SUPPER_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private static final String androidStyleNameSpace = "http://schemas.android.com/apk/res/android";

    public static final int TOUCH_PRESS_COLOR = 0x30000000; //black_alpha_48
    public static final int KEY_SHADOW_COLOR = 0x4E000000; //0x1E000000;
    public static final int FILL_SHADOW_COLOR = 0x6D000000; //0x3D000000;
    public static final float X_OFFSET = 0f;
    public static final float Y_OFFSET = 1.75f;
    public static final float SHADOW_RADIUS = 3.5f;
    public static final int SHADOW_ELEVATION = 4;


    public static Typeface getFont(Context context, String fontFile) {
        String fontPath = "fonts/" + fontFile;
        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            Log.e("Genius Ui", "Font file at " + fontPath + " cannot be found or the file is " +
                    "not a valid font file. Please be sure that library assets are included " +
                    "to project. If not, copy assets/fonts folder of the library to your " +
                    "projects assets folder.");
            return null;
        }
    }

    /**
     * Change Dip to PX
     *
     * @param resources Resources
     * @param dp        Dip
     * @return PX
     */
    public static float dipToPx(Resources resources, float dp) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * Change SP to PX
     *
     * @param resources Resources
     * @param sp        SP
     * @return PX
     */
    public static float spToPx(Resources resources, float sp) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    /**
     * Modulate the colorAlpha to new alpha
     *
     * @param colorAlpha Color's alpha
     * @param alpha      Modulate alpha
     * @return Modulate alpha
     */
    public static int modulateAlpha(int colorAlpha, int alpha) {
        int scale = alpha + (alpha >>> 7);  // convert to 0..256
        return colorAlpha * scale >>> 8;
    }

    /**
     * Modulate the color to new alpha
     *
     * @param color Color
     * @param alpha Modulate alpha
     * @return Modulate alpha color
     */
    public static int modulateColorAlpha(int color, int alpha) {
        int colorAlpha = color >>> 24;
        int scale = alpha + (alpha >> 7);
        int newAlpha = colorAlpha * scale >> 8;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return newAlpha << 24 | r << 16 | g << 8 | b;
    }


    /**
     * Change the color to new alpha
     *
     * @param color Color
     * @param alpha New alpha
     * @return New alpha color
     */
    public static int changeColorAlpha(int color, int alpha) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return alpha << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Get the attribute have enabled value
     * Form android styles namespace
     *
     * @param attrs        AttributeSet
     * @param attribute    The attribute to retrieve
     * @param defaultValue What to return if the attribute isn't found
     * @return Resulting value
     */
    public static boolean isTrueFromAttribute(AttributeSet attrs, String attribute, boolean defaultValue) {
        return attrs.getAttributeBooleanValue(Ui.androidStyleNameSpace, attribute, defaultValue);
    }

    /**
     * Retrieve styled attribute information in this Context's theme.  See
     * {@link android.content.res.Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)}
     * for more information.
     *
     * @param context      Context
     * @param attrs        The base set of attribute values.  May be null.
     * @param attr         The desired attributes to be retrieved.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies
     *                     defaults values for the TypedArray.  Can be
     *                     0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the TypedArray,
     *                     used only if defStyleAttr is 0 or can not be found
     *                     in the theme.  Can be 0 to not look for defaults.
     * @param defaultValue Value to return if the attribute is not defined or
     *                     cannot be coerced to an integer.
     * @return Returns a TypedArray holding an array of the attribute values.
     * @see android.content.res.Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)
     */
    @SuppressWarnings("ResourceType")
    public static boolean getBoolFormAttribute(Context context,
                                               AttributeSet attrs,
                                               int attr,
                                               int defStyleAttr,
                                               int defStyleRes,
                                               boolean defaultValue) {
        int[] attrsArray = new int[]{attr};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray, defStyleAttr, defStyleRes);
        boolean ret = defaultValue;
        if (ta.length() > 0) {
            ret = ta.getBoolean(0, ret);
        }
        ta.recycle();
        return ret;
    }

    /**
     * Get Background color if the attr is color value
     *
     * @param context Context
     * @param attrs   AttributeSet
     * @return Color
     */
    public static int getBackgroundColor(Context context, AttributeSet attrs) {
        int color = Color.TRANSPARENT;

        if (isHaveAttribute(attrs, "background")) {
            int styleId = attrs.getStyleAttribute();
            int[] attributesArray = new int[]{android.R.attr.background};

            try {
                TypedArray typedArray = context.obtainStyledAttributes(styleId, attributesArray);
                if (typedArray.length() > 0)
                    color = typedArray.getColor(0, color);
                typedArray.recycle();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }

        return color;
    }

    /**
     * Get color array values form array resource
     *
     * @param resources Resources
     * @param resId     Resources id
     * @return color array
     */
    public static int[] getColorsFromArrayRes(Resources resources, int resId) {
        try {
            @SuppressLint("Recycle")
            TypedArray array = resources.obtainTypedArray(resId);
            if (array.length() > 0) {
                final int len = array.length();
                final int[] colors = new int[len];
                for (int i = 0; i < len; i++) {
                    colors[i] = array.getColor(i, 0);
                }
                return colors;
            }
        } catch (Resources.NotFoundException ignored) {
        }
        return null;
    }

    /**
     * Check the AttributeSet values have a attribute String, on user set the attribute resource.
     * Form android styles namespace
     *
     * @param attrs     AttributeSet
     * @param attribute The attribute to retrieve
     * @return If have the attribute return True
     */
    public static boolean isHaveAttribute(AttributeSet attrs, String attribute) {
        return attrs.getAttributeValue(Ui.androidStyleNameSpace, attribute) != null;
    }

}
