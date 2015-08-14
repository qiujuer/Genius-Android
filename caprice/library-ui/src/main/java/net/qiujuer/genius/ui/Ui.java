/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/23/2014
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
package net.qiujuer.genius.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 * Created by QiuJu
 * This is Genius UI Center
 */
public class Ui {
    public static final String androidStyleNameSpace = "http://schemas.android.com/apk/res/android";

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
     * Returns a suitable drawable for ActionBar with theme colors.
     *
     * @param theme selected theme
     * @param dark  boolean for choosing dark colors or primary colors
     * @return drawable to be used in ActionBar
     */
    public static Drawable getActionBarDrawable(Activity activity, int theme, boolean dark) {
        return getActionBarDrawable(activity, theme, dark, 0);
    }

    /**
     * Returns a suitable drawable for ActionBar with theme colors.
     *
     * @param theme        selected theme
     * @param dark         boolean for choosing dark colors or primary colors
     * @param borderBottom bottom border width
     * @return drawable to be used in ActionBar
     */
    public static Drawable getActionBarDrawable(Activity activity, int theme, boolean dark, float borderBottom) {
        int[] colors = activity.getResources().getIntArray(theme);

        int color1 = colors[2];
        int color2 = colors[1];

        if (dark) {
            color1 = colors[1];
            color2 = colors[0];
        }

        borderBottom = dipToPx(activity, borderBottom);

        PaintDrawable front = new PaintDrawable(color1);
        PaintDrawable bottom = new PaintDrawable(color2);
        Drawable[] d = {bottom, front};
        LayerDrawable drawable = new LayerDrawable(d);
        drawable.setLayerInset(1, 0, 0, 0, (int) borderBottom);
        return drawable;
    }


    /**
     * Change Dip to PX
     *
     * @param context Context
     * @param dp      Dip
     * @return PX
     */
    public static float dipToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
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
     * @param context Context
     * @param sp      SP
     * @return PX
     */
    public static float spToPx(Context context, float sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
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
     *
     * @param context Context
     * @param attrs   AttributeSet
     * @return IsEnabled
     */
    public static boolean isEnableAttr(Context context, AttributeSet attrs) {
        return attrs.getAttributeBooleanValue(Ui.androidStyleNameSpace, "enabled", true);
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

        String attributeValue = attrs.getAttributeValue(Ui.androidStyleNameSpace, "background");
        if (attributeValue != null) {
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
     * MotionEventCompat getActionMasked
     *
     * @param event MotionEvent
     * @return ActionMasked
     */
    public static int getActionMasked(MotionEvent event) {
        return event.getAction() & 255;
    }


    /**
     * =============================================================================================
     * Init State List Drawable and Color
     * =============================================================================================
     */

    public static StateListDrawable createStateListDrawable(Drawable drawable[]) {
        if (drawable == null || drawable.length < 4)
            return null;
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, drawable[0]);
        states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, drawable[1]);
        states.addState(new int[]{android.R.attr.state_enabled}, drawable[2]);
        states.addState(new int[]{-android.R.attr.state_enabled}, drawable[3]);
        return states;
    }

    public static ColorStateList createColorStateList(int normal, int unable) {
        int[] colors = new int[]{normal, unable};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }

    public static ColorStateList createColorStateList(int normal, int pressed, int unable) {
        int[] colors = new int[]{pressed, pressed, normal, unable};
        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_focused, android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{-android.R.attr.state_enabled};
        return new ColorStateList(states, colors);
    }


}
