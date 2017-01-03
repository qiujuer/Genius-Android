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
package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.CircleCheckDrawable;

/**
 * This is CheckBox widget
 * The widget extend view widget
 * <p>
 * <p><strong>XML attributes</strong></p>
 * <p>
 * See {@link net.qiujuer.genius.ui.R.styleable#CheckBox_gBorderSize Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#CheckBox_gFont Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#CheckBox_gIntervalSize Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#CheckBox_gMarkColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#CheckBox_gMarkSize Attributes}
 */
public class CheckBox extends android.widget.CheckBox {
    private CircleCheckDrawable mMarkDrawable;

    public CheckBox(Context context) {
        super(context);
        init(null, R.attr.gCheckBoxStyle, R.style.Genius_Widget_CompoundButton_CheckBox);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.gCheckBoxStyle, R.style.Genius_Widget_CompoundButton_CheckBox);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Genius_Widget_CompoundButton_CheckBox);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CheckBox.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CheckBox.class.getName());
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final Context context = getContext();
        final Resources resource = getResources();
        final float density = resource.getDisplayMetrics().density;
        final int baseSize = (int) (density * 2);

        if (attrs == null) {
            mMarkDrawable = new CircleCheckDrawable(resource.getColorStateList(R.color.g_default_check_box));
            setButtonDrawable(mMarkDrawable);
            return;
        }

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CheckBox, defStyleAttr, defStyleRes);

        int borderSize = a.getDimensionPixelOffset(R.styleable.CheckBox_gBorderSize, baseSize);
        int intervalSize = a.getDimensionPixelOffset(R.styleable.CheckBox_gIntervalSize, baseSize);
        int markSize = a.getDimensionPixelOffset(R.styleable.CheckBox_gMarkSize, -1);
        ColorStateList color = a.getColorStateList(R.styleable.CheckBox_gMarkColor);
        String fontFile = a.getString(R.styleable.CheckBox_gFont);

        a.recycle();

        if (color == null)
            color = resource.getColorStateList(R.color.g_default_check_box);

        boolean isCustom = true;

        if (markSize < 0) {
            markSize = (int) (density * 22);
            isCustom = false;
        }

        mMarkDrawable = new CircleCheckDrawable(color);
        mMarkDrawable.setBorderSize(borderSize);
        mMarkDrawable.setIntervalSize(intervalSize);
        mMarkDrawable.setMarkSize(markSize, isCustom);
        mMarkDrawable.inEditMode(isInEditMode());
        setButtonDrawable(mMarkDrawable);

        // Check for IDE preview render
        if (!this.isInEditMode()) {
            // Font
            if (fontFile != null && fontFile.length() > 0) {
                Typeface typeface = Ui.getFont(getContext(), fontFile);
                if (typeface != null) setTypeface(typeface);
            }
        }
    }

    public void setBorderSize(int size) {
        mMarkDrawable.setBorderSize(size);
    }

    public void setIntervalSize(int size) {
        mMarkDrawable.setIntervalSize(size);
    }

    public void setMarkSize(int size) {
        mMarkDrawable.setMarkSize(size, true);
    }

    public int getBorderSize() {
        return mMarkDrawable.getBorderSize();
    }

    public int getIntervalSize() {
        return mMarkDrawable.getIntervalSize();
    }

    public int getMarkSize() {
        return mMarkDrawable.getMarkSize();
    }

    public void setMarkColor(int color) {
        mMarkDrawable.setColor(color);
    }

    public void setMarkColor(ColorStateList colorList) {
        mMarkDrawable.setColorStateList(colorList);
    }

    public ColorStateList getMarkColor() {
        return mMarkDrawable.getColorStateList();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Refresh display with current params
        refreshDrawableState();
    }
}
