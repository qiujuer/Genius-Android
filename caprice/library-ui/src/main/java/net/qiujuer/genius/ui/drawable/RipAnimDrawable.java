package net.qiujuer.genius.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Have a circle animation
 */
public class RipAnimDrawable extends RipDrawable implements Animatable {
    // Time
    private static final int ALL_DURATION = ANIMATION_DURATION * 2;

    private Point mPoint = new Point();
    private float mRadius;
    private float mMaxRadius;

    private Interpolator mInterpolator = new DecelerateInterpolator(1.2f);
    private long mStartTime;
    private boolean isFirst = true;
    private boolean isRun = false;

    @Override
    protected void draw(Canvas canvas, Path path, Paint paint) {
        if (isFirst) {
            isFirst = false;
            start();
        } else {
            int sc = canvas.save();
            canvas.clipPath(path);
            canvas.drawCircle(mPoint.x, mPoint.y, mRadius, paint);
            canvas.restoreToCount(sc);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mPoint.set(bounds.left, bounds.top);

        int x = bounds.right - bounds.left;
        int y = bounds.bottom - bounds.top;

        mMaxRadius = (float) Math.sqrt(x * x + y * y);
        mRadius = mMaxRadius;
    }

    protected void onInAnimateUpdate(float factor) {
        mRadius = mMaxRadius * factor;
        invalidateSelf();
    }


    private final Runnable mAnim = new Runnable() {
        @Override
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            long diff = currentTime - mStartTime;
            int duration = ALL_DURATION;
            if (diff <= duration) {
                float interpolation = mInterpolator.getInterpolation((float) diff / (float) duration);
                // Notify
                onInAnimateUpdate(interpolation);

                // Next
                scheduleSelf(this, currentTime + FRAME_DURATION);
            } else {
                unscheduleSelf(this);

                // Notify
                onInAnimateUpdate(1f);

                isRun = false;
            }
        }
    };

    @Override
    public void start() {
        if (isRun) {
            unscheduleSelf(mAnim);
        }
        isRun = true;
        // Start animation
        mStartTime = SystemClock.uptimeMillis() + FRAME_DURATION * 6;
        scheduleSelf(mAnim, mStartTime);
    }

    @Override
    public void stop() {
        unscheduleSelf(mAnim);
    }

    @Override
    public boolean isRunning() {
        return isRun;
    }
}
