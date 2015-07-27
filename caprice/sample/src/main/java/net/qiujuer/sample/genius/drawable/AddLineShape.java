package net.qiujuer.sample.genius.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by qiujuer on 15/7/22.
 * Line Shape
 */
public class AddLineShape extends Shape {
    private float mCenterX, mCenterY;
    private float mHalfLong;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float topY = mCenterY - mHalfLong;
        float bottomY = mCenterY + mHalfLong;

        canvas.drawLine(mCenterX, topY, mCenterX, bottomY, paint);


        float leftX = mCenterX - mHalfLong;
        float rightX = mCenterX + mHalfLong;

        canvas.drawLine(leftX, mCenterY, rightX, mCenterY, paint);
    }

    @Override
    protected void onResize(float width, float height) {
        mCenterX = width / 2;
        mCenterY = height / 2;

        float minLong = Math.min(width, height);
        mHalfLong = minLong * 0.2f;
    }
}
