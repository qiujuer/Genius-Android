/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 02/04/2015
 * Changed 02/04/2015
 * Version 2.0.0
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
package net.qiujuer.genius.ui.drawable.shape;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

/**
 * This class is draw line on border
 * Line is support dash line
 * This class is extend shape
 * Use to ShapeDrawable class
 */
public class BorderShape extends Shape {
    private RectF mBorder = null;
    private DashPathEffect mPathEffect = null;
    private Path mPath = null;

    public BorderShape(RectF border) {
        this(border, 0, 0);
    }

    public BorderShape(RectF border, float dashWidth, float dashGap) {
        if (border.left != 0 || border.top != 0 || border.right != 0 || border.bottom != 0) {
            mBorder = border;
            if (dashWidth > 0 && dashGap > 0) {
                mPathEffect = new DashPathEffect(new float[]{dashWidth, dashGap}, 0);
                mPath = new Path();
            }
        }
    }

    public void setBorder(RectF border) {
        if (border.left != 0 || border.top != 0 || border.right != 0 || border.bottom != 0) {
            if (mBorder == null)
                mBorder = new RectF(border);
            else
                mBorder.set(border);
        } else
            this.mBorder = null;
    }

    public RectF getBorder(RectF border) {
        if (mBorder != null && border != null)
            border.set(mBorder);
        return border;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mBorder == null)
            return;

        float width = getWidth();
        float height = getHeight();

        if (mPathEffect == null) {

            // left
            if (mBorder.left > 0)
                canvas.drawRect(0, 0, mBorder.left, height, paint);

            // top
            if (mBorder.top > 0)
                canvas.drawRect(0, 0, width, mBorder.top, paint);

            // right
            if (mBorder.right > 0)
                canvas.drawRect(width - mBorder.right, 0, width, height, paint);

            // bottom
            if (mBorder.bottom > 0)
                canvas.drawRect(0, height - mBorder.bottom, width, height, paint);

        } else {
            if (paint.getPathEffect() != mPathEffect) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setPathEffect(mPathEffect);
            }

            // left
            if (mBorder.left > 0) {
                paint.setStrokeWidth(mBorder.left);
                initPath(mBorder.left / 2, 0, mBorder.left / 2, height);
                canvas.drawPath(mPath, paint);
            }

            // top
            if (mBorder.top > 0) {
                paint.setStrokeWidth(mBorder.top);
                initPath(0, mBorder.top / 2, width, mBorder.top / 2);
                canvas.drawPath(mPath, paint);
            }

            // right
            if (mBorder.right > 0) {
                paint.setStrokeWidth(mBorder.right);
                initPath(width - mBorder.right / 2, 0, width - mBorder.right / 2, height);
                canvas.drawPath(mPath, paint);
            }

            // bottom
            if (mBorder.bottom > 0) {
                paint.setStrokeWidth(mBorder.bottom);
                initPath(0, height - mBorder.bottom / 2, width, height - mBorder.bottom / 2);
                canvas.drawPath(mPath, paint);
            }
        }
    }

    private void initPath(float startX, float startY, float endX, float endY) {
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endX, endY);
    }
}
