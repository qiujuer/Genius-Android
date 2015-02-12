/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/09/2015
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
package net.qiujuer.genius.widget.attribute;

import android.content.res.Resources;
import android.graphics.Color;

import net.qiujuer.genius.R;

public class Attributes {
    public static final int INVALID = -1;
    public static int DEFAULT_THEME = R.array.StrawberryIce;
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
     * Attribute change listener. Used to redraw the view when attributes are changed.
     */
    private AttributeChangeListener attributeChangeListener;

    public Attributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        this.attributeChangeListener = attributeChangeListener;
        setTheme(DEFAULT_THEME, resources);
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme, Resources resources) {
        try {
            this.theme = theme;
            colors = resources.getIntArray(theme);
        } catch (Resources.NotFoundException e) {
            // setting theme blood if exception occurs (especially used for preview rendering by IDE)
            colors = DEFAULT_COLORS;
        }
    }

    public void setColors(int[] colors) {
        if (colors == null || colors.length < 6)
            throw new ArrayIndexOutOfBoundsException("colors mast have >= 6 values");
        this.colors = colors;
    }

    public int[] getColors() {
        return colors;
    }

    public int getColor(int colorPos) {
        return colors[colorPos];
    }

    public void notifyAttributeChange() {
        attributeChangeListener.onThemeChange();
    }

    public interface AttributeChangeListener {
        void onThemeChange();

        public Attributes getAttributes();
    }
}
