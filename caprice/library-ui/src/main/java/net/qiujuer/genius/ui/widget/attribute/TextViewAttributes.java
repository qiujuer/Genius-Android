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
package net.qiujuer.genius.ui.widget.attribute;

import android.content.res.Resources;

public class TextViewAttributes extends BaseAttributes {
    public static final int COLOR_STYLE_DARKER = 0;
    public static final int COLOR_STYLE_DARK = 1;
    public static final int COLOR_STYLE_MAIN = 2;
    public static final int COLOR_STYLE_LIGHT = 3;

    private int mTextColorStyle = COLOR_STYLE_MAIN;
    private int mBackgroundColorStyle = Attributes.INVALID;
    private int mCustomBackgroundColor = Attributes.INVALID;

    public TextViewAttributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        super(attributeChangeListener, resources);
    }

    public int getTextColorStyle() {
        return mTextColorStyle;
    }

    public int getBackgroundColorStyle() {
        return mBackgroundColorStyle;
    }

    public int getCustomBackgroundColor() {
        return mCustomBackgroundColor;
    }

    public void setTextColorStyle(int textColorStyle) {
        this.mTextColorStyle = textColorStyle;
    }

    public void setBackgroundColorStyle(int backgroundColorStyle) {
        this.mBackgroundColorStyle = backgroundColorStyle;
    }

    public void setCustomBackgroundColor(int customBackgroundColor) {
        this.mCustomBackgroundColor = customBackgroundColor;
    }
}
