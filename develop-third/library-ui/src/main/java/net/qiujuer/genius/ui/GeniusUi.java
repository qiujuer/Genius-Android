/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/13/2014
 * Changed 03/08/2015
 * Version 3.0.0
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
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import net.qiujuer.genius.ui.widget.attribute.Attributes;
import net.qiujuer.genius.ui.widget.attribute.BaseAttributes;

/**
 * Created by QiuJu
 * on 2014/9/3.
 */
public class GeniusUi {
    public static final String androidStyleNameSpace = "http://schemas.android.com/apk/res/android";

    public static final int AQUAMARINE = net.qiujuer.genius.ui.R.array.Aquamarine;
    public static final int SCUBA_BLUE = net.qiujuer.genius.ui.R.array.ScubaBlue;
    public static final int LUCITE_GREEN = net.qiujuer.genius.ui.R.array.LuciteGreen;
    public static final int CLASSIC_BLUE = net.qiujuer.genius.ui.R.array.ClassicBlue;
    public static final int TOASTED_ALMOND = net.qiujuer.genius.ui.R.array.ToastedAlmond;
    public static final int STRAWBERRY_ICE = net.qiujuer.genius.ui.R.array.StrawberryIce;
    public static final int TANGERINE = net.qiujuer.genius.ui.R.array.Tangerine;
    public static final int CUSTARD = net.qiujuer.genius.ui.R.array.Custard;
    public static final int MARSALA = net.qiujuer.genius.ui.R.array.Marsala;
    public static final int GLACIER_GRAY = net.qiujuer.genius.ui.R.array.GlacierGray;
    public static final int DUSK_BLUE = net.qiujuer.genius.ui.R.array.DuskBlue;
    public static final int TREETOP = net.qiujuer.genius.ui.R.array.Treetop;
    public static final int WOODBINE = net.qiujuer.genius.ui.R.array.Woodbine;
    public static final int SANDSTONE = net.qiujuer.genius.ui.R.array.Sandstone;
    public static final int TITANIUM = net.qiujuer.genius.ui.R.array.Titanium;
    public static final int LAVENDER_HERB = net.qiujuer.genius.ui.R.array.LavenderHerb;
    public static final int DARK = net.qiujuer.genius.ui.R.array.Dark;

    /**
     * Creates and returns the font file from given attributes.
     *
     * @param context    Context
     * @param attributes Attributes
     * @return Typeface
     */
    public static Typeface getFont(Context context, BaseAttributes attributes) {
        String fontPath = "fonts/" + attributes.getFontFamily()
                + "_" + attributes.getFontWeight()
                + "." + attributes.getFontExtension();
        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            Log.e("GeniusUI", "Font file at " + fontPath + " cannot be found or the file is " +
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
     * Sets the default theme of the application. The views which doesn't have any theme attribute
     * will have this defined default theme.
     * <p/>
     * IMPORTANT: This method should be called before setContentView method of the activity.
     *
     * @param theme Theme Id
     */
    public static void setDefaultTheme(int theme) {
        Attributes.DEFAULT_THEME = theme;
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
     * @param alpha      You alpha
     * @return Modulate alpha
     */
    public static int modulateAlpha(int colorAlpha, int alpha) {
        int scale = alpha + (alpha >> 7);
        return colorAlpha * scale >> 8;
    }
}
