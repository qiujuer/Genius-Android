/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 08/04/2015
 * Changed 08/04/2015
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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.drawable.BalloonMarkerDrawable;

/**
 * This is a SeekBar BalloonMarker PopupIndicator
 */
public class PopupIndicator {
    private final WindowManager mWindowManager;
    Point screenSize = new Point();
    private boolean mShowing;
    private Floater mPopupView;
    //Outside listener for the DiscreteSeekBar to get MarkerDrawable animation events.
    //The whole chain of events goes this way:
    //MarkerDrawable->Marker->Floater->mListener->DiscreteSeekBar....
    //... phew!
    private BalloonMarkerDrawable.MarkerAnimationListener mListener;
    private int[] mDrawingLocation = new int[2];

    public PopupIndicator(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mPopupView = new Floater(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenSize.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public PopupIndicator(Context context, ColorStateList color, int textAppearanceId, float closeSize, String maxValue) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mPopupView = new Floater(context, color, textAppearanceId, closeSize, maxValue);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenSize.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public void setIndicatorColor(ColorStateList color) {
        dismissComplete();
        if (mPopupView != null) {
            mPopupView.mMarker.setBackgroundColor(color);
        }
    }

    public ColorStateList getIndicatorColor() {
        if (mPopupView != null) {
            return mPopupView.mMarker.getBackgroundColor();
        }
        return null;
    }

    public void setIndicatorTextAppearance(int textAppearanceId) {
        dismissComplete();
        if (mPopupView != null) {
            mPopupView.mMarker.setTextAppearance(textAppearanceId);
        }
    }

    public void setIndicatorClosedSize(float closeSize) {
        dismissComplete();
        if (mPopupView != null) {
            mPopupView.mMarker.setClosedSize(closeSize);
        }
    }

    public void setIndicatorSizes(String maxValue) {
        dismissComplete();
        if (mPopupView != null) {
            mPopupView.mMarker.resetSizes(maxValue);
        }
    }

    public void setListener(BalloonMarkerDrawable.MarkerAnimationListener listener) {
        mListener = listener;
    }

    /**
     * We want the Floater to be full-width because the contents will be moved from side to side.
     * We may/should change this in the future to use just the PARENT View width and/or pass it in the constructor
     */
    private void measureFloater() {
        int specWidth = View.MeasureSpec.makeMeasureSpec(screenSize.x, View.MeasureSpec.EXACTLY);
        int specHeight = View.MeasureSpec.makeMeasureSpec(screenSize.y, View.MeasureSpec.AT_MOST);
        mPopupView.measure(specWidth, specHeight);
    }

    public void setValue(CharSequence value) {
        mPopupView.mMarker.setValue(value);
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void showIndicator(View parent, Point point) {
        if (isShowing()) {
            mPopupView.mMarker.animateOpen();
            return;
        }

        IBinder windowToken = parent.getWindowToken();
        if (windowToken != null) {
            WindowManager.LayoutParams p = createPopupLayout(windowToken);

            p.gravity = Gravity.TOP | GravityCompat.START;
            updateLayoutParamsForPosition(parent, p, point.y);
            mShowing = true;

            translateViewIntoPosition(point.x);
            invokePopup(p);
        }
    }

    public void move(int x) {
        if (!isShowing()) {
            return;
        }
        translateViewIntoPosition(x);
    }

    public void setColors(int startColor, int endColor) {
        mPopupView.setColors(startColor, endColor);
    }

    /**
     * This will start the closing animation of the Marker and call onClosingComplete when finished
     */
    public void dismiss() {
        mPopupView.mMarker.animateClose();
    }

    /**
     * FORCE the popup window to be removed.
     * You typically calls this when the parent view is being removed from the window to avoid a Window Leak
     */
    public void dismissComplete() {
        if (isShowing()) {
            mShowing = false;
            try {
                mWindowManager.removeViewImmediate(mPopupView);
            } finally {
                // Do...
            }
        }
    }

    private void updateLayoutParamsForPosition(View anchor, WindowManager.LayoutParams p, int yOffset) {
        measureFloater();
        int measuredHeight = mPopupView.getMeasuredHeight();
        int paddingBottom = mPopupView.mMarker.getPaddingBottom();
        anchor.getLocationInWindow(mDrawingLocation);
        p.x = 0;
        p.y = mDrawingLocation[1] - measuredHeight + yOffset + paddingBottom;
        p.width = screenSize.x;
        p.height = measuredHeight;
    }

    private void translateViewIntoPosition(final int x) {
        mPopupView.setFloatOffset(x + mDrawingLocation[0]);
    }

    private void invokePopup(WindowManager.LayoutParams p) {
        mWindowManager.addView(mPopupView, p);
        mPopupView.mMarker.animateOpen();
    }

    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        p.setTitle("DiscreteSeekBar Indicator:" + Integer.toHexString(hashCode()));

        return p;
    }

    /**
     * I'm NOT completely sure how all this bitwise things work...
     *
     * @param curFlags Cur Flags
     * @return Flags
     */
    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }

    /**
     * Small FrameLayout class to hold and move the bubble around when requested
     * I wanted to use the {@link BalloonMarker} directly
     * but doing so would make some things harder to implement
     * (like moving the marker around, having the Marker's outline to work, etc)
     */
    private class Floater extends FrameLayout implements BalloonMarkerDrawable.MarkerAnimationListener {
        private BalloonMarker mMarker;
        private int mOffset;

        public Floater(Context context) {
            super(context);
            mMarker = new BalloonMarker(context);
            addView(mMarker, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP));
        }

        public Floater(Context context, ColorStateList color, int textAppearanceId, float closeSize, String maxValue) {
            this(context);

            mMarker.setBackgroundColor(color);
            mMarker.setTextAppearance(textAppearanceId);
            mMarker.setClosedSize(closeSize);
            mMarker.resetSizes(maxValue);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSie = mMarker.getMeasuredHeight();
            setMeasuredDimension(widthSize, heightSie);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int centerDiffX = mMarker.getMeasuredWidth() / 2;
            int offset = (mOffset - centerDiffX);
            mMarker.layout(offset, 0, offset + mMarker.getMeasuredWidth(), mMarker.getMeasuredHeight());
        }

        public void setFloatOffset(int x) {
            mOffset = x;
            int centerDiffX = mMarker.getMeasuredWidth() / 2;
            int offset = (x - centerDiffX);
            mMarker.offsetLeftAndRight(offset - mMarker.getLeft());
            //Without hardware acceleration (or API levels<11), offsetting a view seems to NOT invalidate the proper area.
            //We should calc the proper invalidate Rect but this will be for now...
            if (!(this.isHardwareAccelerated())) {
                invalidate();
            }
        }

        @Override
        public void onClosingComplete() {
            if (mListener != null) {
                mListener.onClosingComplete();
            }
            dismissComplete();
        }

        @Override
        public void onOpeningComplete() {
            if (mListener != null) {
                mListener.onOpeningComplete();
            }
        }

        public void setColors(int startColor, int endColor) {
            mMarker.setColors(startColor, endColor);
        }
    }
}
