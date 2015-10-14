/*
 * Copyright (C) 2015 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 09/28/2015
 * Changed 09/28/2015
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

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import net.qiujuer.genius.ui.R;

/**
 * This is android loading view
 */
public class Loading extends View {
    private Paint mPaint;
    private RectF mOval = new RectF();
    private int mCenterX, mCenterY;
    private int mBorderSize = 4;

    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANIMATION_DURATION = 2500;

    // Animator
    private ObjectAnimator mAnimator;

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
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mBorderSize);


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCheckedState();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX = w >> 1;
        mCenterY = h >> 1;

        int minCenter = Math.min(mCenterX, mCenterY);
        int areRadius = minCenter - ((mBorderSize + 1) >> 1);

        mOval.set(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(getResources().getColor(R.color.amber_500));
        canvas.drawArc(mOval, 0, 360, false, mPaint);

        mPaint.setColor(getResources().getColor(R.color.blue_500));
        canvas.drawArc(mOval, mStartAngle, 60, false, mPaint);

        if (mStartAngle == 360)
            animateCheckedState();
    }

    private int mStartAngle;

    private void setStartAngle(int value) {
        mStartAngle = value;
        invalidate();
        //invalidate((int) mOval.left-1, (int) mOval.top-1, (int) mOval.right+1, (int) mOval.bottom+1);


    }


    /**
     * =============================================================================================
     * The custom properties
     * =============================================================================================
     */

    private void animateCheckedState() {
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofInt(this, ANIM_VALUE, 0, 360);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            //    mAnimator.setAutoCancel(true);
            mAnimator.setDuration(ANIMATION_DURATION);
            mAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
            //mAnimator.setupStartValues();

        } else {
            mAnimator.cancel();
            //mAnimator.setupEndValues();
            //mAnimator.setObjectValues(360);
        }
        mAnimator.start();

    }

    private final static Property<Loading, Integer> ANIM_VALUE = new Property<Loading, Integer>(Integer.class, "startAngle") {
        @Override
        public Integer get(Loading object) {
            return object.mStartAngle;
        }

        @Override
        public void set(Loading object, Integer value) {
            object.setStartAngle(value);
        }
    };
}
