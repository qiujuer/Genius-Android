/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/28/2015
 * Changed 10/16/2015
 * Version 1.0.0
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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import net.qiujuer.genius.ui.drawable.CircleLoadingDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

/**
 * This is android loading view
 */
public class Loading extends View {
    private LoadingDrawable mDrawable;

    public Loading(Context context) {
        super(context);
        init();
    }

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Loading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Loading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mDrawable = new CircleLoadingDrawable();
        mDrawable.setCallback(this);
    }

    public void start() {
        mDrawable.start();
        mNeedRun = false;
    }

    public void stop() {
        mDrawable.stop();
        mNeedRun = false;
    }

    public boolean isRun() {
        return mDrawable.isRunning();
    }

    public void setBackgroundLineSize(float size) {
        mDrawable.setBackgroundLineSize(size);
    }

    public void setForegroundLineSize(float size) {
        mDrawable.setForegroundLineSize(size);
    }

    public float getBackgroundLineSize() {
        return mDrawable.getBackgroundLineSize();
    }

    public float getForegroundLineSize() {
        return mDrawable.getForegroundLineSize();
    }

    public void setBackgroundColor(int color) {
        mDrawable.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return mDrawable.getBackgroundColor();
    }

    public void setForegroundColor(int color) {
        mDrawable.setForegroundColor(color);
    }


    public void setForegroundColor(int[] colors) {
        mDrawable.setForegroundColor(colors);
    }

    public int[] getForegroundColor() {
        return mDrawable.getForegroundColor();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == h) {
            mDrawable.setBounds(0, 0, w, h);
        } else if (w > h) {
            int offset = (w - h) / 2;
            mDrawable.setBounds(offset, 0, w - offset, h);
        } else if (w < h) {
            int offset = (h - w) / 2;
            mDrawable.setBounds(0, offset, w, h - offset);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
    }

    private boolean mNeedRun;

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        saveOrRecoveryRun(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        saveOrRecoveryRun(visibility);
    }

    private void saveOrRecoveryRun(int visibility) {
        if (visibility == VISIBLE) {
            if (mNeedRun) {
                start();
            }
        } else {
            if (mDrawable.isRunning()) {
                mNeedRun = true;
                mDrawable.stop();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDrawable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDrawable.stop();
    }
}
