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

import android.content.res.ColorStateList;
import android.content.res.Resources;

import net.qiujuer.genius.R;


public class EditTextAttributes extends BaseAttributes {
    public static final int STYLE_FILL = 1;
    public static final int STYLE_BOX = 2;
    public static final int STYLE_TRANSPARENT = 3;
    public static final int STYLE_LINE = 4;

    private int mStyle = STYLE_FILL;

    private int mTitleTextPaddingLeft;
    private int mTitleTextPaddingTop;
    private int mTitleTextSize;
    private ColorStateList mTitleTextColor;
    private boolean isShowTitle;

    public EditTextAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);

        mTitleTextSize = resources.getDimensionPixelSize(R.dimen.genius_editText_titleTextSize);
        mTitleTextPaddingTop = resources.getDimensionPixelSize(R.dimen.genius_editText_titlePaddingTop);
        mTitleTextPaddingLeft = resources.getDimensionPixelSize(R.dimen.genius_editText_titlePaddingLeft);
    }

    public void setStyle(int style) {
        this.mStyle = style;
    }

    public void setTitleTextColor(int color) {
        this.mTitleTextColor = ColorStateList.valueOf(color);
    }

    public void setTitleTextColor(ColorStateList titleTextColor) {
        this.mTitleTextColor = titleTextColor;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.mTitleTextSize = titleTextSize;
    }

    public void setTitleTextPaddingLeft(int titleTextPaddingLeft) {
        this.mTitleTextPaddingLeft = titleTextPaddingLeft;
    }

    public void setTitleTextPaddingTop(int titleTextPaddingTop) {
        this.mTitleTextPaddingTop = titleTextPaddingTop;
    }

    public void setShowTitle(boolean isShowTitle) {
        this.isShowTitle = isShowTitle;
    }

    public int getStyle() {
        return mStyle;
    }

    public ColorStateList getTitleTextColor() {
        return mTitleTextColor;
    }

    public int getTitleTextSize() {
        return mTitleTextSize;
    }

    public int getTitleTextPaddingLeft() {
        return mTitleTextPaddingLeft;
    }

    public int getTitleTextPaddingTop() {
        return mTitleTextPaddingTop;
    }

    public boolean isShowTitle() {
        return isShowTitle;
    }
}
