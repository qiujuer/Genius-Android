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
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.shape.BorderShape;

/**
 * TextView this is quickly setup
 * This supper custom font and custom border
 * <p>
 * <p><strong>XML attributes</strong></p>
 * <p>
 * See {@link net.qiujuer.genius.ui.R.styleable#TextView_gBorder Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#TextView_gBorderColor Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#TextView_gBorderSize Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#TextView_gFont Attributes}
 */
@SuppressWarnings("WeakerAccess")
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
        final Resources resources = getResources();
        final float density = resources.getDisplayMetrics().density;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyleAttr, defStyleRes);
        int border = a.getInt(R.styleable.TextView_gBorder, 0);
        int borderSize = a.getDimensionPixelOffset(R.styleable.TextView_gBorderSize, (int) density);
        int borderColor = a.getColor(R.styleable.TextView_gBorderColor, UiCompat.getColor(resources, R.color.g_default_base_secondary));
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
     * Set the border, You can init border size, color and decision that direction contains border
     *
     * @param flag  use {@link #BORDER_ALL } if you need All border,
     *              or {@link #BORDER_LEFT} the view will have Left border
     *              of case you can use eg: {@link #BORDER_LEFT}|{@link #BORDER_BOTTOM}
     *              the view will left and bottom border
     *              <p>
     *              if you not use border you need set the flag to 0
     * @param size  set all border size, unit is px, if you use dp, plx see {@link Ui#dipToPx(Resources, float)} )}
     * @param color set all border color
     */
    public void setBorder(int flag, int size, int color) {
        if (mBorder != flag || mBorderSize != size || mBorderColor != color) {
            mBorder = flag;
            mBorderSize = size;
            mBorderColor = color;

            if (flag <= 0) {
                mBorderDrawable = null;
            } else {
                RectF borderRect;
                if ((flag & BORDER_ALL) == BORDER_ALL)
                    borderRect = new RectF(size, size, size, size);
                else {
                    int l = 0, t = 0, r = 0, b = 0;

                    if ((flag & BORDER_LEFT) == BORDER_LEFT)
                        l = size;
                    if ((flag & BORDER_RIGHT) == BORDER_RIGHT)
                        r = size;
                    if ((flag & BORDER_TOP) == BORDER_TOP)
                        t = size;
                    if ((flag & BORDER_BOTTOM) == BORDER_BOTTOM)
                        b = size;

                    borderRect = new RectF(l, t, r, b);
                }

                if (mBorderDrawable == null) {
                    ShapeDrawable drawable = new ShapeDrawable(new BorderShape(borderRect));
                    Paint paint = drawable.getPaint();
                    paint.setColor(color);
                    drawable.setCallback(this);
                    mBorderDrawable = drawable;
                } else {
                    ShapeDrawable drawable = (ShapeDrawable) mBorderDrawable;
                    Paint paint = drawable.getPaint();
                    paint.setColor(color);
                    BorderShape shape = (BorderShape) drawable.getShape();
                    shape.setBorder(borderRect);
                }
            }

            invalidate();
        }
    }

    /**
     * Get border flag, the include:
     * {@link #BORDER_BOTTOM}
     * {@link #BORDER_LEFT}
     * {@link #BORDER_TOP}
     * {@link #BORDER_RIGHT}
     * {@link #BORDER_ALL}
     *
     * @return border flag
     */
    public int getBorder() {
        return mBorder;
    }

    /**
     * Get all border color
     *
     * @return color value
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * Get all border size
     *
     * @return px value
     */
    public int getBorderSize() {
        return mBorderSize;
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
