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


import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * This is SeekBar,
 * the bar have a {@link BalloonMarker} to show progress.
 * <p>
 * <p><strong>XML attributes</strong></p>
 * <p>
 * See {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gAllowTrackClickToDrag Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gFont Attributes},
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicator Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicatorBackgroundColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicatorFormatter Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicatorSeparation Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicatorTextAppearance Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gIndicatorTextPadding Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gMax Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gMin Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gMirrorForRtl Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gRippleColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gScrubberColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gScrubberStroke Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gThumbColor Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gThumbSize Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gTickSize Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gTouchSize Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gTrackStroke Attributes}
 * {@link net.qiujuer.genius.ui.R.styleable#AbsSeekBar_gValue Attributes}
 */
public class SeekBar extends AbsSeekBar {
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public SeekBar(Context context) {
        super(context);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekBar's progress level. Also
     * provides notifications of when the user starts and stops a touch gesture within the SeekBar.
     * And provides notifications of when the AbsSeekBar shows/hides the bubble indicator.
     *
     * @param l The seek bar notification listener
     * @see OnSeekBarChangeListener
     */
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    @Override
    protected void onProgressChanged(int scale, boolean fromUser) {
        super.onProgressChanged(scale, fromUser);

        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, scale, fromUser);
        }
    }

    @Override
    protected void onStartTrackingTouch() {
        super.onStartTrackingTouch();
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    @Override
    protected void onStopTrackingTouch() {
        super.onStopTrackingTouch();
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(SeekBar.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(SeekBar.class.getName());
    }

    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar  The GeniusSeekBar
         * @param progress The current progress level. This will be in the range 0..max where max
         *                 was set by {@link AbsSeekBar#setMax(int)}. (The default value for max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the SeekBar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(SeekBar seekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the SeekBar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(SeekBar seekBar);
    }
}
