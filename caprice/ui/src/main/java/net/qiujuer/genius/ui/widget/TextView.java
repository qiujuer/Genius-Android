/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 07/23/2015
 * Changed 08/13/2015
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
package net.qiujuer.genius.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import net.qiujuer.genius.ui.R;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

/**
 * TextView this is quickly setup
 * This supper custom font and custom border
 */
public class TextView extends android.widget.TextView {
    public final static int BORDER_LEFT = 0x0001;
    public final static int BORDER_RIGHT = 0x0010;
    public final static int BORDER_TOP = 0x0100;
    public final static int BORDER_BOTTOM = 0x1000;
    public final static int BORDER_ALL = BORDER_LEFT | BORDER_RIGHT | BORDER_TOP | BORDER_BOTTOM;
    private int mBorder;
    private int mBorderColor;
    private int mBorderSize;
    private Drawable mBorderDrawable;
    private boolean isAttachedToWindow;

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.attr.gTextViewStyle, R.style.Genius_Widget_TextView);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Genius_Widget_TextView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TextView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextView.class.getName());
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null)
            return;

        final Context context = getContext();
        final Resources resource = getResources();
        final float density = resource.getDisplayMetrics().density;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        int border = a.getInt(R.styleable.TextView_gBorder, -1);
        int borderSize = a.getDimensionPixelOffset(R.styleable.TextView_gBorderSize, (int) density);
        int borderColor = a.getColor(R.styleable.TextView_gBorderColor, resource.getColor(R.color.g_default_base_secondary));
        String fontFile = a.getString(R.styleable.TextView_gFont);
        a.recycle();

        setBorder(border, borderSize, borderColor);

        // Check for IDE preview render
        if (!this.isInEditMode() && fontFile != null && fontFile.length() > 0) {
            Typeface typeface = Ui.getFont(getContext(), fontFile);
            if (typeface != null) setTypeface(typeface);
        }
    }

    /**
     * In this init BorderSharp and Drawable
     */
    private void setBorder(int border, int borderSize, int borderColor) {
        mBorder = border;
        mBorderSize = borderSize;
        mBorderColor = borderColor;

        if (mBorder == -1 || mBorder == 0) {
            mBorderDrawable = null;
        } else {
            RectF borderRect;
            if ((border & BORDER_ALL) == BORDER_ALL)
                borderRect = new RectF(borderSize, borderSize, borderSize, borderSize);
            else {
                int l = 0, t = 0, r = 0, b = 0;

                if ((border & BORDER_LEFT) == BORDER_LEFT)
                    l = borderSize;
                if ((border & BORDER_RIGHT) == BORDER_RIGHT)
                    r = borderSize;
                if ((border & BORDER_TOP) == BORDER_TOP)
                    t = borderSize;
                if ((border & BORDER_BOTTOM) == BORDER_BOTTOM)
                    b = borderSize;

                borderRect = new RectF(l, t, r, b);
            }

            if (mBorderDrawable == null) {
                ShapeDrawable drawable = new ShapeDrawable(new BorderShape(borderRect));
                Paint paint = drawable.getPaint();
                paint.setColor(borderColor);
                mBorderDrawable = drawable;
            } else {
                ShapeDrawable drawable = (ShapeDrawable) mBorderDrawable;
                Paint paint = drawable.getPaint();
                paint.setColor(borderColor);
                BorderShape shape = (BorderShape) drawable.getShape();
                shape.setBorder(borderRect);
            }
        }

        if (isAttachedToWindow)
            invalidate();
    }

    public void setBorder(int flag) {
        if (mBorder != flag) {
            mBorder = flag;
            setBorder(mBorder, mBorderSize, mBorderColor);
        }
    }

    public void setBorderSize(int size) {
        if (mBorderSize != size) {
            mBorderSize = size;
            setBorder(mBorder, mBorderSize, mBorderColor);
        }
    }

    public void setBorderColor(int color) {
        if (mBorderColor != color) {
            mBorderColor = color;
            setBorder(mBorder, mBorderSize, mBorderColor);
        }
    }

    public int getBorder() {
        return mBorder;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public int getBorderSize() {
        return mBorderSize;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = mBorderDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mBorderDrawable;
        return (drawable != null && who == drawable) || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = mBorderDrawable;
        if (drawable != null && mBorderSize > 0 && mBorderColor != 0)
            drawable.draw(canvas);
        super.onDraw(canvas);
    }
}
