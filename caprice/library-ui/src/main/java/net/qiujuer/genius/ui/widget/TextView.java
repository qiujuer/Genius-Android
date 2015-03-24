/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/23/2014
 * Changed 03/23/2015
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
package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.widget.attribute.FontAttribute;

/**
 * TextView this is quickly set up
 */
public class TextView extends android.widget.TextView {
    private FontAttribute mFontAttribute;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (mFontAttribute == null)
            mFontAttribute = new FontAttribute();

        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextView);

            mFontAttribute.setFontPath(a.getString(R.styleable.TextView_fontPath));
            if (mFontAttribute.getFontPath() == null) {
                mFontAttribute.setFontFamily(FontAttribute.DEFAULT_FONT_FAMILY[a.getInt(R.styleable.TextView_fontFamily, 0)]);
                mFontAttribute.setFontWeight(FontAttribute.DEFAULT_FONT_WEIGHT[a.getInt(R.styleable.TextView_fontWeight, 3)]);
                mFontAttribute.setFontExtension(a.getString(R.styleable.TextView_fontExtension));
            }
            a.recycle();
        }

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            Typeface typeface = mFontAttribute.getFont(context);
            if (typeface != null) setTypeface(typeface);
        }
    }
}
